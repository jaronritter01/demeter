package com.finalproject.demeter.dao;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "recipe_items")
@Data
public class RecipeItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private FoodItem foodItem;
    @ManyToOne
    private Recipe recipe;
    private String measurementUnit;
    private Float quantity;
}
