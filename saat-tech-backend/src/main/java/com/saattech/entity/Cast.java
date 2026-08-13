package com.saattech.entity;

import com.saattech.enums.CastStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import lombok.Data;

@Entity
@Table(name = "casts")
@DynamicUpdate
@Data
public class Cast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String poster;
    private String name;

    @Enumerated(EnumType.STRING)
    private CastStatus status = CastStatus.ACTIVE;

}
