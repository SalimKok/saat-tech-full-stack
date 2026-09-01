package com.saattech.service.implementation;

import com.saattech.config.properties.OmdbProperties;
import com.saattech.constant.exception.OmdbExceptionMessages;
import com.saattech.dto.omdb.OmdbResponseDto;
import com.saattech.dto.cast.CastRequestDto;
import com.saattech.dto.contentcast.ContentCastRequestDto;
import com.saattech.dto.content.ContentRequestDto;
import com.saattech.dto.bulkimport.BulkImportResponseDto;
import com.saattech.dto.cast.CastResponseDto;
import com.saattech.enums.CastType;
import com.saattech.exception.ResourceNotFoundException;
import com.saattech.mapper.OmdbMapper;
import com.saattech.service.CastService;
import com.saattech.service.ContentService;
import com.saattech.service.OmdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OmdbServiceImpl implements OmdbService {

    private final RestTemplate restTemplate;
    private final OmdbMapper omdbMapper;
    private final OmdbProperties configValues;
    private final CastService castService;
    private final ContentService contentService;

    @Override
    public ContentRequestDto fetchFromOmdb(String imdbId) {
        String requestUrl = configValues.getUrl() + "?apikey=" + configValues.getKey() + "&i=" + imdbId;
        OmdbResponseDto responseDto = restTemplate.getForObject(requestUrl, OmdbResponseDto.class);
        ContentRequestDto requestDto = omdbMapper.toContentRequestDto(responseDto);
        if (requestDto == null) {
            throw new ResourceNotFoundException(OmdbExceptionMessages.MOVIE_NOT_FOUND + imdbId);
        }

        List<ContentCastRequestDto> casts = new ArrayList<>();

        parseAndAddCasts(responseDto.getActors(), CastType.ACTOR, casts);
        parseAndAddCasts(responseDto.getDirector(), CastType.DIRECTOR, casts);
        parseAndAddCasts(responseDto.getWriter(), CastType.WRITER, casts);

        requestDto.setCasts(casts);

        return requestDto;
    }


    @Override
    public BulkImportResponseDto bulkImportFromOmdb(List<String> imdbIds) {
        List<String> successful = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        if (imdbIds != null) {
            for (String imdbId : imdbIds) {
                String cleanId = (imdbId != null) ? imdbId.trim() : "";
                if (cleanId.isEmpty()) continue;
                try {
                    ContentRequestDto requestDto = fetchFromOmdb(cleanId);
                    contentService.saveContent(requestDto);
                    successful.add(cleanId);
                } catch (Exception e) {
                    failed.add(cleanId + " -> " + e.getMessage());
                }
            }
        }
        return BulkImportResponseDto.builder()
                .totalRequested(imdbIds != null ? imdbIds.size() : 0)
                .successCount(successful.size())
                .failedCount(failed.size())
                .successfulIds(successful)
                .failedIds(failed)
                .build();
    }


    private void parseAndAddCasts(String omdbString, CastType role, List<ContentCastRequestDto> castsList) {
        if (omdbString != null && !omdbString.equalsIgnoreCase("N/A")) {

            String[] names = omdbString.split(",");

            for (String name : names) {

                CastRequestDto castReq = new CastRequestDto();
                castReq.setName(name.trim());


                CastResponseDto savedCast = castService.saveCast(castReq);

                ContentCastRequestDto ccReq = new ContentCastRequestDto();
                ccReq.setCastId(savedCast.getId());
                ccReq.setRole(role);
                ccReq.setCastName(savedCast.getName());

                castsList.add(ccReq);
            }
        }
    }

}
