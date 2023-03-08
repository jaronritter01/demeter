package com.finalproject.demeter.repository;

import com.finalproject.demeter.dao.PersonalRecipe;
import com.finalproject.demeter.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PersonalRecipeRepository extends JpaRepository<PersonalRecipe, Long> {
    @Query(value = "SELECT * FROM personal_recipes WHERE user_id=?1 AND recipe_id=?2 FETCH FIRST ROW ONLY ",
            nativeQuery = true)
    Optional<PersonalRecipe> findByUserAndRecipe(long userId, long recipeId);

    Optional<List<PersonalRecipe>> findByUser(User user);
}
