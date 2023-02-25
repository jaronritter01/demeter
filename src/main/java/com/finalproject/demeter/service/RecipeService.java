package com.finalproject.demeter.service;

import com.finalproject.demeter.dao.Recipe;
import com.finalproject.demeter.dto.RecipeQuery;
import com.finalproject.demeter.repository.RecipeRepository;
import com.finalproject.demeter.util.MapBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RecipeService {
    private RecipeRepository recipeRepository;
    private final Pattern SPECIALCHARREGEX = Pattern.compile("[$&+:;=?@#|<>.^*()%!]");

    enum QueryMethod {
        LESS,
        MORE,
        NAME,
        DESC
    }

    private Map<String, QueryMethod> queryMap = new MapBuilder<String, QueryMethod>()
            .put("lesstime", QueryMethod.LESS)
            .put("moretime", QueryMethod.MORE)
            .put("desc", QueryMethod.DESC)
            .put("name", QueryMethod.NAME)
            .build();

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public ResponseEntity<?> getQueriedRecipes(RecipeQuery query) {
        QueryMethod method = queryMap.get(query.getMethod().toLowerCase());
        if (method == null) {
            return new ResponseEntity("Invalid Method", HttpStatus.BAD_REQUEST);
        }
        switch (method) {
            case NAME:
            case DESC: {
                Matcher specMatch = SPECIALCHARREGEX.matcher(query.getValue());
                if (specMatch.find()) {
                    return new ResponseEntity("Invalid Query", HttpStatus.BAD_REQUEST);
                }
                return new ResponseEntity(recipeRepository.findRecipeLike(query.getValue()), HttpStatus.OK);
            }
            case LESS: {
                try {
                    List<Recipe> recipeList = recipeRepository.findRecipeWithTimeLess(
                            Integer.parseInt(query.getValue())
                    );
                    return new ResponseEntity(recipeList, HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity("Please pass a valid number", HttpStatus.BAD_REQUEST);
                }
            }
            case MORE: {
                try {
                    List<Recipe> recipeList = recipeRepository.findRecipeWithTimeMore(
                            Integer.parseInt(query.getValue())
                    );
                    return new ResponseEntity(recipeList, HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity("Please pass a valid number", HttpStatus.BAD_REQUEST);
                }
            }
            default: {
                // This is unreachable in theory
                return new ResponseEntity<>("Invalid Method", HttpStatus.BAD_REQUEST);
            }
        }
    }
}
