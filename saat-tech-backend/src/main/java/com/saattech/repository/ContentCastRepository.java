package com.saattech.repository;

import com.saattech.entity.ContentCast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContentCastRepository extends JpaRepository<ContentCast, Long> {

    @Query("SELECT cc FROM ContentCast cc JOIN FETCH cc.content c LEFT JOIN FETCH c.metadata m WHERE cc.cast.id = :castId")
    List<ContentCast> findByCastIdWithContent(@Param("castId") Long castId);

}