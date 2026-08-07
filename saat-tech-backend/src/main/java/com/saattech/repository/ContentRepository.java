package com.saattech.repository;

import com.saattech.entity.Content;
import com.saattech.enums.ContentType;
import com.saattech.enums.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long>, JpaSpecificationExecutor<Content> {

    Optional<Content> findByMetadata_ImdbID(String imdbID);

    Optional<Content> findByIdAndStatus(Long id, EntityStatus status);

    Optional<Content> findByParentContent_IdAndContentTypeAndSeasonNo(Long parentId, ContentType contentType, Integer seasonNo);

    Optional<Content> findByParentContent_IdAndContentTypeAndEpisodeNo(Long parentId, ContentType contentType, Integer episodeNo);

}
