package com.finalproject.demeter.dto;

import lombok.Data;

@Data
public class UpdateRecipeReview {
    private long reviewId;
    private String review;
    private Integer stars;
}
