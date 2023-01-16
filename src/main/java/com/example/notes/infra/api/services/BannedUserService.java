package com.example.notes.infra.api.services;

import com.example.notes.core.BannedUser;
import com.example.notes.infra.jpa.JpaBannedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannedUserService {
    @Autowired
    JpaBannedUserRepository bannedUserRepository;

    public void addBannedUser(String IP) {
        BannedUser bannedUser = new BannedUser(IP);
        bannedUserRepository.save(bannedUser);
    }

    public boolean isUserBanned(String IP) {


        List<BannedUser> bannedUsersList = bannedUserRepository.findAll();
        for (BannedUser bannedUser : bannedUsersList) {
            if (bannedUser.getIP().equals(IP)) {
                return true;
            }
        }
        return false;
    }
}
