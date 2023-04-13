package com.finalproject.demeter.dto;

import com.finalproject.demeter.dao.Recipe;
import lombok.Data;

@Data
public class RecipeWithSub {
    private Recipe recipe;
    private Boolean isSubbed = false;
    private long foodIdToReplace;
    private long subbedId;
}
