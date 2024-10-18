// package com.nailcase.controller;
//
// import java.util.List;
//
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.servlet.ModelAndView;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
// import com.nailcase.model.dto.ChatMessageDto;
// import com.nailcase.model.dto.ChatRoomDto;
// import com.nailcase.service.ChatRoomService;
//
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
//
// @Controller
// @RequiredArgsConstructor
// @RequestMapping(value = "/chat")
// @Slf4j
// public class RoomController {
//
// 	private final ChatRoomService chatRoomService;
//
// 	//채팅방 목록 조회
// 	@GetMapping("/rooms")
// 	public ModelAndView rooms() {
//
// 		log.info("# All Chat Rooms");
// 		ModelAndView mv = new ModelAndView("chat/rooms");
//
// 		mv.addObject("list", chatRoomService.findAll());
//
// 		return mv;
// 	}
//
// 	//채팅방 개설
// 	@PostMapping(value = "/room")
// 	public String create(@RequestParam String name, RedirectAttributes rttr) {
//
// 		log.info("# Create Chat Room , name: " + name);
// 		ChatRoomDto chatRoom = ChatRoomDto.builder().name(name).build();
// 		rttr.addFlashAttribute("roomName", chatRoomService.saveRoom(chatRoom));
// 		return "redirect:/chat/rooms";
// 	}
//
// 	@GetMapping("/room")
// 	public String getMessagesInRoom(@RequestParam Long chatRoomId, @RequestParam Long memberId, Model model) {
// 		log.info("# Get Chat Room, chatRoomId: " + chatRoomId);
//
// 		ChatRoomDto chatRoom = chatRoomService.findByRoomId(chatRoomId);
// 		if (chatRoom == null) {
// 			log.warn("Chat room not found with ID: {}", chatRoomId);
// 			return "redirect:/chat/rooms";  // 채팅방이 존재하지 않으면 목록으로 리다이렉트
// 		}
//
// 		List<ChatMessageDto> messages = chatRoomService.findMessagesByRoomId(chatRoomId);
// 		log.info("!!!!!!!!Loaded messages: {}", messages);
// 		model.addAttribute("room", chatRoom);
// 		model.addAttribute("messages", messages);
// 		model.addAttribute("memberId", memberId);
//
// 		return "chat/room";  // 채팅방 세부사항을 보여주는 뷰
// 	}
// }