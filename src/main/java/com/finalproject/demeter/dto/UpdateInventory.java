package com.finalproject.demeter.dto;

import lombok.Data;

@Data
public class UpdateInventory {
    private Long foodId;
    private Float quantity;
    private String unit;
}
