package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ApplicationDailyUsageDTO {
    private String name;
    private String packageName;
    private String category;
    private LocalDate date;
    private List<Integer> hourlyUsageInSeconds;
}
