package com.jhonipereira.libraryapi.service;

import com.jhonipereira.libraryapi.model.entity.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class ScheduleService {

    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";
    @Value("${application.mail.dueloans.message}")
    private String message;

    private final LoanService loanService;
    private final EmailService emailService;

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendMailToLateLoans(){
        List<Loan> allDueLoans = loanService.getAllDueLoans();
        List<String> mailList = allDueLoans.stream()
                .map(loan -> loan.getCustomer())
                .collect(Collectors.toList());

        emailService.sendMails(message, mailList);

    }
}
