package com.jhonipereira.libraryapi.model.repository;

import com.jhonipereira.libraryapi.model.entity.Book;
import com.jhonipereira.libraryapi.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByBookAndNotReturned(Book book);
}
