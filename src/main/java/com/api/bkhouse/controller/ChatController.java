package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.config.annotation.CurrentUser;
import com.api.bkhouse.entity.User;
import com.api.bkhouse.payload.dto.UserDTO;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.security.services.UserDetailsImpl;
import com.api.bkhouse.service.UserService;
import com.api.bkhouse.util.Util;
import com.api.bkhouse.entity.ChatRoom;
import com.api.bkhouse.entity.Message;
import com.api.bkhouse.payload.dto.ChatRoomDTO;
import com.api.bkhouse.payload.dto.MessageDTO;
import com.api.bkhouse.service.ChatService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/chat")
@PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/chat-room")
    public ResponseEntity<BaseResponse> createChatRoom(@RequestBody ChatRoomDTO chatRoomDTO, @CurrentUser UserDetailsImpl userDetails) {
        try {
            if (chatService.existsByFirstUserAndSecondUser(chatRoomDTO.getFirstUserId(), chatRoomDTO.getSecondUserId())) {
                return ResponseEntity.ok(new BaseResponse(
                        null,
                        "Phòng chat đã tồn tại",
                        HttpStatus.CREATED
                ));
            }
            chatRoomDTO.setId(null);
            chatRoomDTO.setEnable(true);
            chatRoomDTO.setCreateBy(userDetails.getId());
            chatRoomDTO.setCreateAt(Util.getCurrentDateTime());
            ChatRoom chatRoom = chatService.createChatRoom(modelMapper.map(chatRoomDTO, ChatRoom.class));
            return ResponseEntity.ok(new BaseResponse(
                    modelMapper.map(chatRoom, ChatRoomDTO.class),
                    "", HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi tạo phòng chat " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/chat-room/detail/{roomId}")
    public ResponseEntity<BaseResponse> chatRoomDetail(@PathVariable("roomId") Integer roomId, @CurrentUser UserDetailsImpl userDetails) {
        try {
            ChatRoom chatRoom = chatService.findChatRoomById(roomId);
            if (chatRoom == null) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy thông tin cuộc hội thoại", HttpStatus.NO_CONTENT));
            }
            if (!chatRoom.getFirstUserId().equals(userDetails.getId()) && !chatRoom.getSecondUserId().equals(userDetails.getId())) {
                return ResponseEntity.ok(new BaseResponse(null, "Bạn không có quyền lấy thông tin cuộc hội thoại", HttpStatus.NOT_ACCEPTABLE));
            }
            return ResponseEntity.ok(new BaseResponse(modelMapper.map(chatRoom, ChatRoomDTO.class), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách tin nhắn của đoạn chat " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @DeleteMapping("/chat-room/{roomId}")
    public ResponseEntity<BaseResponse> deleteChatRoom(@PathVariable("roomId") Integer roomId, @CurrentUser UserDetailsImpl userDetails) {
        try {
            ChatRoom chatRoom = chatService.findChatRoomById(roomId);
            
            if (chatRoom == null) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy thông tin cuộc trò chuyện", HttpStatus.NO_CONTENT));
            }
            if (!chatRoom.getFirstUserId().equals(userDetails.getId()) && !chatRoom.getSecondUserId().equals(userDetails.getId())) {
                return ResponseEntity.ok(new BaseResponse(null, "Bạn không có quyền xóa cuộc trò chuyện", HttpStatus.NOT_ACCEPTABLE));
            }
            chatRoom.setEnable(Boolean.FALSE);
            chatRoom.setUpdateAt(Util.getCurrentDateTime());
            chatService.createChatRoom(chatRoom);
            return ResponseEntity.ok(new BaseResponse(chatRoom.getId(), "Xóa cuộc hội thoại thành công.", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi xóa cuộc hội thoại " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/chat-room/user")
    public ResponseEntity<BaseResponse> findChatRoomByUser(@CurrentUser UserDetailsImpl userDetails) {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    chatService.findChatRoomByUserId(userDetails.getId()), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách cuộc hội thoại của người dùng " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @PostMapping("/message")
    public ResponseEntity<BaseResponse> createMessage(@RequestBody MessageDTO messageDTO, @CurrentUser UserDetailsImpl userDetails) {
        try {
            ChatRoom chatRoom = chatService.findChatRoomById(messageDTO.getChatRoomId());
            if (chatRoom == null) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy thông tin cuộc trò chuyện", HttpStatus.NO_CONTENT));
            }
            if (!chatRoom.getFirstUserId().equals(userDetails.getId()) && !chatRoom.getSecondUserId().equals(userDetails.getId())) {
                return ResponseEntity.ok(new BaseResponse(null, "Bạn không có quyền gửi tin nhắn", HttpStatus.NOT_ACCEPTABLE));
            }
            messageDTO.setCreateAt(Util.getCurrentDateTime());
            messageDTO.setCreateBy(userDetails.getId().toString());
            Message message = modelMapper.map(messageDTO, Message.class);
            message.setChatRoom(chatRoom);
            message.setId(null);
            Message response = chatService.saveMessage(message);
            return ResponseEntity.ok(new BaseResponse(response, "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi gửi tin nhắn " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/user/enable")
    public ResponseEntity<BaseResponse> getChatUserEnable(@CurrentUser UserDetailsImpl userDetails) {
        try {
            return ResponseEntity.ok(new BaseResponse(chatService.getEnableUserChat(userDetails.getId()), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách người dùng có thể nhắn tin " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }
}
