package com.finalproject.demeter.service;

import com.finalproject.demeter.dao.Recipe;
import com.finalproject.demeter.dto.PaginationSetting;
import com.finalproject.demeter.dto.RecipeQuery;
import com.finalproject.demeter.repository.RecipeRepository;
import com.finalproject.demeter.util.MapBuilder;
import com.finalproject.demeter.util.PaginationSettingBuilder;
import com.finalproject.demeter.util.RecipeBuilder;
import com.finalproject.demeter.util.RecipeQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RecipeService {
    private RecipeRepository recipeRepository;
    private final Pattern SPECIALCHARREGEX = Pattern.compile("[$&+:;=?@#|<>.^*()%!]");
    private final Logger LOGGER = LoggerFactory.getLogger(RecipeService.class);
    private static final PaginationSetting DEFAULT_PAGE = new PaginationSettingBuilder()
            .pageNumber(0)
            .pageSize(5)
            .build();

    private static final RecipeQuery DEFAULT_QUERY = new RecipeQueryBuilder()
            .method("name")
            .value("default")
            .build();

    private static final Recipe DEFAULT_RECIPE = new RecipeBuilder()
            .id(0)
            .name("Default Recipe")
            .description("Default Description")
            .cookTime(60)
            .build();

    // Usually you should not do this
    private static final List<Recipe> DEFAULT_RECIPE_LIST = new ArrayList<>(){{
        add(DEFAULT_RECIPE);
    }};

    enum QueryMethod {
        LESS,
        MORE,
        NAME,
        DESC,
        DEFAULT
    }

    private Map<String, QueryMethod> queryMap = new MapBuilder<String, QueryMethod>()
            .put("lesstime", QueryMethod.LESS)
            .put("moretime", QueryMethod.MORE)
            .put("desc", QueryMethod.DESC)
            .put("name", QueryMethod.NAME)
            .put("default", QueryMethod.DEFAULT)
            .build();

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> getAllRecipes(PaginationSetting pageSettings) {
        Pageable page = PageRequest.of(pageSettings.getPageNumber(), pageSettings.getPageSize());
        List<Recipe> returnList = new ArrayList<>();
        recipeRepository.findAll(page).forEach(recipe -> returnList.add(recipe));
        return returnList;
    }

    public PaginationSetting getPaginationSettings(HashMap<String, HashMap<String, String>> objs) {
        try {
            Integer pageNumber = Integer.parseInt(objs.get("page").get("pageNumber"));
            Integer pageSize = Integer.parseInt(objs.get("page").get("pageSize"));
            PaginationSetting pageSettings = new PaginationSettingBuilder()
                    .pageNumber(pageNumber)
                    .pageSize(pageSize)
                    .build();
            return pageSettings;
        }
        catch (Exception e){
            LOGGER.error("Issue parsing page settings: ", e.getMessage(), e);
        }

        return DEFAULT_PAGE;
    }

    public RecipeQuery getRecipeQuery(HashMap<String, HashMap<String, String>> objs) {
        try {
            RecipeQuery query = new RecipeQueryBuilder()
                    .method(objs.get("query").get("method"))
                    .value(objs.get("query").get("value"))
                    .build();

            return query;
        } catch (Exception e) {
            LOGGER.error("Issue parsing query: ", e.getMessage(), e);
        }
        return DEFAULT_QUERY;
    }

    public ResponseEntity<?> getQueriedRecipes(RecipeQuery query, PaginationSetting pageSettings) {
        QueryMethod method = queryMap.get(query.getMethod().toLowerCase());
        Pageable page = PageRequest.of(pageSettings.getPageNumber(), pageSettings.getPageSize());
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
                return new ResponseEntity(recipeRepository.findRecipeLike(query.getValue(), page), HttpStatus.OK);
            }
            case LESS: {
                try {
                    List<Recipe> recipeList = recipeRepository.findRecipeWithTimeLess(
                            Integer.parseInt(query.getValue()), page
                    );
                    return new ResponseEntity(recipeList, HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity("Please pass a valid number", HttpStatus.BAD_REQUEST);
                }
            }
            case MORE: {
                try {
                    List<Recipe> recipeList = recipeRepository.findRecipeWithTimeMore(
                            Integer.parseInt(query.getValue()), page
                    );
                    return new ResponseEntity(recipeList, HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity("Please pass a valid number", HttpStatus.BAD_REQUEST);
                }
            }
            case DEFAULT: {
                return new ResponseEntity(DEFAULT_RECIPE_LIST, HttpStatus.OK);
            }
            default: {
                // This is unreachable in theory
                return new ResponseEntity<>("Invalid Method", HttpStatus.BAD_REQUEST);
            }
        }
    }
}
