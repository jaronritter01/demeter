package com.finalproject.demeter.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "minor_items")
@Data
public class MinorItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JsonIgnore
    @ManyToOne
    private User user;
    @ManyToOne
    private FoodItem foodItem;
}
