package com.jhonipereira.libraryapi.api.resource;

import com.jhonipereira.libraryapi.api.dto.BookDTO;
import com.jhonipereira.libraryapi.api.exception.ApiErrors;
import com.jhonipereira.libraryapi.exception.BusinessException;
import com.jhonipereira.libraryapi.model.entity.Book;
import com.jhonipereira.libraryapi.service.BookService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService service;
    private ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper mapper) {
        this.service = service;
        this.modelMapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO dto){
        Book entity = modelMapper.map(dto, Book.class);

        service.save(entity);

        return modelMapper.map(entity, BookDTO.class);

//        return BookDTO
//                .builder()
//                .id(entity.getId())
//                .title(entity.getTitle())
//                .author(entity.getAuthor())
//                .isbn(entity.getIsbn())
//                .build();
    }

    @GetMapping("{id}")
    public BookDTO get(@PathVariable Long id){
        Book book = service.getById(id).get();
        return modelMapper.map(book, BookDTO.class);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationExceptions(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();

        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessException e){
        return new ApiErrors(e);
    }

}
