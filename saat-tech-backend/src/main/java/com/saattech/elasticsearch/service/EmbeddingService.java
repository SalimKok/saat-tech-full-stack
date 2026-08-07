package com.saattech.elasticsearch.service;


import java.util.List;
public interface EmbeddingService {

    List<Float> getEmbedding(String text);

    double calculateCosineSimilarity(List<Float> vector1, List<Float> vector2);

    boolean isAvailable();
}