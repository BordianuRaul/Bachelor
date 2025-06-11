package com.example.backend.service;

import com.example.backend.repository.ApplicationHourlyUsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class ApplicationHourlyUsageService {

    private final ApplicationHourlyUsageRepository applicationHourlyUsageRepository;

    @Autowired
    public ApplicationHourlyUsageService(ApplicationHourlyUsageRepository applicationHourlyUsageRepository, ApplicationHourlyUsageRepository applicationHourlyUsageRepository1) {
        this.applicationHourlyUsageRepository = applicationHourlyUsageRepository1;
    }
    
    public LocalDate getLastUpdateDateForUser(UUID userId) {
        LocalDate lastDate = applicationHourlyUsageRepository.findLatestDateByUserId(userId);
        return lastDate == null ? LocalDate.now().minusDays(7) : lastDate;
    }
}
