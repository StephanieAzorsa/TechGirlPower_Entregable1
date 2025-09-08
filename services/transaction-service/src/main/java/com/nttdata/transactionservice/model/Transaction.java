package com.nttdata.transactionservice.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "transactions")
public class Transaction {

    @BsonId
    private String id;

    @NotNull
    private TransactionType transactionType;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private LocalDateTime date;

    @NotNull
    private String sourceAccountId;

    private String destinationAccountId;

}
