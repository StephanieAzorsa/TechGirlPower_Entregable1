package com.nttdata.transactionservice.mapper;

import com.nttdata.transactionservice.dto.TransactionResponseDTO;
import com.nttdata.transactionservice.model.Transaction;
import com.nttdata.transactionservice.model.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TransactionMapperTest {

    @InjectMocks
    private TransactionMapper transactionMapper;

    // CP- - toDTO() con todos los campos completos
    @Test
    void toDTO_WithCompleteTransaction_ShouldReturnCompleteDTO() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Transaction transaction = Transaction.builder()
                .id("trans123")
                .transactionType(TransactionType.DEPOSITO)
                .amount(new BigDecimal("100.50"))
                .date(now)
                .sourceAccountId("acc123")
                .destinationAccountId("acc456")
                .build();

        // Act
        TransactionResponseDTO result = transactionMapper.toDTO(transaction);

        // Assert
        assertNotNull(result);
        assertEquals("trans123", result.getId());
        assertEquals(TransactionType.DEPOSITO, result.getTransactionType());
        assertEquals(new BigDecimal("100.50"), result.getAmount());
        assertEquals(now, result.getDate());
        assertEquals("acc123", result.getSourceAccountId());
        assertEquals("acc456", result.getDestinationAccountId());
    }

}
