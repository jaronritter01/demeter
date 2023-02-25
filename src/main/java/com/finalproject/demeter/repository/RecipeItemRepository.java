package com.finalproject.demeter.repository;

import com.finalproject.demeter.dao.Recipe;
import com.finalproject.demeter.dao.RecipeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeItemRepository extends JpaRepository<RecipeItem, Long> {
    Optional<List<RecipeItem>> findRecipeItemsByRecipe(Recipe recipe);
}
