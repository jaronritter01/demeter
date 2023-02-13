package com.finalproject.demeter.dao

import spock.lang.Specification

class RecipeReviewSpec extends Specification{

    def "Given a valid star rating to set it should not error" (){
        given:
        RecipeReview rr = new RecipeReview()

        when:
        rr.setStars(5)

        then:
        noExceptionThrown()
    }

    def "Given a star rating too high to set, it should not error" (){
        given:
        RecipeReview rr = new RecipeReview()

        when:
        rr.setStars(10)

        then:
        final Exception exception = thrown()
        exception.message == "A value less than 1 or more than 5 cannot be used as a rating"
    }

    def "Given a star rating too low to set, it should not error" (){
        given:
        RecipeReview rr = new RecipeReview()

        when:
        rr.setStars(0)

        then:
        final Exception exception = thrown()
        exception.message == "A value less than 1 or more than 5 cannot be used as a rating"
    }
}
