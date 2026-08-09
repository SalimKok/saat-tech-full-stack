package com.saattech.elasticsearch;

import com.saattech.enums.ContentType;
import com.saattech.enums.EntityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "contents")
public class ContentIndex {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "english")
    private String title;

    @Field(type = FieldType.Text, analyzer = "english")
    private String plot;

    @Field(type = FieldType.Text)
    private String genre;

    @Field(type = FieldType.Text)
    private List<String> castNames;


    @Field(type = FieldType.Keyword)
    private ContentType contentType;

    @Field(type = FieldType.Keyword)
    private EntityStatus status;


    @Field(type = FieldType.Double)
    private Double imdbRating;

    @Field(type = FieldType.Integer)
    private Integer year;

    @Field(type = FieldType.Integer)
    private Integer seasonNo;

    @Field(type = FieldType.Integer)
    private Integer episodeNo;

    @Field(type = FieldType.Integer)
    private Integer runtimeMinutes;


    @Field(type = FieldType.Keyword, index = false)
    private String poster;

    @Transient
    private Float score;

    @Transient
    private MatchExplanationDto matchExplanation;

    @Field(type = FieldType.Dense_Vector, dims = 384, similarity = "cosine")
    private List<Float> plotVector;
}

