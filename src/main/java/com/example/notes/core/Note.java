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
    @Column(columnDefinition = "LONGTEXT")
    private String noteText = "";
    private String password = null;
    private Key noteKey = null;

    private boolean isEncrypted = false;

    private boolean isPublic = false;

    public Note(String noteText, boolean isPublic) {
        this.noteText = noteText;
        this.isPublic = isPublic;
    }

    public Note(String noteText, String password) throws Exception {
        if (password != null && password.length() != 0) {
            this.isEncrypted = true;
            this.noteKey = Encryption.generateKey(password);
            this.noteText = Encryption.encrypt(noteText, noteKey);
        } else {
            this.noteText = noteText;
        }
        this.password = password;
    }

    public Note() {

    }
}
