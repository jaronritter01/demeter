package com.finalproject.demeter.dao;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "recipe_kitchen_tools")
@Data
public class RecipeKitchenTool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private Recipe recipe;
    @ManyToOne
    private KitchenTool tool;
    private Integer quantity;
}
