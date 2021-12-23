package kr.omen.pico.controller;

import kr.omen.pico.dao.ChatRoomRepository;
import kr.omen.pico.domain.ChatRoom;
import kr.omen.pico.domain.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomRepository chatRoomRepository;

    @GetMapping("/chatroom/{chatroomidx}")
    public ResponseDTO.ChatRoomResponse findOneChatRoom(@PathVariable Long chatroomidx) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomidx).get();
        return new ResponseDTO.ChatRoomResponse(chatRoom);
    }
}
