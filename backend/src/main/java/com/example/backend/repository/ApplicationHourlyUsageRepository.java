package com.example.backend.repository;

import com.example.backend.model.ApplicationHourlyUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationHourlyUsageRepository extends JpaRepository<ApplicationHourlyUsage, UUID> {

    @Query("SELECT MAX(a.date) FROM ApplicationHourlyUsage a WHERE a.user.id = :userId")
    LocalDate findLatestDateByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(DISTINCT ahu.date) FROM ApplicationHourlyUsage ahu " +
            "WHERE ahu.application.id = :appId AND ahu.user.id = :userId AND ahu.date >= :startDate")
    int countUsageDaysInLastSevenDays(@Param("appId") String appId, @Param("userId") UUID userId, @Param("startDate") LocalDate startDate);

    List<ApplicationHourlyUsage> findByApplicationIdAndUserIdAndDateAfter(String applicationId, UUID userId, LocalDate date);

}
