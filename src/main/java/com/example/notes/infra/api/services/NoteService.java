package com.example.notes.infra.api.services;

import com.example.notes.core.Note;
import com.example.notes.infra.jpa.JpaNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class NoteService {
    @Autowired
    JpaNoteRepository jpaNoteRepository;

    public void addPublicNote(String text) {
        Note note = new Note(text, true);
        jpaNoteRepository.save(note);
    }

    public List<Note> getAllNotes() {
        return jpaNoteRepository.findAll().stream().filter(Note::isPublic).toList();
    }
}
