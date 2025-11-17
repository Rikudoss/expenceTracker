package kz.rikudo.study.expencetracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;



    public Transaction(Type type, String category, BigDecimal amount, LocalDate date, User user) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.user = user;
    }



}
