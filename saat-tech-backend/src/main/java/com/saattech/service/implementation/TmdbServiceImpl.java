package com.saattech.service.implementation;

import com.saattech.config.properties.TmdbProperties;
import com.saattech.dto.tmdb.TmdbFindResponseDto;
import com.saattech.dto.tmdb.TmdbVideoResponseDto;
import com.saattech.entity.Content;
import com.saattech.entity.Trailer;
import com.saattech.enums.ContentType;
import com.saattech.exception.ResourceNotFoundException;
import com.saattech.mapper.TrailerMapper;
import com.saattech.service.TmdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TmdbServiceImpl implements TmdbService {
    private final RestTemplate restTemplate;
    private final TmdbProperties tmdbProperties;
    private final TrailerMapper trailerMapper;

    @Override
    public Long findTmdbIdByImdbId(String imdbId) {
        String findUrl = tmdbProperties.getBaseUrl()
                + "/find/" + imdbId
                + "?api_key=" + tmdbProperties.getApiKey()
                + "&external_source=imdb_id";

        TmdbFindResponseDto response = restTemplate.getForObject(findUrl, TmdbFindResponseDto.class);

        if (response == null) {
            throw new ResourceNotFoundException("TMDB find response is null for IMDB ID: " + imdbId);
        }

        if (response.getMovieResults() != null && !response.getMovieResults().isEmpty()) {
            return response.getMovieResults().get(0).getId();
        }

        if (response.getTvResults() != null && !response.getTvResults().isEmpty()) {
            return response.getTvResults().get(0).getId();
        }

        throw new ResourceNotFoundException("No TMDB entry found for IMDB ID: " + imdbId);
    }

    @Override
    public List<Trailer> fetchTrailersFromTmdb(String imdbId, Content content) {
        Long tmdbId = findTmdbIdByImdbId(imdbId);

        String mediaType = resolveMediaType(content.getContentType());
        String videosUrl = tmdbProperties.getBaseUrl()
                + "/" + mediaType + "/" + tmdbId + "/videos"
                + "?api_key=" + tmdbProperties.getApiKey();

        TmdbVideoResponseDto response = restTemplate.getForObject(videosUrl, TmdbVideoResponseDto.class);

        if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
            log.warn("No videos found on TMDB for IMDB ID: {} (TMDB ID: {})", imdbId, tmdbId);
            return Collections.emptyList();
        }

        List<Trailer> trailers = new ArrayList<>();
        for (TmdbVideoResponseDto.TmdbVideoResult result : response.getResults()) {
            if (!"YouTube".equalsIgnoreCase(result.getSite())) {
                continue;
            }

            Trailer trailer = trailerMapper.toEntity(result, content);
            if (trailer != null) {
                trailers.add(trailer);
            }
        }

        log.info("Fetched {} YouTube trailers from TMDB for IMDB ID: {}", trailers.size(), imdbId);
        return trailers;
    }

    private String resolveMediaType(ContentType contentType) {
        if (contentType == ContentType.SERIES || contentType == ContentType.SEASON || contentType == ContentType.EPISODE) {
            return "tv";
        }
        return "movie";
    }
}
