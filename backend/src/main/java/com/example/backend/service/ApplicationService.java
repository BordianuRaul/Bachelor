package com.example.backend.service;

import com.example.backend.dto.ApplicationDailyUsageDTO;
import com.example.backend.model.Application;
import com.example.backend.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public void updateApplicationsUsed(List<ApplicationDailyUsageDTO> applications) {
        for(ApplicationDailyUsageDTO currentApplication : applications ) {
            if(!applicationRepository.existsByName(currentApplication.getName())) {
                final String name = currentApplication.getName();
                final String packageName = currentApplication.getPackageName();
                final String category = currentApplication.getCategory();

                Application application = new Application(name, packageName, category);

                applicationRepository.save(application);
            }
        }
    }

}
