package com.example.notes.core;


import lombok.Data;

import javax.persistence.*;
import java.security.Key;

@Data
@Entity
public class Note {
    @Id
    @Column(name = "note_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noteId;
    private String noteText = "";
    private String password = null;
    private Key noteKey = null;

    private boolean isEncrypted = false;

    private boolean isPublic = false;

    public Note(String NoteText, boolean isPublic) {
        this.noteText = NoteText;
        this.isPublic = isPublic;
    }

    public Note(String NoteText, String password) throws Exception {
        if (password != null && password.length() != 0) {
            this.isEncrypted = true;
            this.noteKey = Encryption.generateKey(password);
            this.noteText = Encryption.encrypt(NoteText, noteKey);
        } else {
            this.noteText = noteText;
        }
        this.password = password;
    }

    public Note() {

    }
}
