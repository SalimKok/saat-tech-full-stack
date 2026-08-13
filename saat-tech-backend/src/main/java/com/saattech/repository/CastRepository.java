package com.saattech.repository;

import com.saattech.entity.Cast;
import com.saattech.enums.CastStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CastRepository extends JpaRepository<Cast, Long> {

    Page<Cast> findByStatus(CastStatus status, Pageable pageable);
    Page<Cast> findByNameContainingIgnoreCaseAndStatus(String name, CastStatus status, Pageable pageable);

    Cast findByName(String name);
}
