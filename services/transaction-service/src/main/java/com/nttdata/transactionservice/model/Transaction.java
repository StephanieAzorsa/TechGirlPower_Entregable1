package com.nttdata.transactionservice.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@Document(collection = "transactions")
public class Transaction {

    @BsonId
    private String id;

    @NotNull
    private TransactionType transactionType;

    @NotNull
    @Positive
    private Double amount;

    @NotNull
    private LocalDate date;

    @NotNull
    private String sourceAccountId;

    private String destinationAccountId;
}
