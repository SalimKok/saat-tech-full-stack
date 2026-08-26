package com.saattech.repository;

import com.saattech.entity.Trailer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrailerRepository extends JpaRepository<Trailer, Long> {

    List<Trailer> findByContentId(Long contentId);

    void deleteByContentId(Long contentId);

    boolean existsByContentIdAndYoutubeKey(Long contentId, String youtubeKey);
}
