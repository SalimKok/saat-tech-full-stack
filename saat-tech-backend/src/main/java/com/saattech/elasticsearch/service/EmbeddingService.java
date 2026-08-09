package com.saattech.elasticsearch.service;


import java.util.List;
public interface EmbeddingService {

    List<Float> getEmbedding(String text);

}