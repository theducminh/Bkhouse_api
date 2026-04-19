package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
    @Query(value = "select cr.id, u.avatar_url as avatarUrl, m.message, m.create_by as createBy,\n" +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName\n" +
            "from chat_room cr, user u, message m \n" +
            "where m.id = (select id from message where chat_room_id = cr.id order by create_at desc limit 1)\n" +
            "and cr.first_user_id = u.id\n" +
            "and u.enable = 1 \n" +
            "and cr.second_user_id = :userId\n" +
            "and cr.enable = 1\n" +
            "union\n" +
            "select cr.id, u.avatar_url as avatarUrl, m.message, m.create_by as createBy,\n" +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName\n" +
            "from chat_room cr, user u, message m \n" +
            "where m.id = (select id from message where chat_room_id = cr.id order by create_at desc limit 1)\n" +
            "and cr.second_user_id = u.id\n" +
            "and cr.enable = 1\n" +
            "and u.enable = 1 \n" +
            "and cr.first_user_id = :userId ", nativeQuery = true)
    List<IChatRoom> findChatRoomOfUser(UUID userId);

    @Query(value = "select cr.id, u.avatar_url as avatarUrl, ' ' as message, cr.create_by as createBy,\n" +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName\n" +
            "from user u, chat_room cr\n" +
            "where cr.id not in (select distinct chat_room_id from message)\n" +
            "and cr.first_user_id = u.id\n" +
            "and u.enable = 1\n" +
            "and cr.second_user_id = :userId \n" +
            "union \n" +
            "select cr.id, u.avatar_url as avatarUrl, ' ' as message, cr.create_by as createBy,\n" +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName\n" +
            "from user u, chat_room cr\n" +
            "where cr.id not in (select distinct chat_room_id from message)\n" +
            "and cr.second_user_id = u.id\n" +
            "and u.enable = 1\n" +
            "and cr.first_user_id = :userId",nativeQuery = true)
    List<IChatRoom> findChatRoomNoChatOfUser(UUID userId);

    @Query(value = "select cr.id, '/assets/images/user.png' as avatarUrl, m.message, m.create_by as createBy,\n" +
            "'Ẩn danh' as fullName\n" +
            "from chat_room cr, message m\n" +
            "where m.id = (select id from message where chat_room_id = cr.id order by create_at desc limit 1)\n" +
            "and cr.enable = 1\n" +
            "and cr.second_user_id = 'admin'\n" +
            "and cr.create_by = 'anonymous'", nativeQuery = true)
    List<IChatRoom> findAnonymousChatRoomOfAdmin();

    @Query(value = "select u.id, u.avatar_url as avatarUrl,\n" +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName, \n" +
            "u.phone_number as phoneNumber\n" +
            "from user u\n" +
            "where u.id not in \n" +
            "\t(select distinct first_user_id as user_id from chat_room where second_user_id = :userId \n" +
            "\tunion\n" +
            "\tselect distinct second_user_id as user_id from chat_room where first_user_id = :userId ) \n" +
            "and u.id != 'anonymous' \n" +
            "and u.id != :userId \n" +
            "and u.enable = 1", nativeQuery = true)
    List<IEnableUserChat> getListUserEnableChat(UUID userId);
}
