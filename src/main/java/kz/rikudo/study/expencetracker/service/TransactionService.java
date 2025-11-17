package kz.rikudo.study.expencetracker.service;

import kz.rikudo.study.expencetracker.dto.TransactionRequestDTO;
import kz.rikudo.study.expencetracker.dto.TransactionResponseDTO;
import kz.rikudo.study.expencetracker.entity.Transaction;
import kz.rikudo.study.expencetracker.entity.Type;
import kz.rikudo.study.expencetracker.entity.User;
import kz.rikudo.study.expencetracker.mapper.TransactionMapper;
import kz.rikudo.study.expencetracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    public List<TransactionResponseDTO> getAllTransactions(User user) {
        return transactionRepository.findByUser(user).stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TransactionResponseDTO getTransaction(Long id, User user) {
        return transactionRepository.findById(id)
                .filter(transaction -> belongsToUser(transaction, user))
                .map(transactionMapper::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
    }

    public TransactionResponseDTO createTransaction(TransactionRequestDTO dto, User user) {
        Transaction transaction = transactionMapper.toEntity(dto);
        transaction.setUser(user);
        return transactionMapper.toDTO(transactionRepository.save(transaction));
    }

    public void deleteTransaction(Long id, User user) {
        Transaction transaction = transactionRepository.findById(id)
                .filter(t -> belongsToUser(t, user))
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        transactionRepository.delete(transaction);
    }

    public TransactionResponseDTO updateTransaction(Long id, TransactionRequestDTO updatedTransaction, User user) {
        Transaction transaction = transactionRepository.findById(id)
                .filter(t -> belongsToUser(t, user))
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        transaction.setType(updatedTransaction.getType());
        transaction.setAmount(updatedTransaction.getAmount());
        transaction.setCategory(updatedTransaction.getCategory());
        transaction.setDate(updatedTransaction.getDate());
        return transactionMapper.toDTO(transactionRepository.save(transaction));
    }

    public List<TransactionResponseDTO> getFiltered(User user, String category, LocalDate from, LocalDate to) {
        List<Transaction> transactions;

        if (category != null && from != null && to != null) {
            transactions = transactionRepository.findByUserAndCategoryAndDateBetween(user, category, from, to);
        } else if (category != null) {
            transactions = transactionRepository.findByUserAndCategory(user, category);
        } else if (from != null && to != null) {
            transactions = transactionRepository.findByUserAndDateBetween(user, from, to);
        } else {
            transactions = transactionRepository.findByUser(user);
        }
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Map<String, BigDecimal> getMonthlyStatistics(User user) {
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);
        LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());

        List<Transaction> transactions = transactionRepository.findByUserAndDateBetween(user, firstDay, lastDay);

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == Type.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> t.getType() == Type.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> stats = new HashMap<>();
        stats.put("Income", totalIncome);
        stats.put("Expense", totalExpense);
        stats.put("Total", totalIncome.subtract(totalExpense));

        return stats;
    }

    private boolean belongsToUser(Transaction transaction, User user) {
        return transaction.getUser() != null && transaction.getUser().getId() != null
                && user != null && user.getId() != null
                && transaction.getUser().getId().equals(user.getId());
    }
}
