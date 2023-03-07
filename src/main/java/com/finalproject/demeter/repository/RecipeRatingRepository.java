package com.finalproject.demeter.repository;

import com.finalproject.demeter.dao.RecipeReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeRatingRepository extends JpaRepository<RecipeReview, Long> {
    Optional<Long> countByRecipeId(long recipe_id);
    @Query(
            value = "SELECT ROUND(AVG(r.stars),1) FROM recipe_reviews r WHERE r.recipe_id = ?1",
            nativeQuery = true
    )
    Optional<Float> getAverageReviewByRecipeId(long recipe_id);

    Optional<RecipeReview> findById(long review_id);
}
