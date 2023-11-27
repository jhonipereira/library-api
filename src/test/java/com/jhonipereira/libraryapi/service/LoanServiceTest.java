package com.jhonipereira.libraryapi.service;

import com.jhonipereira.libraryapi.exception.BusinessException;
import com.jhonipereira.libraryapi.model.entity.Book;
import com.jhonipereira.libraryapi.model.entity.Loan;
import com.jhonipereira.libraryapi.model.repository.LoanRepository;
import com.jhonipereira.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {
    LoanService service;

    @MockBean
    LoanRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("should save a loan")
    public void saveLoanTest(){
        Book book = Book.builder().id(11L).build();
        Loan savingLoan = Loan.builder()
                .book(book)
                .customer("John")
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder().id(11L).book(book).customer("John").loanDate(LocalDate.now()).build();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(false); //just to make sure
        when(repository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = service.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }

    @Test
    @DisplayName("should thrown an error on save a loaned book")
    public void saveLoanedBookTest(){
        Book book = Book.builder().id(11L).build();
        Loan savingLoan = Loan.builder()
                .book(book)
                .customer("John")
                .loanDate(LocalDate.now())
                .build();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable exception = catchThrowable(() -> service.save(savingLoan));

        assertThat(exception).isInstanceOf(BusinessException.class)
                .hasMessage("book already loaned");

        verify(repository, never()).save(savingLoan);
    }
}
