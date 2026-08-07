package com.saattech.elasticsearch.service.impl;

import com.saattech.elasticsearch.ContentIndex;
import com.saattech.elasticsearch.ContentQueryBuilder;
import com.saattech.elasticsearch.SearchExplanationHelper;
import com.saattech.elasticsearch.ContentIndexMapper;
import com.saattech.elasticsearch.repository.ContentElasticsearchRepository;
import com.saattech.elasticsearch.service.ContentSearchService;
import com.saattech.entity.Content;
import com.saattech.enums.EntityStatus;
import com.saattech.repository.ContentRepository;
import com.saattech.specification.dto.ContentFilterDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentSearchServiceImpl implements ContentSearchService {

    private final ContentElasticsearchRepository searchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ContentRepository contentRepository;

    private final ContentQueryBuilder queryBuilder;
    private final SearchExplanationHelper explanationHelper;
    private final ContentIndexMapper indexMapper;

    @Override
    public Page<ContentIndex> search(String query, ContentFilterDto filter, Pageable pageable) {
        log.info("================== ELASTICSEARCH SEARCH START ==================");
        long startTime = System.currentTimeMillis();

        try {
            NativeQuery nativeQuery = queryBuilder.buildSearchQuery(query, filter, pageable);
            List<String> activeFilters = queryBuilder.extractActiveFilterNames(filter);

            SearchHits<ContentIndex> searchHits = elasticsearchOperations.search(nativeQuery, ContentIndex.class);
            List<ContentIndex> contents = explanationHelper.processSearchHits(searchHits, activeFilters);

            long tookMs = System.currentTimeMillis() - startTime;
            log.info("==> [SUCCESS] Found {} items in {} ms", searchHits.getTotalHits(), tookMs);
            return new PageImpl<>(contents, pageable, searchHits.getTotalHits());

        } catch (NoSuchIndexException e) {
            log.warn("==> [WARN] Index not found, syncing database to Elasticsearch...");
            syncAllContents();
            return searchRepository.findAll(pageable);
        } catch (Exception e) {
            log.error("==> [ERROR] Search query failed: {}", e.getMessage(), e);
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
    }

    @Override
    public void indexContent(Content content) {
        if (content == null) return;
        if (content.getStatus() == EntityStatus.DELETED) {
            deleteContentIndex(content.getId());
            return;
        }
        try {
            ContentIndex index = indexMapper.toIndex(content);
            searchRepository.save(index);
            log.info("Content indexed to Elasticsearch successfully with id: {}", content.getId());
        } catch (Exception e) {
            log.error("Failed to index content to Elasticsearch with id: {}", content.getId(), e);
        }
    }

    @Override
    public void deleteContentIndex(Long contentId) {
        try {
            searchRepository.deleteById(contentId);
            log.info("Content deleted from Elasticsearch index with id: {}", contentId);
        } catch (Exception e) {
            log.error("Failed to delete content from Elasticsearch with id: {}", contentId, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void syncAllContents() {
        log.info("Starting bulk synchronization from PostgreSQL to Elasticsearch...");
        List<ContentIndex> indices = contentRepository.findAll().stream()
                .filter(c -> c.getStatus() == EntityStatus.ACTIVE)
                .map(indexMapper::toIndex)
                .collect(Collectors.toList());

        searchRepository.saveAll(indices);
        log.info("Successfully indexed {} contents into Elasticsearch!", indices.size());
    }
}
