package com.finalproject.demeter.dao;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name="recipes")
@Data
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Column(length = 2048)
    private String description;
    @Transient
    private Float avgRating;
    @Transient
    private Long reviewCount;
    @NotNull
    private Boolean isPublic;
    @Column(length = 4096)
    private String picId;
}
