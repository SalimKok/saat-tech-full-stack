package com.saattech.service;

import com.saattech.dto.request.CastRequestDto;
import com.saattech.dto.response.CastResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CastService {

    Page<CastResponseDto> getAllCasts(String name, Pageable pageable);

    CastResponseDto getCastById(Long id);

    CastResponseDto saveCast(CastRequestDto requestDto);

    void deleteCast(Long id);

    CastResponseDto updateCast(Long id, CastRequestDto requestDto);
}
