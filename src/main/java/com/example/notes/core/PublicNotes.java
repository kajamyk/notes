package com.example.notes.core;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import static javax.persistence.FetchType.EAGER;

@Data
@Entity
public class PublicNotes {
    @Id
    @Column(name = "notes_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notesId;
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = EAGER)
    private List<Note> publicNotes = new ArrayList<>();
}
