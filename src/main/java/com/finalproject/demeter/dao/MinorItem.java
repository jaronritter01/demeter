package com.finalproject.demeter.dao;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "minor_items")
@Data
public class MinorItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private FoodItem foodItem;
}
