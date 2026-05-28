package com.chat.repository;

import com.chat.entity.Discussion;
import com.chat.repository.dtos.DiscussionSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
    Optional<Discussion> findByRootMessageId(Long rootMessageId);
    boolean existsByRootMessageId(Long rootMessageId);

    @Query("SELECT d" +
            " FROM Discussion d" +
            " JOIN FETCH d.rootMessage m" +
            " JOIN FETCH m.space" +
            " WHERE d.id = :id")
    Optional<Discussion> findByIdFetchRootMessageAndSpace(@Param("id") Long id);

    @Query("SELECT new com.chat.repository.dtos.DiscussionSummary(" +
            "d.rootMessage.id, d.id, COUNT(dm))" +
            " FROM Discussion d" +
            " LEFT JOIN DiscussionMessage dm ON dm.discussion = d" +
            " WHERE d.rootMessage.id IN :messageIds" +
            " GROUP BY d.rootMessage.id, d.id")
    List<DiscussionSummary> findDiscussionSummariesByRootMessageIds(@Param("messageIds") List<Long> messageIds);
}
