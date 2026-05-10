package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.bkhouse.entity.ChatRoom;
import com.api.bkhouse.entity.response.IChatRoom;
import com.api.bkhouse.entity.response.IEnableUserChat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    boolean existsByFirstUserIdAndSecondUserIdAndEnable(UUID firstUserId, UUID secondUserId, boolean enable);
    Optional<ChatRoom> findByIdAndEnable(Integer id, boolean enable);
    Optional<ChatRoom> findByEnableAndFirstUserIdAndSecondUserId(boolean enable, UUID firstUserId, UUID secondUserId);
    @Query(value = "select cr.id, u.avatar_url as avatarUrl, m.content as message, cast(m.sender_id as varchar) as createBy,\n" +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName\n" +
            "from chat_rooms cr, users u, messages m \n" +
            "where m.id = (select id from messages where chat_room_id = cr.id order by created_at desc limit 1)\n" +
            "and cr.first_user_id = u.id\n" +
            "and u.is_enabled = true \n" +
            "and cr.second_user_id = :userId\n" +
            "and cr.is_active = true\n" +
            "union\n" +
            "select cr.id, u.avatar_url as avatarUrl, m.content as message, cast(m.sender_id as varchar) as createBy,\n" +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName\n" +
            "from chat_rooms cr, users u, messages m \n" +
            "where m.id = (select id from messages where chat_room_id = cr.id order by created_at desc limit 1)\n" +
            "and cr.second_user_id = u.id\n" +
            "and cr.is_active = true\n" +
            "and u.is_enabled = true \n" +
            "and cr.first_user_id = :userId ", nativeQuery = true)
    List<IChatRoom> findChatRoomOfUser(@Param("userId") UUID userId);

    @Query(value = "select cr.id, u.avatar_url as avatarUrl, ' ' as message, cast(cr.created_by as varchar) as createBy,\n" +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName\n" +
            "from users u, chat_rooms cr\n" +
            "where cr.id not in (select distinct chat_room_id from messages)\n" +
            "and cr.first_user_id = u.id\n" +
            "and u.is_enabled = true\n" +
            "and cr.second_user_id = :userId \n" +
            "union \n" +
            "select cr.id, u.avatar_url as avatarUrl, ' ' as message, cast(cr.created_by as varchar) as createBy,\n" +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName\n" +
            "from users u, chat_rooms cr\n" +
            "where cr.id not in (select distinct chat_room_id from messages)\n" +
            "and cr.second_user_id = u.id\n" +
            "and u.is_enabled = true\n" +
            "and cr.first_user_id = :userId",nativeQuery = true)
    List<IChatRoom> findChatRoomNoChatOfUser(@Param("userId") UUID userId);

    @Query(value = "select cr.id, '/assets/images/user.png' as avatarUrl, m.content as message, cast(m.sender_id as varchar) as createBy,\n" +
            "'Ẩn danh' as fullName\n" +
            "from chat_rooms cr, messages m\n" +
            "where m.id = (select id from messages where chat_room_id = cr.id order by created_at desc limit 1)\n" +
            "and cr.is_active = true\n" +
            "and cr.second_user_id = :adminId\n" +
            "and cr.created_by = :anonymousId", nativeQuery = true)
    List<IChatRoom> findAnonymousChatRoomOfAdmin(@Param("adminId") UUID adminId, @Param("anonymousId") UUID anonymousId);

    @Query(value = "select cast(u.id as varchar) as id, u.avatar_url as avatarUrl,\n" +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName, \n" +
            "u.phone_number as phoneNumber\n" +
            "from users u\n" +
            "where u.id not in \n" +
            "\t(select distinct first_user_id as user_id from chat_rooms where second_user_id = :userId \n" +
            "\tunion\n" +
            "\tselect distinct second_user_id as user_id from chat_rooms where first_user_id = :userId ) \n" +
            "and u.id != :anonymousId \n" +
            "and u.id != :userId \n" +
            "and u.is_enabled = true", nativeQuery = true)
    List<IEnableUserChat> getListUserEnableChat(@Param("userId") UUID userId, @Param("anonymousId") UUID anonymousId);
}
