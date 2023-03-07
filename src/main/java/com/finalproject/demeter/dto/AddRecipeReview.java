package com.finalproject.demeter.dto;

import lombok.Data;

@Data
public class AddRecipeReview {
        private String review;
        private Integer stars;
        private long recipeId;
}
