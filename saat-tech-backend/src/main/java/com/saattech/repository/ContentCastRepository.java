package com.saattech.repository;

import com.saattech.entity.ContentCast;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentCastRepository extends JpaRepository<ContentCast, Long> {

    @Query(value = "SELECT cc FROM ContentCast cc JOIN FETCH cc.content c LEFT JOIN FETCH c.metadata m WHERE cc.cast.id = :castId",
           countQuery = "SELECT count(cc) FROM ContentCast cc WHERE cc.cast.id = :castId")
    Page<ContentCast> findByCastIdWithContent(@Param("castId") Long castId, Pageable pageable);

}