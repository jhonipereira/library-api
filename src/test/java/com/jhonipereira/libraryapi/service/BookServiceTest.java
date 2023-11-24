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
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("should obtain a book by ID")
    public void getByIdTest(){
        Long id = 11L;
        Book book = createValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        //execution
        Optional<Book> found = service.getById(id);

        //verification
        assertThat( found.isPresent()).isTrue();
        assertThat( found.get().getId()).isEqualTo(id);
        assertThat( found.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat( found.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat( found.get().getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    @DisplayName("should return empty when not found a book by id")
    public void getNonExistentByIdTest(){
        Long id = 11L;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //execution
        Optional<Book> book = service.getById(id);

        //verification
        assertThat( book.isPresent()).isFalse();
    }

    @Test
    @DisplayName("should delete a book")
    public void deleteBookByIdTest(){
        Long id = 11L;
        Book book = createValidBook();
        book.setId(id);
//        Mockito.when(repository.findById(id)).thenReturn(Optional.empty()); v1

        //execution
//        service.delete(book); //v1
        org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> service.delete(book)); //v2

//        Optional<Book> found = service.getById(id); v1

        //verification
        Mockito.verify(repository, Mockito.times(1)).delete(book); //v2
//        assertThat( found.isPresent() ).isFalse(); v1
    }

    @Test
    @DisplayName("should Throw an error when book is null on DELETE")
    public void getErrorOnDeleteByIdTest() throws IllegalArgumentException{
        Book newbook = new Book();
        //execution
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(newbook));

        //verification
        Mockito.verify(repository, Mockito.never()).delete(newbook);
    }

    @Test
    @DisplayName("should Throw an error when book is null on UPDATE")
    public void getErrorOnUpdateByIdTest() throws IllegalArgumentException{
        Book newbook = new Book();
        //execution
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(newbook));

        //verification
        Mockito.verify(repository, Mockito.never()).save(newbook);
    }

    @Test
    @DisplayName("should update a book")
    public void updateBookTest(){
        Long id = 11L;
        Book bookToUpdate = Book.builder().id(id).build();

        Book updated = createValidBook();
        updated.setId(id);

        //
        Mockito.when(repository.save(bookToUpdate)).thenReturn(updated);

        //execution
        Book book = service.update(bookToUpdate);

        //checking
        assertThat(book.getId()).isEqualTo(updated.getId());
        assertThat(book.getTitle()).isEqualTo(updated.getTitle());
        assertThat(book.getIsbn()).isEqualTo(updated.getIsbn());
        assertThat(book.getAuthor()).isEqualTo(updated.getAuthor());
    }

    @Test
    @DisplayName("should filter books by properties")
    public void findBookTest(){
        Book book = createValidBook();

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Book> list = Arrays.asList(book);

        Page<Book> page = new PageImpl<Book>(list, pageRequest, 1);
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Book> result =  service.find(book, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(list);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }
}
