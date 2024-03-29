package com.finalproject.demeter.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "disliked_items")
@Data
public class DislikedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JsonIgnore
    @ManyToOne
    private User user;
    @ManyToOne
    private FoodItem foodItem;
}
