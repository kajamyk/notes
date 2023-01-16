package com.example.notes.infra.api.services;

import com.example.notes.core.Encryption;
import com.example.notes.core.Note;
import com.example.notes.core.User;
import com.example.notes.infra.jpa.JpaUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    JpaUserRepository userRepository;

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
        if (user != null) {
            return arePasswordsMatching(password, user.getUserPassword());
        }
        return false;
    }

    public void addUser(String userName, String password) {
        User user = new User(userName, encodePassword(password));
        userRepository.save(user);
    }

    public User getUserByUserName(String userName) {
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getUserName().equals(userName)) {
                return user;
            }
        }
        return null;
    }

    public boolean isUserNameAvailable(String userName) {
        List<String> userNames = getAllUserNames();
        return !userNames.contains(userName);
    }

    public void addUserNote(String userName, String text, String password) throws Exception {
        User user = getUserByUserName(userName);
        assert user != null;
        if (password != null) {
            user.getNotes().add(new Note(text, password));
        } else {
            user.getNotes().add(new Note(text, false));
        }
        userRepository.save(user);
    }

    public List<String> getAllUserNotes(String userName) {
        List<Note> notes = noteService.getAllNotes();
        User user = getUserByUserName(userName);
        try {
            List<Note> userNotes = user.getNotes();
            List<String> finalList = new ArrayList<>();

            for (Note userNote : userNotes) {
                if (!userNote.isEncrypted()) {
                    notes.add(userNote);
                }
            }

            for (Note note : notes) {
                finalList.add(note.getNoteText());
            }

            return finalList;
        } catch (Exception exception) {
            return null;
        }
    }

    public String getSecretNote(String userName, String password) throws Exception {
        User user = getUserByUserName(userName);
        assert user != null;
        List<Note> userNotes = user.getNotes();
        for (Note note : userNotes) {
            if (note.isEncrypted() && note.getPassword().equals(password)) {
                return Encryption.decrypt(note.getNoteText(), note.getNoteKey());
            }
        }
        return null;
    }

    private List<String> getAllUserNames() {
        List<String> userNames = new ArrayList<>();
        List<User> users = getAllUsers();
        for (User user : users) {
            userNames.add(user.getUserName());
        }
        return userNames;
    }


    private List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
