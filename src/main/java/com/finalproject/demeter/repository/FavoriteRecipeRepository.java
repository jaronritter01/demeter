package com.finalproject.demeter.repository;

import com.finalproject.demeter.dao.FavoriteRecipe;
import com.finalproject.demeter.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRecipeRepository extends JpaRepository<FavoriteRecipe, Long> {
    Optional<List<FavoriteRecipe>> findByUser(User user);
}
