package com.saattech.service;

import com.saattech.dto.tmdb.TmdbSaveRequestDto;
import com.saattech.dto.trailer.TrailerResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TrailerService {

    List<TrailerResponseDto> getTrailersByContentId(Long contentId);

    List<TrailerResponseDto> fetchAndSaveTrailers(Long contentId);

    void deleteTrailersByContentId(Long contentId);

    TrailerResponseDto uploadAndSaveTrailer(Long contentId, MultipartFile file, String name, String type);

    List<TrailerResponseDto> previewTmdbTrailers(Long contentId);

    TrailerResponseDto saveSingleTmdbTrailer(Long contentId, TmdbSaveRequestDto request);

    TrailerResponseDto updateTrailerDetails(Long contentId, Long trailerId, String newName, String newType);

    void deleteTrailer(Long contentId, Long trailerId);
}
