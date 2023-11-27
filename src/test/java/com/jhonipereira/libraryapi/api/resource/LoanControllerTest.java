package com.jhonipereira.libraryapi.api.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhonipereira.libraryapi.api.dto.LoanDTO;
import com.jhonipereira.libraryapi.exception.BusinessException;
import com.jhonipereira.libraryapi.model.entity.Book;
import com.jhonipereira.libraryapi.model.entity.Loan;
import com.jhonipereira.libraryapi.service.BookService;
import com.jhonipereira.libraryapi.service.LoanService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest
{

    static final String LOAN_API = "/api/loans";
    @Autowired
    MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("should do a loan")
    public void createLoanTest() throws Exception{


        LoanDTO dto = LoanDTO.builder().isbn("111").customer("John").build();
        String json = new ObjectMapper().writeValueAsString(dto);
        Long id = 11L;

        Book book = Book.builder().id(id).isbn("111").build();
        Loan loan = Loan.builder().id(id).customer("John").book(book).loanDate(LocalDate.now()).build();

        BDDMockito.given(bookService.getBookByIsbn("111")).willReturn(Optional.of(book));
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("11"));

    }

    @Test
    @DisplayName("should return error when nonexistent book on create action")
    public void invalidIsbnCreateLoanTest() throws Exception {
        LoanDTO dto = LoanDTO.builder().isbn("111").customer("John").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn("111")).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("book not found for informed isbn"));
    }

    @Test
    @DisplayName("should return error when try to loan an already loaned book")
    public void invalidLoanedBookCreateLoanTest() throws Exception {
        LoanDTO dto = LoanDTO.builder().isbn("111").customer("John").build();
        String json = new ObjectMapper().writeValueAsString(dto);
        Long id = 11L;

        Book book = Book.builder().id(id).isbn("111").build();
        BDDMockito.given(bookService.getBookByIsbn("111")).willReturn(Optional.of(book));
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willThrow( new BusinessException("book already loaned") );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("book already loaned"));
    }
}
