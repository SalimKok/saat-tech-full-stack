package com.saattech.elasticsearch.service;

public interface QueryExpansionService {

    String expand(String query);

    boolean needsExpansion(String query);

}
