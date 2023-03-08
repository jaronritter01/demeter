package com.finalproject.demeter.dao;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "personal_recipes")
@Data
public class PersonalRecipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne
    private User user;
    @OneToOne
    private Recipe recipe;
}
