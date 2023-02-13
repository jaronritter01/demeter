package com.finalproject.demeter.dao;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Entity
@Table(name = "recipe_reviews")
@Data
public class RecipeReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private Recipe recipe;
    @ManyToOne
    private User user;
    private String review;
    @Setter(AccessLevel.NONE) // This should prevent Lombok from generating it's own setter for stars
    private Integer stars;

    public void setStars(Integer stars) throws Exception{
        if (stars <= 5 || stars >= 1){
            this.stars = stars;
        }
        else{
            throw new Exception("A value less than 1 or more than 5 cannot be used as a rating");
        }
    }
}
