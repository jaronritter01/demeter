package com.finalproject.demeter.util;

import com.finalproject.demeter.dto.RecipeQuery;

public class RecipeQueryBuilder {
    private RecipeQuery query = new RecipeQuery();

    public RecipeQueryBuilder method(String method) {
        query.setMethod(method);
        return this;
    }

    public RecipeQueryBuilder value(String value) {
        query.setValue(value);
        return this;
    }

    public RecipeQuery build() {
        return query;
    }
}
