package com.saattech.elasticsearch.service.impl;

import com.saattech.config.SearchProperties;
import com.saattech.elasticsearch.model.ContentIndex;
import com.saattech.elasticsearch.builder.ContentQueryBuilder;
import com.saattech.elasticsearch.helper.ReciprocalRankFusionHelper;
import com.saattech.elasticsearch.mapper.ContentIndexMapper;
import com.saattech.elasticsearch.repository.ContentElasticsearchRepository;
import com.saattech.elasticsearch.service.ContentSearchService;
import com.saattech.elasticsearch.service.EmbeddingService;
import com.saattech.elasticsearch.service.QueryExpansionService;
import com.saattech.entity.Content;
import com.saattech.enums.ContentStatus;
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
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentSearchServiceImpl implements ContentSearchService {

    private final ContentElasticsearchRepository searchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ContentRepository contentRepository;

    private final ContentQueryBuilder queryBuilder;
    private final ReciprocalRankFusionHelper rrfHelper;
    private final ContentIndexMapper indexMapper;
    private final EmbeddingService embeddingService;

    private final SearchProperties searchProperties;
    private final QueryExpansionService queryExpansionService;

    @Override
    public Page<ContentIndex> search(String query, ContentFilterDto filter, Pageable pageable) {
        log.info("================== ASYNC RRF HYBRID SEARCH START ==================");
        long overallStartTime = System.currentTimeMillis();

        long ragStartTime = System.currentTimeMillis();
        String expandedQuery = "";
        if (queryExpansionService.needsExpansion(query)) {
            String expanded = queryExpansionService.expand(query);
            if (expanded != null && !expanded.equals(query)) {
                expandedQuery = expanded;
            }
        }
        long ragTookMs = System.currentTimeMillis() - ragStartTime;

        final String bm25QueryStr = expandedQuery.isEmpty() ? query : query + " " + expandedQuery;
        final String vectorQueryStr = query;

        try {
            boolean hasText = query != null && !query.trim().isEmpty();
            long[] metrics = new long[4];

            int topK = Math.max(searchProperties.getRrf().getK(), (int) pageable.getOffset() + pageable.getPageSize());

            CompletableFuture<List<SearchHit<ContentIndex>>> bm25Future = CompletableFuture.supplyAsync(() -> {
                long start = System.currentTimeMillis();
                try {
                    NativeQuery textQuery = queryBuilder.buildTextQuery(bm25QueryStr, filter, topK);
                    SearchHits<ContentIndex> hits = elasticsearchOperations.search(textQuery, ContentIndex.class);
                    metrics[0] = System.currentTimeMillis() - start;
                    return hits.getSearchHits();
                } catch (Exception e) {
                    log.warn("Async BM25 search skipped due to error: {}", e.getMessage());
                }
                metrics[0] = System.currentTimeMillis() - start;
                return Collections.<SearchHit<ContentIndex>>emptyList();
            });

            CompletableFuture<List<SearchHit<ContentIndex>>> vectorFuture = CompletableFuture.supplyAsync(() -> {
                if (!hasText) {
                    return Collections.<SearchHit<ContentIndex>>emptyList();
                }
                long branchStart = System.currentTimeMillis();
                try {

                    long modelStart = System.currentTimeMillis();
                    List<Float> vector = embeddingService.getEmbedding(vectorQueryStr.trim());
                    metrics[1] = System.currentTimeMillis() - modelStart;
                    if (vector != null && !vector.isEmpty()) {

                        long esVectorStart = System.currentTimeMillis();
                        NativeQuery vectorQuery = queryBuilder.buildVectorQuery(vector, filter, topK);
                        SearchHits<ContentIndex> hits = elasticsearchOperations.search(vectorQuery, ContentIndex.class);
                        metrics[2] = System.currentTimeMillis() - esVectorStart;
                        metrics[3] = System.currentTimeMillis() - branchStart;
                        return hits.getSearchHits();
                    }
                } catch (Exception e) {
                    log.warn("Async vector search skipped due to error: {}", e.getMessage());
                }
                metrics[3] = System.currentTimeMillis() - branchStart;
                return Collections.<SearchHit<ContentIndex>>emptyList();
            });

            CompletableFuture.allOf(bm25Future, vectorFuture).join();
            List<SearchHit<ContentIndex>> bm25Hits = bm25Future.join();
            List<SearchHit<ContentIndex>> vectorHits = vectorFuture.join();

            List<ContentIndex> fusedResults = rrfHelper.fuseResults(bm25Hits, vectorHits, filter);

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), fusedResults.size());
            List<ContentIndex> pageContent = (start <= end && start < fusedResults.size())
                    ? fusedResults.subList(start, end)
                    : Collections.emptyList();
            long totalTookMs = System.currentTimeMillis() - overallStartTime;

            log.info(
                    "==> [PERFORMANCE REPORT] Total: {} ms | LLM (RAG): {} ms | BM25: {} ms | Vector Branch: {} ms (Model: {} ms, ES KNN: {} ms) | Fused Unique Items: {}",
                    totalTookMs, ragTookMs, metrics[0], metrics[3], metrics[1], metrics[2], fusedResults.size());
            return new PageImpl<>(pageContent, pageable, fusedResults.size());
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
        if (content == null)
            return;
        if (content.getStatus() == ContentStatus.DELETED) {
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
                .filter(c -> c.getStatus() != ContentStatus.DELETED)
                .map(indexMapper::toIndex)
                .collect(Collectors.toList());

        searchRepository.saveAll(indices);
        log.info("Successfully indexed {} contents into Elasticsearch!", indices.size());
    }
}
