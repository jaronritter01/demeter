package com.finalproject.demeter.service

import com.finalproject.demeter.dao.Recipe
import com.finalproject.demeter.dto.RecipeQuery
import com.finalproject.demeter.repository.RecipeRepository
import org.apache.coyote.Response
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

class RecipeServiceSpec extends Specification{
    private RecipeRepository recipeRepository = Mock()
    private RecipeService recipeService = new RecipeService(recipeRepository)

    def "getAllRecipes should call the recipe repo" () {
        when:
        recipeService.getAllRecipes()

        then:
        1 * recipeRepository.findAll()
    }

    def "when an invalid method def is passed, a bad request should be returned" () {
        given:
        RecipeQuery rq = new RecipeQuery()
        rq.setMethod("invalidMethod")
        rq.setValue("abc")

        when:
        ResponseEntity response = recipeService.getQueriedRecipes(rq)

        then:
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def "when a valid method def is passed, a 200 and a list should be returned" () {
        given:
        RecipeQuery rq = new RecipeQuery()
        rq.setMethod("lesstime")
        rq.setValue("2")
        recipeRepository.findRecipeWithTimeLess(_) >> new ArrayList<Recipe>()

        when:
        ResponseEntity response = recipeService.getQueriedRecipes(rq)

        then:
        response.getStatusCode() == HttpStatus.OK && response.body instanceof List<Recipe>
    }

    def "when a valid method def and an invalid value is passed, a 200 and a list should be returned" () {
        given:
        RecipeQuery rq = new RecipeQuery()
        rq.setMethod("lesstime")
        rq.setValue("asdf")
        recipeRepository.findRecipeWithTimeLess(_) >> {throw new NumberFormatException()}

        when:
        ResponseEntity response = recipeService.getQueriedRecipes(rq)

        then:
        response.getStatusCode() == HttpStatus.BAD_REQUEST && response.body == "Please pass a valid number"
    }
}
