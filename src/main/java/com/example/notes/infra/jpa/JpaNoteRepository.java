package com.example.notes.infra.jpa;

import com.example.notes.core.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaNoteRepository extends JpaRepository<Note, Long> {
}
