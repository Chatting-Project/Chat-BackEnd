package com.chat.repository;

import com.chat.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpaceRepository extends JpaRepository<Space, Long> {

    Optional<Space> findByInviteCode(String inviteCode);
}
