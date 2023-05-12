package com.finalproject.demeter.repository;

import com.finalproject.demeter.dao.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query(
            value = "SELECT * FROM recipes r WHERE r.is_public=true",
            nativeQuery = true
    )
    Page<Recipe> findAllPublic(Pageable page);

    @Query(
            value = "SELECT * FROM recipes r WHERE r.is_public=true",
            nativeQuery = true
    )
    List<Recipe> findAllPublic();
    @Query(
            value = "SELECT * FROM recipes r WHERE (r.name like %?1% OR r.description like %?1%) AND r.is_public=true",
            nativeQuery = true
    )
    Page<Recipe> findRecipeLike(String s, Pageable page);

    Recipe findById(long id);
}
