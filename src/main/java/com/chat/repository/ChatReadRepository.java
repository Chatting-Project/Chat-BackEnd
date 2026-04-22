package com.chat.repository;

import com.chat.entity.ChatRead;
import com.chat.repository.dtos.ChatRoomUnreadCount;
import com.chat.repository.dtos.ChatUnreadCount;
import com.chat.repository.dtos.MemberUnreadCount;
import com.chat.service.dtos.LastChatRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatReadRepository extends JpaRepository<ChatRead, Long> {

    @Query("SELECT COUNT (cre)" +
            " FROM ChatRead cre" +
            " WHERE cre.chat.id = :chatId" +
            " AND cre.isRead = false")
    Long findUnReadCountBy(@Param("chatId") Long chatId);

    @Query("SELECT new com.chat.repository.dtos.ChatRoomUnreadCount(c.chatRoom.id, COUNT(cre))" +
            " FROM ChatRead cre" +
            " JOIN cre.chat c" +
            " WHERE c.chatRoom.id IN :chatRoomIds" +
            " AND cre.member.id = :memberId" +
            " AND cre.isRead = false" +
            " GROUP BY c.chatRoom.id")
    List<ChatRoomUnreadCount> findChatRoomUnreadCountsBy(@Param("chatRoomIds") List<Long> chatRoomIds,
                                                 @Param("memberId") Long memberId);

    @Query("SELECT cr" +
            " FROM ChatRead cr" +
            " WHERE cr.chat.id = :chatId" +
            " and cr.member.id = :memberId")
    ChatRead findBy(@Param("chatId") Long chatId, @Param("memberId") Long memberId);

    @Query("SELECT new com.chat.service.dtos.LastChatRead(cr.member.id, COALESCE(MAX(CASE WHEN cr.isRead = true THEN c.id ELSE 0 END), 0))" +
            "FROM ChatRead cr " +
            "JOIN cr.chat c " +
            "JOIN c.chatRoom r " +
            "WHERE r.id = :chatRoomId " +
            "GROUP BY cr.member.id")
    List<LastChatRead> findLastReadChatsBy(@Param("chatRoomId") Long chatRoomId);

    @Query("SELECT new com.chat.service.dtos.LastChatRead(cr.member.id, MAX(c.id))" +
            " FROM ChatRead cr" +
            " JOIN Chat c ON c.id = cr.chat.id" +
            " WHERE cr.member.id = :memberId" +
            " AND c.chatRoom.id = :chatRoomId" +
            " AND cr.isRead = true" +
            " GROUP BY cr.member.id" +
            " ORDER BY MAX(c.id) DESC")
    List<LastChatRead> findLastReadChatBy(@Param("memberId") Long memberId, @Param("chatRoomId") Long chatRoomId);

    @Modifying
    @Query("UPDATE ChatRead cr " +
            "SET cr.isRead = true " +
            "WHERE cr.chat.chatRoom.id = :chatRoomId " +
            "AND cr.member.id = :memberId " +
            "AND cr.isRead = false")
    int updateUnreadChatReadsToRead(@Param("memberId") Long memberId,
                                    @Param("chatRoomId") Long chatRoomId);

    @Query("SELECT new com.chat.repository.dtos.ChatUnreadCount(cr.chat.id, COUNT(cr))" +
            " FROM ChatRead cr" +
            " where cr.chat.id IN :chatIds" +
            " And cr.isRead = false" +
            " group by cr.chat.id")
    List<ChatUnreadCount> countUnreadByChatIds(@Param("chatIds") List<Long> chatIds);

    @Query("SELECT new com.chat.repository.dtos.MemberUnreadCount(cre.member.id, COUNT(cre))" +
            " FROM ChatRead cre" +
            " JOIN cre.chat c" +
            " WHERE c.chatRoom.id = :chatRoomId" +
            " AND cre.member.id IN :memberIds" +
            " AND cre.isRead = false" +
            " GROUP BY cre.member.id")
    List<MemberUnreadCount> findUnReadCountsBy(@Param("chatRoomId") Long chatRoomId,
                                                      @Param("memberIds") List<Long> memberIds);
}
