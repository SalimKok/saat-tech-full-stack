package com.saattech.repository;

import com.saattech.entity.Metadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataRepository extends JpaRepository<Metadata, Long> {

    boolean existsByImdbID(String imdbID);
}
