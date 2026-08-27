package com.saattech.service;

import com.saattech.dto.trailer.TrailerResponseDto;
import java.util.List;

public interface TrailerService {

    List<TrailerResponseDto> getTrailersByContentId(Long contentId);

    List<TrailerResponseDto> fetchAndSaveTrailers(Long contentId);

    void deleteTrailersByContentId(Long contentId);
}
