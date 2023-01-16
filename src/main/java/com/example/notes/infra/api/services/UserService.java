package com.example.notes.infra.api.services;

import com.example.notes.core.Encryption;
import com.example.notes.core.Note;
import com.example.notes.core.User;
import com.example.notes.infra.jpa.JpaUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
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
        log.error(text);
        if (password != null) {
            user.getNotes().add(new Note(text, password));
        } else {
            user.getNotes().add(new Note(text, false));
        }
        userRepository.save(user);
    }

    public List<String> getAllUserNotes(String userName) {
        List<Note> notes = noteService.getAllNotes();
        List<String> finalList = new ArrayList<>();
        User user = getUserByUserName(userName);
        List<Note> userNotes = user.getNotes();

        try {
            for(Note note : notes) {
                if(note.isPublic()) {
                    finalList.add(note.getNoteText());
                }
            }
            for(Note userNote : userNotes) {
                if(! userNote.isEncrypted()) {
                    finalList.add(userNote.getNoteText());
                }
            }

            finalList.removeAll(Arrays.asList("", null));
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
