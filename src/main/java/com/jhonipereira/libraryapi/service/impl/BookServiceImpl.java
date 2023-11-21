package com.jhonipereira.libraryapi.service.impl;

import com.jhonipereira.libraryapi.exception.BusinessException;
import com.jhonipereira.libraryapi.model.entity.Book;
import com.jhonipereira.libraryapi.model.repository.BookRepository;
import com.jhonipereira.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if (repository.existsByIsbn(book.getIsbn())){
            throw new BusinessException("ISBN already in use.");
        }
        return repository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return Optional.empty();
    }
}
