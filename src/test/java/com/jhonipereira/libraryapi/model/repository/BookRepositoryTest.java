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

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("should return true when a book exists in DB with the ISBN")
    public void returnTrueWhenISBNExists(){
        //scenario
        String isbn = "123";
        Book book = Book.builder().title("King Arthur").isbn("123").author("Arthur").build();
        entityManager.persist(book);

        //execution
        boolean exists = repository.existsByIsbn(isbn);

        //verification
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("should return FALSE when a book does NOT exists in DB with the ISBN")
    public void returnFalseWhenISBNExists(){
        //scenario
        String isbn = "123";

        //execution
        boolean exists = repository.existsByIsbn(isbn);

        //verification
        assertThat(exists).isFalse();
    }
}
