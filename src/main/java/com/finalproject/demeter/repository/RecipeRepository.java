package com.finalproject.demeter.repository;

import com.finalproject.demeter.dao.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query(
            value = "SELECT * FROM recipes r WHERE r.name like %?1% OR r.description like %?1% AND r.is_public=true",
            nativeQuery = true
    )
    Page<Recipe> findRecipeLike(String s, Pageable page);

    @Query(
            value = "SELECT * FROM recipes r WHERE r.cook_time <= ?1 AND r.is_public=true",
            nativeQuery = true
    )
    Page<Recipe> findRecipeWithTimeLess(int time, Pageable page);

    @Query(
            value = "SELECT * FROM recipes r WHERE r.cook_time >= ?1 AND r.is_public=true",
            nativeQuery = true
    )
    Page<Recipe> findRecipeWithTimeMore(int time, Pageable page);

    Recipe findById(long id);
}
