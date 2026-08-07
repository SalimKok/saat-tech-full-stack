package com.saattech.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import lombok.Data;
import java.time.LocalDate;

@Entity
@DynamicUpdate
@Data
public class Metadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate released;
    private Double imdbRating;
    private String imdbVotes;
    private String runtime;
    private String rated;
    private String language;
    private String country;
    private String awards;
    private String boxOffice;
    private String metascore;
    private String imdbID;
    private String genre;
    private String title;
    private String poster;

    @Column(columnDefinition = "TEXT")
    private String plot;

}
