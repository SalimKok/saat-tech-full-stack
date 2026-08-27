package com.saattech.service.implementation;

import com.saattech.dto.metadata.MetadataRequestDto;
import com.saattech.entity.Metadata;
import com.saattech.mapper.MetadataMapper;
import com.saattech.repository.MetadataRepository;
import com.saattech.service.MetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetadataServiceImpl implements MetadataService {

    private final MetadataRepository metadataRepository;
    private final MetadataMapper metadataMapper;

    @Override
    public Metadata createMetadata(MetadataRequestDto requestDto) {
        Metadata metadata = metadataMapper.toEntity(requestDto);
        return metadataRepository.save(metadata);
    }
    @Override
    public void updateMetadata(Metadata metadata, MetadataRequestDto requestDto) {
        metadataMapper.updateEntityFromDto(requestDto, metadata);
        metadataRepository.save(metadata);
    }
}
