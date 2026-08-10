package com.saattech.elasticsearch.builder;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import com.saattech.config.SearchProperties;
import com.saattech.elasticsearch.model.ContentIndex;
import com.saattech.specification.dto.ContentFilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ContentQueryBuilder {
    private final SearchProperties searchProperties;

    private static final String FIELD_TITLE = "title";
    private static final String FIELD_PLOT = "plot";
    private static final String FIELD_CAST = "castNames";
    private static final String FIELD_GENRE = "genre";

    public NativeQuery buildTextQuery(String query, ContentFilterDto filter, int topK) {

        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        float titleBoost = (filter != null && filter.getTitleBoost() != null && filter.getTitleBoost() > 0) ? filter.getTitleBoost() : searchProperties.getBoost().getTitle();
        float plotBoost  = (filter != null && filter.getPlotBoost()  != null && filter.getPlotBoost()  > 0) ? filter.getPlotBoost()  : searchProperties.getBoost().getPlot();
        float castBoost  = (filter != null && filter.getCastBoost()  != null && filter.getCastBoost()  > 0) ? filter.getCastBoost()  : searchProperties.getBoost().getCast();
        float genreBoost = (filter != null && filter.getGenreBoost() != null && filter.getGenreBoost() > 0) ? filter.getGenreBoost() : searchProperties.getBoost().getGenre();

        if (query != null && !query.trim().isEmpty()) {
            String trimmed = query.trim();
            List<String> dynamicFields = List.of(
                    FIELD_TITLE + "^" + titleBoost,
                    FIELD_PLOT + "^" + plotBoost,
                    FIELD_CAST + "^" + castBoost,
                    FIELD_GENRE + "^" + genreBoost
            );
            boolQuery.must(m -> m.multiMatch(mm -> mm
                    .query(trimmed)
                    .fields(dynamicFields)
                    .minimumShouldMatch(searchProperties.getQuery().getMinimumShouldMatch())
                    .fuzziness("AUTO")
            ));
        }
        applyFilters(boolQuery, filter);
        Highlight highlight = buildHighlightConfiguration();
        return NativeQuery.builder()
                .withQuery(boolQuery.build()._toQuery())
                .withHighlightQuery(new HighlightQuery(highlight, ContentIndex.class))
                .withPageable(PageRequest.of(0, topK))
                .build();
    }

    public NativeQuery buildVectorQuery(List<Float> queryVector, ContentFilterDto filter, int topK) {
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        if (queryVector != null && !queryVector.isEmpty()) {
            boolQuery.must(m -> m.knn(k -> k
                    .field("plotVector")
                    .queryVector(queryVector)
                    .numCandidates(searchProperties.getKnn().getCandidates())
            ));
        }
        applyFilters(boolQuery, filter);
        return NativeQuery.builder()
                .withQuery(boolQuery.build()._toQuery())
                .withPageable(PageRequest.of(0, topK))
                .build();
    }


    private void applyFilters(BoolQuery.Builder boolQuery, ContentFilterDto filter) {
        if (filter == null) return;

        if (filter.getContentType() != null) {
            boolQuery.filter(f -> f.match(t -> t.field("contentType").query(filter.getContentType().name())));
        }
        if (filter.getGenre() != null && !filter.getGenre().trim().isEmpty() && !filter.getGenre().equals("All")) {
            boolQuery.filter(f -> f.match(m -> m.field("genre").query(filter.getGenre().trim())));
        }
        if (filter.getMinRating() != null && filter.getMinRating() > 0) {
            boolQuery.filter(f -> f.range(r -> r.number(n -> n.field("imdbRating").gte(filter.getMinRating()))));
        }
        if (filter.getYear() != null && filter.getYear() > 0) {
            boolQuery.filter(f -> f.term(t -> t.field("year").value(filter.getYear())));
        }
    }

    private Highlight buildHighlightConfiguration() {
        HighlightParameters highlightParameters = HighlightParameters.builder()
                .withPreTags("<mark class=\"es-highlight\">")
                .withPostTags("</mark>")
                .build();

        return new Highlight(highlightParameters, List.of(
                new HighlightField("title"),
                new HighlightField("plot"),
                new HighlightField("castNames"),
                new HighlightField("genre")
        ));
    }
}