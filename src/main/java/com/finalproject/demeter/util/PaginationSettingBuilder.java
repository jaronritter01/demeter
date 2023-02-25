package com.finalproject.demeter.util;

import com.finalproject.demeter.dto.PaginationSetting;

public class PaginationSettingBuilder {
    private PaginationSetting settings = new PaginationSetting();

    public PaginationSettingBuilder pageNumber(int number){
        settings.setPageNumber(number);
        return this;
    }

    public PaginationSettingBuilder pageSize(int number) {
        settings.setPageSize(number);
        return this;
    }

    public PaginationSetting build() {
        return this.settings;
    }
}
