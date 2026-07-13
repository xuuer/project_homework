package edu.fdzc.project.service;

import edu.fdzc.project.entity.Chat;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.fdzc.project.entity.dto.ChatDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
* @author zhangjiu
* @description 针对表【chat】的数据库操作Service
* @createDate 2025-06-21 17:10:34
*/
public interface ChatService extends IService<Chat> {

    String getCoze(ChatDto dto);

    String postCoze(Long examId, Long studentId);

    SseEmitter streamCoze(ChatDto dto);

    List<Chat> selectHistory(Long id);

    Object deleteHistory(Long id);

    Object postCozeHomework(Long homeworkId, Long studentId);
}
