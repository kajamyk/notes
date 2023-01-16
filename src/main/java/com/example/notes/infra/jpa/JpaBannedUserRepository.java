package com.example.notes.infra.jpa;

import com.example.notes.core.BannedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaBannedUserRepository extends JpaRepository<BannedUser, Long> {
}
