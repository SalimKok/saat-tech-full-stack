package com.saattech.listener;

import com.saattech.elasticsearch.service.ContentSearchService;
import com.saattech.event.ContentDeletedEvent;
import com.saattech.event.ContentSavedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticsearchSyncListener {

    private final ContentSearchService contentSearchService;

    @Async
    @EventListener
    public void handleContentSavedEvent(ContentSavedEvent event) {
        log.info("ContentSavedEvent announced! Film is being submitted to ES: {}", event.getContent().getId());
        contentSearchService.indexContent(event.getContent());
    }

    @Async
    @EventListener
    public void handleContentDeletedEvent(ContentDeletedEvent event) {
        log.info("ContentDeletedEvent announced! Movies are being deleted from ES: {}", event.getContentId());
        contentSearchService.deleteContentIndex(event.getContentId());
    }
}
