package com.finalproject.demeter.util;

import com.finalproject.demeter.dao.User;
import com.finalproject.demeter.dao.UserPreference;

public class UserPreferencesBuilder {
    private final UserPreference userPreference = new UserPreference();

    public UserPreferencesBuilder id(long id){
        userPreference.setId(id);
        return this;
    }

    public UserPreferencesBuilder user(User user) {
        userPreference.setUser(user);
        return this;
    }

    public UserPreferencesBuilder isMetric(boolean isMetric) {
        userPreference.setMetric(isMetric);
        return this;
    }

    public UserPreference build() {
        return userPreference;
    }
}
