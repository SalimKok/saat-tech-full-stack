package com.saattech.service;

import com.saattech.dto.metadata.MetadataRequestDto;
import com.saattech.entity.Metadata;

public interface MetadataService {
    Metadata createMetadata(MetadataRequestDto requestDto);
    void updateMetadata(Metadata metadata, MetadataRequestDto requestDto);

}
