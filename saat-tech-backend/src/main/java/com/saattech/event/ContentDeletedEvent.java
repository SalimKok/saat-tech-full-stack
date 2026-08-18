package com.saattech.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ContentDeletedEvent extends ApplicationEvent {
    private final Long contentId;

    public ContentDeletedEvent(Object source, Long contentId) {
        super(source);
        this.contentId = contentId;
    }
}
