package com.example.backend.service;

import com.example.backend.dto.ApplicationDailyUsageDTO;
import com.example.backend.model.Application;
import com.example.backend.model.ApplicationHourlyUsage;
import com.example.backend.model.User;
import com.example.backend.repository.ApplicationHourlyUsageRepository;
import com.example.backend.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationHourlyUsageService {

    private final ApplicationHourlyUsageRepository applicationHourlyUsageRepository;
    private final ApplicationRepository applicationRepository;

    @Autowired
    public ApplicationHourlyUsageService(ApplicationHourlyUsageRepository applicationHourlyUsageRepository, ApplicationHourlyUsageRepository applicationHourlyUsageRepository1, ApplicationRepository applicationRepository) {
        this.applicationHourlyUsageRepository = applicationHourlyUsageRepository1;
        this.applicationRepository = applicationRepository;
    }
    
    public LocalDate getLastUpdateDateForUser(UUID userId) {
        LocalDate lastDate = applicationHourlyUsageRepository.findLatestDateByUserId(userId);
        return lastDate == null ? LocalDate.now().minusDays(7) : lastDate;
    }

    public void updateAppUsage(User user, List<ApplicationDailyUsageDTO> appsUsages) {
        for(ApplicationDailyUsageDTO currentApplication : appsUsages) {
            Application application = applicationRepository.findByName(currentApplication.getName())
                    .orElse(null);
            final LocalDate date = currentApplication.getDate();
            final List<Integer> hourlyUsages = currentApplication.getHourlyUsageInSeconds();
            for(int hour = 0; hour <= 23; hour++) {
                final int secondsSpent = hourlyUsages.get(hour);
                if(secondsSpent == 0) {
                    continue;
                }
                final ApplicationHourlyUsage applicationHourlyUsage = new ApplicationHourlyUsage(user, application, hour, secondsSpent, date);
                applicationHourlyUsageRepository.save(applicationHourlyUsage);
            }
        }
    }

}
