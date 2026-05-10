package com.api.bkhouse.service;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.ChatRoom;
import com.api.bkhouse.entity.Message;
import com.api.bkhouse.entity.response.IChatRoom;
import com.api.bkhouse.entity.response.IEnableUserChat;
import com.api.bkhouse.repository.ChatRoomRepository;
import com.api.bkhouse.repository.MessageRepository;
import com.api.bkhouse.util.Util;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    private final UUID ADMIN_ID = UUID.fromString("00000000-0000-0000-0000-000000000000"); 
    private final UUID ANONYMOUS_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Transactional
    public ChatRoom createChatRoom(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoom findChatRoomById(Integer id) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findByIdAndEnable(id, Boolean.TRUE);
        if (chatRoomOptional.isEmpty()) {
            return null;
        } else {
            return chatRoomOptional.get();
        }
    }

    public List<IChatRoom> findChatRoomByUserId(UUID userId) {
//        return chatRoomRepository.findByEnableAndFirstUserIdOrSecondUserId(Boolean.TRUE, userId, userId);
        List<IChatRoom> response = chatRoomRepository.findChatRoomOfUser(userId);
        List<IChatRoom> response2 = chatRoomRepository.findChatRoomNoChatOfUser(userId);
        if (userId.equals(ADMIN_ID)) {
            List<IChatRoom> response3 = chatRoomRepository.findAnonymousChatRoomOfAdmin(ADMIN_ID, ANONYMOUS_ID);
            response.addAll(response3);
        }
        response.addAll(response2);
        return response;
    }

    public boolean existsByFirstUserAndSecondUser(UUID firstUserId, UUID secondUserId) {
        boolean a = chatRoomRepository.existsByFirstUserIdAndSecondUserIdAndEnable(firstUserId, secondUserId, Boolean.TRUE);
        boolean b = chatRoomRepository.existsByFirstUserIdAndSecondUserIdAndEnable(secondUserId, firstUserId, Boolean.TRUE);
        return a || b;
    }

    public List<IEnableUserChat> getEnableUserChat(UUID userId) {
        return chatRoomRepository.getListUserEnableChat(userId, ANONYMOUS_ID);
    }

    public ChatRoom findAnonymousChatRoom(String userDeviceInfo) {

        UUID deviceUUID = UUID.nameUUIDFromBytes(userDeviceInfo.getBytes());

        Optional<ChatRoom> chatRoomOptional1 = chatRoomRepository.findByEnableAndFirstUserIdAndSecondUserId(Boolean.TRUE, deviceUUID, ADMIN_ID);
        Optional<ChatRoom> chatRoomOptional2 = chatRoomRepository.findByEnableAndFirstUserIdAndSecondUserId(Boolean.TRUE, ADMIN_ID, deviceUUID);
        if (chatRoomOptional1.isEmpty() && chatRoomOptional2.isEmpty()) {
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setAnonymous(true);
            chatRoom.setCreateAt(Util.getCurrentDateTime());
            chatRoom.setCreateBy(ANONYMOUS_ID);
            chatRoom.setEnable(true);
            chatRoom.setId(null);
            chatRoom.setFirstUserId(deviceUUID);
            chatRoom.setSecondUserId(ADMIN_ID);
            return chatRoomRepository.save(chatRoom);
        }
        if (chatRoomOptional1.isEmpty()) {
            return chatRoomOptional2.get();
        } else {
            return chatRoomOptional1.get();
        }
    }
    @Transactional
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }
}
