package kz.rikudo.study.expencetracker.dto;

import kz.rikudo.study.expencetracker.entity.Type;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequestDTO {
    private Type type;
    private String category;
    private BigDecimal amount;
    private LocalDate date;
}
