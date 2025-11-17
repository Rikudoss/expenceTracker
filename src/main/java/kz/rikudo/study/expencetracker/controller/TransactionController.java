package kz.rikudo.study.expencetracker.controller;

import kz.rikudo.study.expencetracker.dto.TransactionRequestDTO;
import kz.rikudo.study.expencetracker.dto.TransactionResponseDTO;
import kz.rikudo.study.expencetracker.entity.User;
import kz.rikudo.study.expencetracker.service.TransactionService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<TransactionResponseDTO> getTransactions(Authentication authentication) {
        User user = extractUser(authentication);
        return transactionService.getAllTransactions(user);
    }

    @GetMapping("/{transactionId}")
    public TransactionResponseDTO getTransaction(@PathVariable("transactionId") long id,
                                                 Authentication authentication) {
        User user = extractUser(authentication);
        return transactionService.getTransaction(id, user);
    }

    @PostMapping
    public TransactionResponseDTO createTransaction(@RequestBody TransactionRequestDTO requestDTO,
                                                    Authentication authentication) {
        User user = extractUser(authentication);
        return transactionService.createTransaction(requestDTO, user);
    }

    @DeleteMapping("/{transactionId}")
    public void deleteTransaction(@PathVariable("transactionId") long id,
                                  Authentication authentication) {
        User user = extractUser(authentication);
        transactionService.deleteTransaction(id, user);
    }

    @PutMapping("/{transactionId}")
    public TransactionResponseDTO updateTransaction(@PathVariable("transactionId") long id,
                                                    @RequestBody TransactionRequestDTO updatedTransaction,
                                                    Authentication authentication) {
        User user = extractUser(authentication);
        return transactionService.updateTransaction(id, updatedTransaction, user);
    }

    @GetMapping("/filter")
    public List<TransactionResponseDTO> getTransactionsWithFilters(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            Authentication authentication) {
        User user = extractUser(authentication);
        return transactionService.getFiltered(user, category, from, to);
    }

    @GetMapping("/statistics/monthly")
    public Map<String, BigDecimal> getMonthlyStatistics(Authentication authentication) {
        User user = extractUser(authentication);
        return transactionService.getMonthlyStatistics(user);
    }

    private User extractUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("Unauthorized");
        }
        return user;
    }
}
