package com.saattech.service;

import com.saattech.dto.request.ContentRequestDto;
import com.saattech.dto.response.BulkImportResponseDto;

import java.util.List;


public interface OmdbService {

    ContentRequestDto fetchFromOmdb(String imdbId);

    BulkImportResponseDto bulkImportFromOmdb(List<String> imdbIds);
}
