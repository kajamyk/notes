package com.example.notes.infra.api.services;

import com.example.notes.core.Note;
import com.example.notes.core.User;
import com.example.notes.infra.jpa.JpaUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    JpaUserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    NoteService noteService;
    private String encodePassword(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
    private boolean arePasswordsMatching(String rawPassword, String encodedPassword) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public boolean areUserCredentialsValid(String userName, String password) {
        User user = getUserByUserName(userName);
        if(user != null) {
            return arePasswordsMatching(password, user.getUserPassword());
        }
        return false;
    }
    public void addUser(String userName, String password) {
        User user = new User(userName, encodePassword(password));
        userRepository.save(user);
    }
    private User getUserByUserName(String userName) {
        List<User> users = getAllUsers();
        for(User user : users) {
            if(user.getUserName().equals(userName)) {
                return user;
            }
        }
        return null;
    }
    public boolean isUserNameAvailable(String userName) {
        List<String> userNames = getAllUserNames();
        return ! userNames.contains(userName);
    }
    private List<String> getAllUserNames() {
        List<String> userNames = new ArrayList<>();
        List<User> users = getAllUsers();
        for(User user : users) {
            userNames.add(user.getUserName());
        }
        return userNames;
    }
    private List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public List<Note> getAllNotes(String userName) {
        List<Note> notes = noteService.getAllNotes();
        User user = getUserByUserName(userName);
        notes.addAll(user.getNotes());
        return notes;
    }

    private void saveUser(User user) {

    }
}
