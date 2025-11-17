package kz.rikudo.study.expencetracker.repository;

import kz.rikudo.study.expencetracker.entity.Transaction;
import kz.rikudo.study.expencetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
    List<Transaction> findByUserAndCategory(User user, String category);
    List<Transaction> findByUserAndDateBetween(User user, LocalDate from, LocalDate to);
    List<Transaction> findByUserAndCategoryAndDateBetween(User user, String category, LocalDate from, LocalDate to);
}
