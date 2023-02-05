package com.finalproject.demeter.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    // Note that this field exists on the object but will not be passed to the front end
    // This field contains user password info and needs to be treated carefully
    @JsonIgnore
    @ManyToOne
    private User userId;
    @ManyToOne
    private FoodItem foodId;
    private Float quantity;
    private String unit;
}

