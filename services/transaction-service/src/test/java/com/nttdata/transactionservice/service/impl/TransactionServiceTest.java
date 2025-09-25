package com.nttdata.transactionservice.service.impl;

import com.nttdata.transactionservice.dto.TransactionRequestDTO;
import com.nttdata.transactionservice.dto.TransactionResponseDTO;
import com.nttdata.transactionservice.dto.TransferRequestDTO;
import com.nttdata.transactionservice.exception.AccountNotFoundException;
import com.nttdata.transactionservice.exception.InsufficientBalanceException;
import com.nttdata.transactionservice.exception.TransactionExecutionException;
import com.nttdata.transactionservice.mapper.TransactionMapper;
import com.nttdata.transactionservice.model.Transaction;
import com.nttdata.transactionservice.model.TransactionType;
import com.nttdata.transactionservice.repository.TransactionRepository;
import com.nttdata.transactionservice.service.TransactionContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionContext transactionContext;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;


    /**
     * CP-TS08 - registerDeposit() - Debe registrar depósito exitosamente
     */
    @Test
    void testRegisterDeposit_Success() {
        TransactionRequestDTO request = new TransactionRequestDTO();
        TransactionResponseDTO responseDTO = TransactionResponseDTO.builder()
                .id("TX100")
                .amount(new BigDecimal("100"))
                .transactionType(null)
                .date(LocalDateTime.now())
                .sourceAccountId("123")
                .destinationAccountId(null)
                .build();

        when(transactionContext.executeStrategy(request, "DEPOSITO"))
                .thenReturn(Mono.just(responseDTO));

        StepVerifier.create(transactionService.registerDeposit(request))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    /**
     * CP-TS09 - registerDeposit() - Debe manejar error del WebCliente correctamente
     */
    @Test
    void testRegisterDeposit_WebClientError() {
        TransactionRequestDTO request = new TransactionRequestDTO();

        WebClientResponseException ex = WebClientResponseException.create(400, "Bad Request", null, null, null);

        when(transactionContext.executeStrategy(request, "DEPOSITO"))
                .thenReturn(Mono.error(ex));

        StepVerifier.create(transactionService.registerDeposit(request))
                .expectError(InsufficientBalanceException.class)
                .verify();
    }

    /**
     * CP-TS10 - registerWithdrawal() - Debe registrar retiro exitosamente
     */
    @Test
    void testRegisterWithdrawal_Success() {
        TransactionRequestDTO request = new TransactionRequestDTO();
        TransactionResponseDTO responseDTO = TransactionResponseDTO.builder()
                .id("TX101")
                .amount(new BigDecimal("50"))
                .transactionType(null)
                .date(LocalDateTime.now())
                .sourceAccountId("123")
                .destinationAccountId(null)
                .build();

        when(transactionContext.executeStrategy(request, "RETIRO"))
                .thenReturn(Mono.just(responseDTO));

        StepVerifier.create(transactionService.registerWithdrawal(request))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    /**
     * CP-TS11 - registerWithdrawal() - Debe manejar error del WebCliente correctamente
     */
    @Test
    void testRegisterWithdrawal_WebClientError() {
        TransactionRequestDTO request = new TransactionRequestDTO();

        WebClientResponseException ex = WebClientResponseException.create(404, "Not Found", null, null, null);

        when(transactionContext.executeStrategy(request, "RETIRO"))
                .thenReturn(Mono.error(ex));

        StepVerifier.create(transactionService.registerWithdrawal(request))
                .expectError(AccountNotFoundException.class)
                .verify();
    }

    /**
     * CP-TS12 - registerTransfer() - Debe registrar transferencia exitosamente
     */
    @Test
    void testRegisterTransfer_Success() {
        TransferRequestDTO request = new TransferRequestDTO();
        TransactionResponseDTO responseDTO = TransactionResponseDTO.builder()
                .id("TX102")
                .amount(new BigDecimal("200"))
                .transactionType(null)
                .date(LocalDateTime.now())
                .sourceAccountId("123")
                .destinationAccountId("456")
                .build();

        when(transactionContext.executeStrategy(request, "TRANSFERENCIA"))
                .thenReturn(Mono.just(responseDTO));

        StepVerifier.create(transactionService.registerTransfer(request))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    // CP-TS13	registerTransfer()	Debe manejar error del WebCliente correctamente
    @Test
    void registerTransfer_ShouldHandleWebClientError_400() {
        // Arrange
        TransferRequestDTO request = TransferRequestDTO.builder()
                .sourceAccountId("acc123")
                .destinationAccountId("acc456")
                .amount(new BigDecimal("100.00"))
                .build();

        WebClientResponseException webClientEx = WebClientResponseException.create(
                400,
                "Bad Request",
                null, null, null
        );

        when(transactionContext.executeStrategy(any(), any()))
                .thenReturn(Mono.error(webClientEx));

        // Act & Assert
        StepVerifier.create(transactionService.registerTransfer(request))
                .expectError(InsufficientBalanceException.class)
                .verify();

        verify(transactionContext).executeStrategy(request, "TRANSFERENCIA");
    }

    // CP-TS14	listTransactions()	Debe listar transacciones exitosamente
    @Test
    void testListTransactions_Success() {
        // Arrange
        Transaction transaction = Transaction.builder()
                .id("trans1")
                .transactionType(TransactionType.DEPOSITO)
                .amount(new BigDecimal("100.00"))
                .build();

        TransactionResponseDTO response = TransactionResponseDTO.builder()
                .id("trans1")
                .transactionType(TransactionType.DEPOSITO)
                .amount(new BigDecimal("100.00"))
                .build();

        when(transactionRepository.findAll()).thenReturn(Flux.just(transaction));
        when(transactionMapper.toDTO(any())).thenReturn(response);

        // Act & Assert
        StepVerifier.create(transactionService.listTransactions())
                .expectNext(response)
                .verifyComplete();

        verify(transactionRepository).findAll();
    }

    // CP-TS15	handleAccountServiceError()	Debe manejar error 400 (saldo insuficiente)
    @Test
    void testHandleAccountServiceError_400() {
        // Arrange
        WebClientResponseException ex = mock(WebClientResponseException.class);
        when(ex.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        // Act
        Throwable result = transactionService.handleAccountServiceError(ex);

        // Assert
        assert result instanceof InsufficientBalanceException;
    }

    // CP-TS16	handleAccountServiceError()	Debe manejar error 404 (cuenta no encontrada)
    @Test
    void testHandleAccountServiceError_404() {
        // Arrange
        WebClientResponseException ex = mock(WebClientResponseException.class);
        when(ex.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);

        // Act
        Throwable result = transactionService.handleAccountServiceError(ex);

        // Assert
        assert result instanceof AccountNotFoundException;
    }

    // CP-TS17	handleAccountServiceError()	Debe manejar otros errores de servicio
    @Test
    void testHandleAccountServiceError_Other() {
        // Arrange
        WebClientResponseException ex = mock(WebClientResponseException.class);
        when(ex.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

        // Act
        Throwable result = transactionService.handleAccountServiceError(ex);

        // Assert
        assert result instanceof TransactionExecutionException;
    }
}
