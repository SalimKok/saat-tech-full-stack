package com.saattech.service.implementation;

import com.saattech.constant.exception.CastExceptionMessages;
import com.saattech.dto.cast.CastContentDto;
import com.saattech.dto.cast.CastRequestDto;
import com.saattech.dto.cast.CastResponseDto;
import com.saattech.entity.Cast;
import com.saattech.entity.ContentCast;
import com.saattech.enums.CastStatus;
import com.saattech.mapper.CastMapper;
import com.saattech.repository.CastRepository;
import com.saattech.exception.ResourceNotFoundException;
import com.saattech.repository.ContentCastRepository;
import com.saattech.service.CastService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CastServiceImpl implements CastService {

    private final CastRepository castRepository;
    private final CastMapper castMapper;
    private final ContentCastRepository contentCastRepository;


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
                .orElseThrow(() -> new ResourceNotFoundException(CastExceptionMessages.NOT_FOUND_ID + id));
        return castMapper.toDto(cast);
    }
    @Override
    public List<CastContentDto> getCastContents(Long id) {
        castRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CastExceptionMessages.NOT_FOUND_ID + id));

        List<ContentCast> contentCasts = contentCastRepository.findByCastIdWithContent(id);

        return contentCasts.stream().map(cc -> {
            CastContentDto cDto = new CastContentDto();
            cDto.setContentId(cc.getContent().getId());

            if (cc.getContent().getMetadata() != null) {
                cDto.setTitle(cc.getContent().getMetadata().getTitle());
                cDto.setPoster(cc.getContent().getMetadata().getPoster());
            }

            cDto.setRole(cc.getRole());
            return cDto;
        }).toList();
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
                .orElseThrow(() -> new ResourceNotFoundException(CastExceptionMessages.NOT_FOUND_TO_DELETE + id));

        cast.setStatus(CastStatus.DELETED);
        castRepository.save(cast);
    }

    @Transactional
    @Override
    public CastResponseDto updateCast(Long id, CastRequestDto requestDto) {
        Cast cast = castRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CastExceptionMessages.NOT_FOUND_ID + id));

        castMapper.updateEntityFromDto(requestDto, cast);

        return castMapper.toDto(cast);
    }
}
