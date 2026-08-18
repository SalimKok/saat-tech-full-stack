package com.saattech.event;

import com.saattech.entity.Content;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ContentSavedEvent extends ApplicationEvent {
    private final Content content;

    public ContentSavedEvent(Object source, Content content) {
        super(source);
        this.content = content;
    }
}
