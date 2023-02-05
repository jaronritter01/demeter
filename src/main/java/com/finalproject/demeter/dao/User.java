package com.finalproject.demeter.dao;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames= {"username"}),
                @UniqueConstraint(columnNames= {"email"})
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
}
