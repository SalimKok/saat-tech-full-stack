package com.saattech.elasticsearch.repository;

import com.saattech.elasticsearch.model.ContentIndex;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Lazy
@Repository
public interface ContentElasticsearchRepository extends ElasticsearchRepository<ContentIndex, Long> {
}
