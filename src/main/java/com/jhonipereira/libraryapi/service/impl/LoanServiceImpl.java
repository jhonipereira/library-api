package com.jhonipereira.libraryapi.service.impl;

import com.jhonipereira.libraryapi.exception.BusinessException;
import com.jhonipereira.libraryapi.model.entity.Loan;
import com.jhonipereira.libraryapi.model.repository.LoanRepository;
import com.jhonipereira.libraryapi.service.LoanService;

public class LoanServiceImpl implements LoanService {
    private final LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {

        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if (repository.existsByBookAndNotReturned(loan.getBook())){
            throw new BusinessException("book already loaned");
        }
        return repository.save(loan);
    }
}
