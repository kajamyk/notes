package com.example.notes.core;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class BannedUser {
    @Id
    @Column(name = "banneduser_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bannedUserId;
    private String IP;

    public BannedUser(String IP) {
        this.IP = IP;
    }

    public BannedUser() {

    }
}
