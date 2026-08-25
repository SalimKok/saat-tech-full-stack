package com.saattech.service.implementation;

import com.saattech.dto.request.CastRequestDto;
import com.saattech.dto.response.CastResponseDto;
import com.saattech.entity.Cast;
import com.saattech.enums.CastStatus;
import com.saattech.mapper.CastMapper;
import com.saattech.repository.CastRepository;
import com.saattech.exception.ResourceNotFoundException;
import com.saattech.service.CastService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CastServiceImpl implements CastService {

    private final CastRepository castRepository;
    private final CastMapper castMapper;

    @Override
    public Page<CastResponseDto> getAllCasts(String name, Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "id")
            );
        }
        Page<Cast> castPage;
        if (name != null && !name.trim().isEmpty()) {
            castPage = castRepository.findByNameContainingIgnoreCaseAndStatus(name.trim(), CastStatus.ACTIVE, pageable);
        } else {
            castPage = castRepository.findByStatus(CastStatus.ACTIVE, pageable);
        }
        return castPage.map(castMapper::toDto);
    }

    @Override
    public CastResponseDto getCastById(Long id) {
        Cast cast = castRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cast not found! ID: " + id));
        return castMapper.toDto(cast);
    }


    @Override
    public CastResponseDto saveCast(CastRequestDto requestDto) {
        Cast existingCast = castRepository.findByName(requestDto.getName());

        if (existingCast != null) {
            return castMapper.toDto(existingCast);
        }
        Cast cast = castMapper.toEntity(requestDto);
        Cast savedCast = castRepository.save(cast);
        return castMapper.toDto(savedCast);
    }

    @Override
    public void deleteCast(Long id) {
        Cast cast = castRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No cast found to delete! ID: " + id));

        cast.setStatus(CastStatus.DELETED);
        castRepository.save(cast);
    }

    @Transactional
    @Override
    public CastResponseDto updateCast(Long id, CastRequestDto requestDto) {
        Cast cast = castRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cast not found! ID: " + id));

        castMapper.updateEntityFromDto(requestDto, cast);

        return castMapper.toDto(cast);
    }
}
