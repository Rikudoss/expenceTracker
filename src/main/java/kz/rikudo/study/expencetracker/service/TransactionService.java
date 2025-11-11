package kz.rikudo.study.expencetracker.service;


import kz.rikudo.study.expencetracker.dto.TransactionRequestDTO;
import kz.rikudo.study.expencetracker.dto.TransactionResponseDTO;
import kz.rikudo.study.expencetracker.entity.Transaction;
import kz.rikudo.study.expencetracker.entity.Type;
import kz.rikudo.study.expencetracker.mapper.TransactionMapper;
import kz.rikudo.study.expencetracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TransactionResponseDTO getTransaction(Long id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toDTO)
                .orElse(null);
    }

    public TransactionResponseDTO createTransaction(TransactionRequestDTO dto) {
        Transaction transaction = transactionMapper.toEntity(dto);
        return transactionMapper.toDTO(transactionRepository.save(transaction));
    }

    public void deleteTransaction(Long id) {
        if (transactionRepository.findById(id).isEmpty()) {
            throw new RuntimeException("Transaction not found");
        }
        transactionRepository.deleteById(id);
    }

    public TransactionResponseDTO updateTransaction(Long id, TransactionRequestDTO updatedTransaction) {
        Transaction transaction = transactionRepository.findById(id).orElse(null);
        if (transaction == null) {
            throw new RuntimeException("Transaction not found");
        }
        transaction.setType(updatedTransaction.getType());
        transaction.setAmount(updatedTransaction.getAmount());
        transaction.setCategory(updatedTransaction.getCategory());
        transaction.setDate(updatedTransaction.getDate());
        return transactionMapper.toDTO(transactionRepository.save(transaction));
    }

    public List<TransactionResponseDTO> getFiltered(String category, LocalDate from, LocalDate to) {
        List<Transaction> transactions;


        if (category != null && from != null && to != null) {
            transactions = transactionRepository.findByCategoryAndDateBetween(category, from, to);
        } else if (category != null) {
            transactions = transactionRepository.findByCategory(category);
        } else if (from != null && to != null) {
            transactions = transactionRepository.findByDateBetween(from, to);
        } else {
            transactions = transactionRepository.findAll();
        }
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Map<String, BigDecimal> getMonthlyStatistics() {
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);
        LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());

        List<Transaction> transactions = transactionRepository.findByDateBetween(firstDay, lastDay);

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == Type.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal totalExpence = transactions.stream()
                .filter(t -> t.getType() == Type.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> stats = new HashMap<>();

        stats.put("Income:", totalIncome);
        stats.put("Expence:", totalExpence);
        stats.put("Total:", totalIncome.subtract(totalExpence));

        return stats;
    }
}
