package com.jhonipereira.libraryapi.model.repository;

import com.jhonipereira.libraryapi.model.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.swing.*;

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

        Book book = Book.builder().build();
        //scenario
        entityManager.persist(book);
        entityManager.persist(loan);

        //execution
        repository.existsByBookAndNotReturned(book);

    }
}
