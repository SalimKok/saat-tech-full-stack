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

    // TMDB'den videoları veritabanına KAYDETMEDEN sadece önizleme olarak getirir
    List<TrailerResponseDto> previewTmdbTrailers(Long contentId);

    // Seçilen tek bir TMDB videosunu veritabanına kaydeder
    TrailerResponseDto saveSingleTmdbTrailer(Long contentId, TmdbSaveRequestDto request);

    // Mevcut bir videonun etiketini (Trailer, Teaser vs.) günceller
    TrailerResponseDto updateTrailerType(Long contentId, Long trailerId, String newType);

}
