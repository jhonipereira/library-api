package com.jhonipereira.libraryapi.model.repository;

import com.jhonipereira.libraryapi.model.entity.Book;
import com.jhonipereira.libraryapi.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static com.jhonipereira.libraryapi.api.resource.BookControllerTest.createNewBookStatic;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanRepository repository;

    @Test
    @DisplayName("should verify if loan exists, and the book is not returned")
    public void existsByBookAndNotReturnedTest(){

        Book book = createNewBookStatic();
        //scenario
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("John").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        //execution
        boolean exists = repository.existsByBookAndNotReturned(book.getId());

        assertThat(exists).isTrue();

    }

    @Test
    @DisplayName("should search loan by book isbn or customer name")
    public void findByBookIsbnOrCustomerTest(){
        //scenario
        Book book = createNewBookStatic();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("John").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        Page<Loan> result = repository.findByBookIsbnOrCustomer("123", "John", PageRequest.of(0, 10));


        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(loan);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("should obtain loans when the loan date is less than or equal the number of days previously defined AND not returned to the library")
    public void findByLoanDateLessThanAndNotReturnedTest(){
        Book book = createNewBookStatic();
        entityManager.persist(book);

        Loan loan = Loan.builder().customerEmail("john@doe.com").book(book).customer("John").loanDate(LocalDate.now().minusDays(5)).build();
        entityManager.persist(loan);

        LocalDate daysAgo = LocalDate.now().minusDays(4);
        List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(daysAgo);

        assertThat(result).hasSize(1).contains(loan);
    }

    @Test
    @DisplayName("should return not found when the loan date is less than or equal the number of days previously defined AND not returned to the library")
    public void notFoundByLoanDateLessThanAndNotReturnedTest(){
        Book book = createNewBookStatic();
        entityManager.persist(book);

        Loan loan = Loan.builder().customerEmail("john@doe.com").book(book).customer("John").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        LocalDate daysAgo = LocalDate.now().minusDays(4);
        List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(daysAgo);

        assertThat(result).isEmpty();
    }
}
