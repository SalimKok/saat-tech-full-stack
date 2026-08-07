package com.saattech.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saattech.enums.CastType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
public class ContentCast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cast_id")
    private Cast cast;

    @Enumerated(EnumType.STRING)
    private CastType role;
}