package com.saattech.service;

import com.saattech.dto.content.ContentRequestDto;
import com.saattech.dto.bulkimport.BulkImportResponseDto;

import java.util.List;


public interface OmdbService {

    ContentRequestDto fetchFromOmdb(String imdbId);

    BulkImportResponseDto bulkImportFromOmdb(List<String> imdbIds);
}
