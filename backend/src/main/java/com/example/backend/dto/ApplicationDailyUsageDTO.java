package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ApplicationDailyUsageDTO {
    public String name;
    public String packageName;
    public String category;
    public LocalDate date;
    public List<Integer> hourlyUsageInSeconds;
}
