package com.saattech.service.implementation;

import com.saattech.dto.trailer.TrailerResponseDto;
import com.saattech.entity.Content;
import com.saattech.entity.Trailer;
import com.saattech.enums.ContentStatus;
import com.saattech.exception.ResourceNotFoundException;
import com.saattech.mapper.TrailerMapper;
import com.saattech.repository.ContentRepository;
import com.saattech.repository.TrailerRepository;
import com.saattech.service.TmdbService;
import com.saattech.service.TrailerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrailerServiceImpl implements TrailerService {
    private final TrailerRepository trailerRepository;
    private final ContentRepository contentRepository;
    private final TmdbService tmdbService;
    private final TrailerMapper trailerMapper;

    @Override
    public List<TrailerResponseDto> getTrailersByContentId(Long contentId) {
        contentRepository.findByIdAndStatusNot(contentId, ContentStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found! ID: " + contentId));

        List<Trailer> trailers = trailerRepository.findByContentId(contentId);
        return trailerMapper.toDtoList(trailers);
    }

    @Override
    public List<TrailerResponseDto> fetchAndSaveTrailers(Long contentId) {
        Content content = contentRepository.findByIdAndStatusNot(contentId, ContentStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found! ID: " + contentId));

        String imdbId = extractImdbId(content);

        List<Trailer> fetchedTrailers = tmdbService.fetchTrailersFromTmdb(imdbId, content);

        List<Trailer> newTrailers = new ArrayList<>();
        for (Trailer trailer : fetchedTrailers) {
            boolean alreadyExists = trailerRepository.existsByContentIdAndYoutubeKey(contentId, trailer.getYoutubeKey());
            if (!alreadyExists) {
                newTrailers.add(trailer);
            }
        }

        if (!newTrailers.isEmpty()) {
            trailerRepository.saveAll(newTrailers);
            log.info("Saved {} new trailers for content ID: {}", newTrailers.size(), contentId);
        } else {
            log.info("No new trailers to save for content ID: {} (all already exist)", contentId);
        }

        List<Trailer> allTrailers = trailerRepository.findByContentId(contentId);
        return trailerMapper.toDtoList(allTrailers);
    }

    @Transactional
    @Override
    public void deleteTrailersByContentId(Long contentId) {
        contentRepository.findByIdAndStatusNot(contentId, ContentStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found! ID: " + contentId));
        trailerRepository.deleteByContentId(contentId);
        log.info("Deleted all trailers for content ID: {}", contentId);
    }

    private String extractImdbId(Content content) {
        if (content.getMetadata() == null || content.getMetadata().getImdbID() == null
                || content.getMetadata().getImdbID().isBlank()) {
            throw new IllegalStateException("Content has no IMDB ID in metadata (Content ID: " + content.getId() + ")");
        }
        return content.getMetadata().getImdbID().trim();
    }
}
