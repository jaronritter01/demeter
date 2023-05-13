package com.finalproject.demeter.repository;

import com.finalproject.demeter.dao.Recipe;
import com.finalproject.demeter.dao.RecipeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RecipeItemRepository extends JpaRepository<RecipeItem, Long> {
    Optional<List<RecipeItem>> findRecipeItemsByRecipe(Recipe recipe);
    Optional<List<RecipeItem>> deleteRecipeItemsByRecipe(Recipe recipe);

    @Query(
            value = "SELECT food_item_id FROM recipe_items r WHERE r.recipe_id = ?1",
            nativeQuery = true
    )
    Set<Long> findRecipeItemsFoodIdsByRecipeId(Long recipeId);
}
