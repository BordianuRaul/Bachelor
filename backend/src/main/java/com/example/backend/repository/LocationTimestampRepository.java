package com.example.backend.repository;

import com.example.backend.model.LocationTimestamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LocationTimestampRepository extends JpaRepository<LocationTimestamp, UUID> {
}
