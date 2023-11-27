package com.jhonipereira.libraryapi.api.resource;

import com.jhonipereira.libraryapi.api.dto.LoanDTO;
import com.jhonipereira.libraryapi.model.entity.Book;
import com.jhonipereira.libraryapi.model.entity.Loan;
import com.jhonipereira.libraryapi.service.BookService;
import com.jhonipereira.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService service;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO dto){
//        Book book = bookService.getBookByIsbn(dto.getIsbn()).get();
        Book book = bookService.getBookByIsbn(dto.getIsbn())
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "book not found for informed isbn"));
        Loan entity = Loan.builder().book(book).customer(dto.getCustomer()).loanDate(LocalDate.now()).build();

        Loan saved = service.save(entity);
        return saved.getId();
    }

}
