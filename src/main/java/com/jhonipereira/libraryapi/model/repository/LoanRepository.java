package com.jhonipereira.libraryapi.model.repository;

import com.jhonipereira.libraryapi.model.entity.Book;
import com.jhonipereira.libraryapi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query(value = " select case when (count(l.id) > 0) then true else false end " +
            " from Loan l where l.id_book = :bookId and (l.returned is null or l.returned is false) ", nativeQuery = true)
    boolean existsByBookAndNotReturned( @Param("bookId") Long bookId);

    @Query( value = " select l from Loan l join l.book as b where b.isbn = :isbn or l.customer = :customer")
    Page<Loan> findByBookIsbnOrCustomer(@Param("isbn") String isbn,
                                        @Param("customer") String customer,
                                        Pageable pageable);

    @Query( value = " select * from Loan l where l.loan_date <= :daysAgo and (l.returned is null or l.returned is false) ", nativeQuery = true)
    List<Loan> findByLoanDateLessThanAndNotReturned(@Param("daysAgo") LocalDate daysAgo);

    Page<Loan> findByBook(Book book, Pageable pageable);
}