package com.saattech.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.search")
public class SearchProperties {

    private Expansion expansion = new Expansion();
    @Data
    public static class Expansion {
        private int minWord = 3;
    }

    private Rrf rrf = new Rrf();
    private Boost boost = new Boost();
    private Query query = new Query();
    private Knn knn = new Knn();

    @Data
    public static class Rrf {
        private int k;
        private Weight weight = new Weight();

        @Data
        public static class Weight {
            private double bm25;
            private double vector;
        }
    }
    @Data
    public static class Boost {
        private float title;
        private float plot;
        private float cast;
        private float genre;
    }
    @Data
    public static class Query {
        private String minimumShouldMatch;
    }
    @Data
    public static class Knn {
        private int candidates;
    }
}
