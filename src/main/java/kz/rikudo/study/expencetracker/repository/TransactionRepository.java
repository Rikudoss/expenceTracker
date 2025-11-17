package kz.rikudo.study.expencetracker.repository;

import kz.rikudo.study.expencetracker.entity.Transaction;
import kz.rikudo.study.expencetracker.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
    List<Transaction> findByCategory(String category);

    List<Transaction> findByDateBetween(LocalDate from, LocalDate to);

    List<Transaction> findByType(Type type);

    List<Transaction> findByCategoryAndDateBetween(String category, LocalDate from, LocalDate to);
}
