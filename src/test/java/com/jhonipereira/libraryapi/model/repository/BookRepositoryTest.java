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

import java.util.Optional;

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
        Book book = createBook();
        entityManager.persist(book);

        //execution
        boolean exists = repository.existsByIsbn(isbn);

        //verification
        assertThat(exists).isTrue();
    }

    private static Book createBook() {
        return Book.builder().title("King Arthur").isbn("123").author("Arthur").build();
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

    @Test
    @DisplayName("should obtain a book by id")
    public void findByIdTest(){
       Book book =  createBook();
       entityManager.persist(book);

       //exec
        Optional<Book> foundBook = repository.findById(book.getId());

        //verification
        assertThat(foundBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("should save a book")
    public void saveBookTest(){
        Book book = createBook();

        Book savedBook = repository.save(book);

        assertThat(savedBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("should delete a book")
    public void deleteBookTest(){
        Book book = createBook();
        entityManager.persist(book);

        Book found = entityManager.find(Book.class, book.getId() );
        repository.delete(book);

        Book deleted = entityManager.find(Book.class, book.getId() );
        assertThat(deleted).isNull();
    }
}
