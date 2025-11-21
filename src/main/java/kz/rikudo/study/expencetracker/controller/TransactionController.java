package kz.rikudo.study.expencetracker.controller;

import kz.rikudo.study.expencetracker.dto.TransactionRequestDTO;
import kz.rikudo.study.expencetracker.dto.TransactionResponseDTO;
import kz.rikudo.study.expencetracker.entity.Transaction;
import kz.rikudo.study.expencetracker.entity.User;
import kz.rikudo.study.expencetracker.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<TransactionResponseDTO> getTransactions() {
        return transactionService.getAllTransactions();

    }

    @GetMapping("/{transactionId}")
    public TransactionResponseDTO getTransaction(
            @PathVariable("transactionId") long id
    ) {
       return transactionService.getTransaction(id);
    }

    @PostMapping
    public TransactionResponseDTO createTransaction(
            @RequestBody TransactionRequestDTO requestDTO,
            Authentication authentication
    ){
        User user = (User) authentication.getPrincipal();
        return transactionService.createTransaction(requestDTO,user);
    }

    @DeleteMapping("/{transactionId}")
    public void deleteTransaction(
            @PathVariable("transactionId") long id
    ){
        transactionService.deleteTransaction(id);
    }

    @PutMapping("/{transactionId}")
    public TransactionResponseDTO updateTransaction(
            @PathVariable("transactionId") long id
            ,@RequestBody TransactionRequestDTO updatedTransaction
    ){
        return transactionService.updateTransaction(id,updatedTransaction);
    }
    // фильтр для дат и категории
    @GetMapping("/filter")
    public List<TransactionResponseDTO> getTransactionsWithFilters(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false)LocalDate to
            ){
        return transactionService.getFiltered(category,from,to);
    }
    @GetMapping("/statistics/monthly")
    public Map<String, BigDecimal> getMonthlyStatistics() {
        return transactionService.getMonthlyStatistics();
    }




}
