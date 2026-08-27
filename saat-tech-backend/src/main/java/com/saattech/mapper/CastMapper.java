package com.saattech.mapper;

import com.saattech.dto.cast.CastRequestDto;
import com.saattech.dto.cast.CastResponseDto;
import com.saattech.entity.Cast;
import org.springframework.stereotype.Component;

@Component
public class CastMapper {

    public CastResponseDto toDto(Cast cast){
        if(cast == null){
            return  null;
        }

        CastResponseDto dto = new CastResponseDto();
        dto.setId(cast.getId());
        dto.setName(cast.getName());
        dto.setPoster(cast.getPoster());
        return dto;
    }

    public Cast toEntity(CastRequestDto requestDto){
        if (requestDto == null){
            return null;
        }

        Cast cast = new Cast();
        cast.setName(requestDto.getName());
        cast.setPoster(requestDto.getPoster());
        return cast;
    }

    public void updateEntityFromDto(CastRequestDto dto, Cast cast) {
        if (dto == null || cast == null) return;

        if (dto.getName() != null) cast.setName(dto.getName());
        if (dto.getPoster() != null) cast.setPoster(dto.getPoster());
    }
}
