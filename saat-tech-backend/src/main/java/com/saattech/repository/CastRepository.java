package com.saattech.repository;

import com.saattech.entity.Cast;
import com.saattech.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CastRepository extends JpaRepository<Cast, Long> {
    List<Cast> findByStatus(EntityStatus status);

    Page<Cast> findByStatus(EntityStatus status, Pageable pageable);
    Page<Cast> findByNameContainingIgnoreCaseAndStatus(String name, EntityStatus status, Pageable pageable);

    Cast findByName(String name);
}
