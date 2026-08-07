package com.saattech.elasticsearch.repository;

import com.saattech.elasticsearch.ContentIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ContentElasticsearchRepository extends ElasticsearchRepository<ContentIndex, Long> {
}
