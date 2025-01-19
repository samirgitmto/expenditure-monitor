package com.expen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTaskService {

    @Autowired
    private EmailParsingService emailService;

    //@Scheduled(fixedRate = 60000) // Runs every 60 seconds
    public void fetchEmails() {
    	System.out.println("scheduler called");
        emailService.fetchEmailsAndProcess();
    }
}
