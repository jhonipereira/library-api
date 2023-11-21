package com.jhonipereira.libraryapi.service;

import com.jhonipereira.libraryapi.exception.BusinessException;
import com.jhonipereira.libraryapi.model.entity.Book;
import com.jhonipereira.libraryapi.model.repository.BookRepository;
import com.jhonipereira.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {
    BookService service;
    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setup(){
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("should save a book")
    public void saveBooksTest(){
        //scenario
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString()) ).thenReturn(false);
        Mockito
                .when(repository.save(book))
                .thenReturn(
                        Book.builder()
                                .id((long)11)
                                .title("Great book")
                                .author("John Doe")
                                .isbn("123")
                                .build()
                );

        //execution
        Book savedBook = service.save(book);

        //verification
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getTitle()).isEqualTo("Great book");
        assertThat(savedBook.getAuthor()).isEqualTo("John Doe");
    }

    private static Book createValidBook() {
        return Book.builder().author("John Doe").title("Great book").isbn("123").build();
    }

    @Test
    @DisplayName("should not save a book with duplicated ISBN")
    public void shouldNotSaveBookWithDuplicatedISBN(){
        //scenario
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString()) ).thenReturn(true);

        //execution
        Throwable exception = Assertions.catchThrowable( () ->  service.save(book) );

        //verification
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("ISBN already in use.");

        //will verify to never execute the end method (save)
        Mockito.verify(repository, Mockito.never()).save(book);


    }
}
