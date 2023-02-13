package com.finalproject.demeter.dao;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "favorited_recipes")
@Data
public class FavoriteRecipes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Recipe recipe;
}
