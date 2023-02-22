package com.finalproject.demeter.repository;

import com.finalproject.demeter.dao.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query(
            value = "SELECT * FROM recipes r WHERE r.name like %?1% OR r.description like %?1%",
            nativeQuery = true
    )
    List<Recipe> findRecipeLike(String s);

    @Query(
            value = "SELECT * FROM recipes r WHERE cook_time <= ?1",
            nativeQuery = true
    )
    List<Recipe> findRecipeWithTimeLess(int time);

    @Query(
            value = "SELECT * FROM recipes r WHERE cook_time >= ?1",
            nativeQuery = true
    )
    List<Recipe> findRecipeWithTimeMore(int time);
}
