package com.finalproject.demeter.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    @ManyToOne
    private Recipe recipe;
    private String measurementUnit;
    private Float quantity;
}
