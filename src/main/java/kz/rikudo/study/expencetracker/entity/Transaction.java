package kz.rikudo.study.expencetracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @SequenceGenerator(
            name = "transaction_sequence",
            sequenceName = "transaction_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "transaction_sequence"
    )
    private Long id;
    @NotNull(message = "Type must not be null")
    @Enumerated(EnumType.STRING)
    private Type type;

    @NotBlank(message = "Category must not be empty")
    private String category;

    @NotNull(message = "Amount must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @Digits(integer = 12, fraction = 3, message = "Amount must be a valid monetary value")
    private BigDecimal amount;

    @NotNull(message = "Date must not be null")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;


    public Transaction() {
    }

    public Transaction(Long id, Type type, String category, BigDecimal amount, LocalDate date) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }
    public Transaction(Type type, String category, BigDecimal amount, LocalDate date) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }



}
