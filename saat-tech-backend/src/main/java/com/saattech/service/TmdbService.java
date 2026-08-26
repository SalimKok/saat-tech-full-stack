package com.saattech.service;

import com.saattech.entity.Content;
import com.saattech.entity.Trailer;

import java.util.List;

public interface TmdbService {

    Long findTmdbIdByImdbId(String imdbId);

    List<Trailer> fetchTrailersFromTmdb(String imdbId, Content content);
}
