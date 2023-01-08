package com.example.notes.core;

import com.sun.istack.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.ArrayList;
import java.util.List;


import javax.persistence.*;

import static javax.persistence.FetchType.EAGER;

@Data
@Entity
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @NotNull
    @UniqueElements
    private String userName;
    @NotNull
    private String userPassword;
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = EAGER)
    private List<Note> notes = new ArrayList<>();

    public User() {

    }
    public User(String userName, String userPassword) {
        this.userName = userName;
        this.userPassword = userPassword;
    }
}
