package com.saattech.elasticsearch.service;

import com.saattech.elasticsearch.ContentIndex;
import com.saattech.entity.Content;
import com.saattech.specification.dto.ContentFilterDto;
import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;

public interface ContentSearchService {

    Page<ContentIndex> search(String query, ContentFilterDto filter, Pageable pageable);

    void indexContent(Content content);

    void deleteContentIndex(Long contentId);

    void syncAllContents();
}

