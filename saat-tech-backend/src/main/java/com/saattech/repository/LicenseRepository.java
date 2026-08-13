package com.saattech.repository;

import com.saattech.entity.License;
import com.saattech.enums.LicenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {

    List<License> findByStatusAndEndDateBefore(LicenseStatus status, LocalDate date);
}
