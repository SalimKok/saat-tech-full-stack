package com.saattech.service.implementation;

import com.saattech.dto.request.ContentCastRequestDto;
import com.saattech.dto.request.ContentRequestDto;
import com.saattech.dto.request.MetadataRequestDto;
import com.saattech.dto.response.ContentResponseDto;
import com.saattech.elasticsearch.service.ContentSearchService;
import com.saattech.entity.Cast;
import com.saattech.entity.Content;
import com.saattech.entity.ContentCast;
import com.saattech.entity.Metadata;
import com.saattech.enums.CastType;
import com.saattech.enums.ContentStatus;
import com.saattech.enums.ContentType;
import com.saattech.enums.LicenseStatus;
import com.saattech.event.ContentDeletedEvent;
import com.saattech.event.ContentSavedEvent;
import com.saattech.exception.DuplicateResourceException;
import com.saattech.mapper.ContentMapper;
import com.saattech.repository.CastRepository;
import com.saattech.repository.ContentRepository;
import com.saattech.exception.ResourceNotFoundException;
import com.saattech.service.ContentService;
import com.saattech.service.LicenseService;
import com.saattech.service.MetadataService;
import com.saattech.specification.builder.ContentSpecificationBuilder;
import com.saattech.specification.dto.ContentFilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final ContentRepository contentRepository;
    private final ContentMapper contentMapper;
    private final CastRepository castRepository;
    private final MetadataService metadataService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Page<ContentResponseDto> getAllContents(ContentFilterDto filterDto, Pageable pageable) {

        Specification<Content> spec = ContentSpecificationBuilder.build(filterDto);

        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "id")
            );
        }

        Page<Content> contentPage = contentRepository.findAll(spec, pageable);
        return contentPage.map(contentMapper::toDto);
    }


    @Override
    public ContentResponseDto getContentById(Long id) {
        Content content = contentRepository.findByIdAndStatusNot(id, ContentStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found! ID: " + id));


        return contentMapper.toDto(content);
    }

    @Transactional
    @Override
    public ContentResponseDto saveContent(ContentRequestDto requestDto) {
        String imdbId = (requestDto.getMetadata() != null) ? requestDto.getMetadata().getImdbID() : null;

        if (imdbId != null && !imdbId.trim().isEmpty()) {
            Optional<Content> existingOpt = contentRepository.findByMetadata_ImdbID(imdbId.trim());
            if (existingOpt.isPresent()) {
                return handleExistingContent(existingOpt.get(), requestDto, "This content already exists!");
            }
        }

        if (requestDto.getParentId() != null && requestDto.getContentType() == ContentType.SEASON && requestDto.getSeasonNo() != null) {
            Optional<Content> existingSeason = contentRepository.findByParentContent_IdAndContentTypeAndSeasonNo(
                    requestDto.getParentId(), ContentType.SEASON, requestDto.getSeasonNo());
            if (existingSeason.isPresent()) {
                return handleExistingContent(existingSeason.get(), requestDto, "Season " + requestDto.getSeasonNo() + " is already active!");
            }
        }

        if (requestDto.getParentId() != null && requestDto.getContentType() == ContentType.EPISODE && requestDto.getEpisodeNo() != null) {
            Optional<Content> existingEpisode = contentRepository.findByParentContent_IdAndContentTypeAndEpisodeNo(
                    requestDto.getParentId(), ContentType.EPISODE, requestDto.getEpisodeNo());
            if (existingEpisode.isPresent()) {
                return handleExistingContent(existingEpisode.get(), requestDto, "Episode " + requestDto.getEpisodeNo() + " is already active!");
            }
        }

        Content parent = null;
        if (requestDto.getParentId() != null) {
            parent = contentRepository.findByIdAndStatusNot(requestDto.getParentId(), ContentStatus.DELETED)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent content not found! ID: " + requestDto.getParentId()));
        }
        Content content = createContentRecursively(requestDto, parent);
        validatePublishStatus(content);
        Content savedContent = contentRepository.save(content);

        applicationEventPublisher.publishEvent(new ContentSavedEvent(this, savedContent));
        return contentMapper.toDto(savedContent);
    }

    private ContentResponseDto handleExistingContent(Content existingContent, ContentRequestDto requestDto, String activeErrorMessage) {
        if (existingContent.getStatus() == ContentStatus.DELETED) {
            throw new DuplicateResourceException(activeErrorMessage);
        }

        if (existingContent.getStatus() == ContentStatus.DELETED) {
            existingContent.setStatus(ContentStatus.DELETED);

            if (requestDto.getMetadata() != null) {
                metadataService.updateMetadata(existingContent.getMetadata(), requestDto.getMetadata());
            }

            Content restoredContent = contentRepository.save(existingContent);
            return contentMapper.toDto(restoredContent);
        }

        return contentMapper.toDto(existingContent);
    }


    @Transactional
    @Override
    public void deleteContent(Long id){
        Content content = contentRepository.findByIdAndStatusNot(id, ContentStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Content to delete not found! ID: " + id));
        softDeleteRecursively(content);

        applicationEventPublisher.publishEvent(new ContentDeletedEvent(this, id));
    }

    private void softDeleteRecursively(Content content) {
        content.setStatus(ContentStatus.DELETED);
        if (content.getSubContents() != null) {
            for (Content child : content.getSubContents()) {
                softDeleteRecursively(child);
            }
        }
        contentRepository.save(content);
    }

    @Transactional
    @Override
    public void addCastToContent(Long contentId, Long castId, CastType role) {
        Content content = contentRepository.findByIdAndStatusNot(contentId, ContentStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found!"));
        Cast cast = castRepository.findById(castId)
                .orElseThrow(() -> new ResourceNotFoundException("Cast not found!"));

        boolean alreadyExists = content.getCastMembers().stream()
                .anyMatch(cc -> cc.getCast().getId().equals(castId) && cc.getRole() == role);

        if (!alreadyExists) { ContentCast contentCast = contentMapper.toContentCast(content, cast, role);
            content.getCastMembers().add(contentCast);
            contentRepository.save(content);
        }
    }

    @Transactional
    @Override
    public void removeCastFromContent(Long contentId, Long castId) {
        Content content = contentRepository.findByIdAndStatusNot(contentId, ContentStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found!"));

        content.getCastMembers().removeIf(cc -> cc.getCast().getId().equals(castId));
        contentRepository.save(content);
    }

    private Content createContentRecursively(ContentRequestDto dto, Content parent) {
        Content content = contentMapper.toEntity(dto);
        content.setParentContent(parent);

        List<ContentCast> finalCasts = new java.util.ArrayList<>();
        if (dto.getCasts() != null && !dto.getCasts().isEmpty()) {
            for (ContentCastRequestDto castDto : dto.getCasts()) {
                Cast cast = castRepository.findById(castDto.getCastId())
                        .orElseThrow(() -> new ResourceNotFoundException("Person not found! ID: " + castDto.getCastId()));

                ContentCast contentCast = contentMapper.toContentCast(content, cast, castDto.getRole());
                finalCasts.add(contentCast);
            }
        }
        content.setCastMembers(finalCasts);

        if (dto.getMetadata() != null) {
            Metadata metadata = metadataService.createMetadata(dto.getMetadata());
            content.setMetadata(metadata);
        }
        if (dto.getSubContents() != null && !dto.getSubContents().isEmpty()) {
            List<Content> children = dto.getSubContents().stream()
                    .map(subDto -> createContentRecursively(subDto, content))
                    .collect(Collectors.toList());
            content.setSubContents(children);
        }

        return content;
    }

    private void updateChildrenRecursively(Content parent, ContentRequestDto requestDto) {
        if (parent.getSubContents() == null || parent.getSubContents().isEmpty() || requestDto == null) {
            return;
        }

        MetadataRequestDto childMetaDto = null;
        if (requestDto.getMetadata() != null) {
            childMetaDto = new MetadataRequestDto();
            org.springframework.beans.BeanUtils.copyProperties(requestDto.getMetadata(), childMetaDto);
            childMetaDto.setTitle(null);
        }
        for (Content child : parent.getSubContents()) {
            if (child.getStatus() == ContentStatus.DELETED) {

                if (childMetaDto != null) {
                    if (child.getMetadata() == null) {
                        child.setMetadata(metadataService.createMetadata(childMetaDto));
                    } else {
                        metadataService.updateMetadata(child.getMetadata(), childMetaDto);
                    }
                }

                if (requestDto.getCasts() != null) {
                    child.getCastMembers().clear();
                    for (ContentCastRequestDto castDto : requestDto.getCasts()) {
                        Cast cast = castRepository.findById(castDto.getCastId())
                                .orElseThrow(() -> new ResourceNotFoundException("Person not found! ID: " + castDto.getCastId()));
                        ContentCast contentCast = contentMapper.toContentCast(child, cast, castDto.getRole());
                        child.getCastMembers().add(contentCast);
                    }
                }

                updateChildrenRecursively(child, requestDto);
            }
        }
    }
    @Transactional
    @Override
    public ContentResponseDto updateContent(Long id, ContentRequestDto requestDto, boolean updateChildren) {
        Content content = contentRepository.findByIdAndStatusNot(id, ContentStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found! ID: " + id));
        contentMapper.updateEntityFromDto(requestDto, content);
        if (requestDto.getParentId() != null) {
            Content parent = contentRepository.findByIdAndStatusNot(requestDto.getParentId(), ContentStatus.DELETED)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent content not found! ID: " + requestDto.getParentId()));
            content.setParentContent(parent);
        }
        if (requestDto.getMetadata() != null) {
            if (content.getMetadata() == null) {
                Metadata metadata = metadataService.createMetadata(requestDto.getMetadata());
                content.setMetadata(metadata);
            } else {
                metadataService.updateMetadata(content.getMetadata(), requestDto.getMetadata());
            }
        }
        if (requestDto.getCasts() != null) {
            content.getCastMembers().clear();
            for (ContentCastRequestDto castDto : requestDto.getCasts()) {
                Cast cast = castRepository.findById(castDto.getCastId())
                        .orElseThrow(() -> new ResourceNotFoundException("Person not found! ID: " + castDto.getCastId()));
                ContentCast contentCast = contentMapper.toContentCast(content, cast, castDto.getRole());
                content.getCastMembers().add(contentCast);
            }
        }

        if (updateChildren) {
            updateChildrenRecursively(content, requestDto);
        }

        validatePublishStatus(content);
        Content savedContent = contentRepository.save(content);
        applicationEventPublisher.publishEvent(new ContentSavedEvent(this, savedContent));
        return contentMapper.toDto(savedContent);
    }

    private void validatePublishStatus(Content content) {
        if (content.getStatus() == ContentStatus.PUBLISHED) {
            boolean hasActiveLicense = false;
            if (content.getLicenses() != null && !content.getLicenses().isEmpty()) {
                hasActiveLicense = content.getLicenses().stream()
                        .anyMatch(license -> license.getStatus() == LicenseStatus.ACTIVE);
            }
            if (!hasActiveLicense) {
                throw new IllegalStateException("Content cannot be PUBLISHED because it has no ACTIVE licenses!");
            }
        }
    }

    @Transactional
    @Override
    public ContentResponseDto changeContentStatus(Long id, ContentStatus newStatus) {
        Content content = contentRepository.findByIdAndStatusNot(id, ContentStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found! ID: " + id));

        if (newStatus == ContentStatus.PUBLISHED) {
            content.setStatus(ContentStatus.PUBLISHED);
            validatePublishStatus(content);
        } else {
            content.setStatus(newStatus);
        }
        Content savedContent = contentRepository.save(content);
        applicationEventPublisher.publishEvent(new ContentSavedEvent(this, savedContent));
        return contentMapper.toDto(savedContent);
    }
}
