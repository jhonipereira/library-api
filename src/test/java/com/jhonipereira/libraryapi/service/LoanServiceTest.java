package com.jhonipereira.libraryapi.service;

import com.jhonipereira.libraryapi.api.dto.LoanFilterDTO;
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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

        when(repository.existsByBookAndNotReturned(book.getId())).thenReturn(false); //just to make sure
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

        when(repository.existsByBookAndNotReturned(book.getId())).thenReturn(true);

        Throwable exception = catchThrowable(() -> service.save(savingLoan));

        assertThat(exception).isInstanceOf(BusinessException.class)
                .hasMessage("book already loaned");

        verify(repository, never()).save(savingLoan);
    }

    @Test
    @DisplayName("should obtain the loan info by id")
    public void getLoanDetailsTest(){
        //scenario
        Long id = 1L;

        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

        //execution
        Optional<Loan> result = service.getById(id);

        //verification
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify(repository).findById(id);

    }

    @Test
    @DisplayName("should update a loan")
    public void updateLoanTest(){
        //scenario
        Long id = 1L;
        Loan loan = createLoan();
        loan.setReturned(true);

        when(repository.save(loan)).thenReturn(loan);

        Loan updatedLoan = service.update(loan);

        assertThat(updatedLoan.getReturned()).isTrue();
        verify(repository).save(loan);
    }

    @Test
    @DisplayName("should filter loans by properties")
    public void findLoanTest(){
        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().customer("John").isbn("123").build();

        Loan loan = createLoan();
        loan.setId(11L);

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Loan> list = Arrays.asList(loan);

        Page<Loan> page = new PageImpl<Loan>(list, pageRequest, list.size());
        when(repository.findByBookIsbnOrCustomer(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Loan> result =  service.find(loanFilterDTO, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(list);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    public static Loan createLoan(){
        Book book = Book.builder().id(11L).build();
        return Loan.builder()
                .book(book)
                .customer("John")
                .loanDate(LocalDate.now())
                .build();
    }
}
