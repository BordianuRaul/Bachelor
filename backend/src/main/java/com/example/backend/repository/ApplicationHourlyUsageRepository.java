package com.example.backend.repository;

import com.example.backend.model.ApplicationHourlyUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface ApplicationHourlyUsageRepository extends JpaRepository<ApplicationHourlyUsage, UUID> {

    @Query("SELECT MAX(a.date) FROM ApplicationHourlyUsage a WHERE a.user.id = :userId")
    LocalDate findLatestDateByUserId(@Param("userId") UUID userId);

}
