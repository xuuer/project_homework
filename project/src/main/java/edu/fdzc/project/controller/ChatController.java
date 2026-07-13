package edu.fdzc.project.controller;
//package org.zc.net.controller;

import edu.fdzc.project.common.Result;
import edu.fdzc.project.entity.Chat;
import edu.fdzc.project.entity.dto.ChatDto;
import edu.fdzc.project.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/chat")
@Tag(name = "ChatController", description = "AI管理Controller")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/brain")
    public Result<String> getCoze(@RequestBody ChatDto dto) {
        return Result.success(chatService.getCoze(dto));
    }

    @PostMapping("/exam/{examId}/student/{studentId}")
    public Result<String> postCoze(@PathVariable("examId") Long examId,
                                   @PathVariable("studentId") Long studentId) {
        return Result.success(chatService.postCoze(examId, studentId));
    }
    @PostMapping("/homework/{homeworkId}/student/{studentId}")
    @Operation(summary = "AI自动批改作业")
    public Result<Object> markHomework(@PathVariable("homeworkId") Long homeworkId,
                                       @PathVariable("studentId") Long studentId) {
        return Result.success("批阅完成", chatService.postCozeHomework(homeworkId, studentId));
    }

    @PostMapping(value = "/coze/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "流式获取AI回答")
    public SseEmitter streamCoze(@RequestBody ChatDto dto) {
        return chatService.streamCoze(dto);
    }

    @GetMapping("/history/{id}")
    @Operation(summary = "获取对话历史")
    public Result<List<Chat>> selectHistory(@PathVariable("id") Long id) {
        return Result.success(chatService.selectHistory(id));
    }

    @DeleteMapping("/history/{id}")
    @Operation(summary = "删除对话历史")
    public Result<Object> deleteHistory(@PathVariable("id") Long id) {
        return Result.success("删除成功", chatService.deleteHistory(id));
    }

}
