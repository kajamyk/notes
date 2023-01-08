package com.example.notes.core;


import lombok.Data;


import javax.persistence.*;

@Data
@Entity
public class Note {
    @Id
    @Column(name = "note_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noteId;
    private String text;
    private String pictureLink;
}
