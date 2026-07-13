package edu.fdzc.project.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.fdzc.project.config.CozeConfig;
import edu.fdzc.project.entity.*;
import edu.fdzc.project.entity.dto.ChatDto;
import edu.fdzc.project.entity.dto.QuestionDto;
import edu.fdzc.project.entity.vo.ChatHomeworkVo;
import edu.fdzc.project.mapper.*;
import edu.fdzc.project.service.ChatService;
import edu.fdzc.project.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhangjiu
 * @description 针对表【chat】的数据库操作Service实现
 * @createDate 2025-06-21 17:10:34
 */

@Slf4j
@Service
public class  ChatServiceImpl extends ServiceImpl<ChatMapper, Chat>
        implements ChatService{

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private CozeConfig cozeConfig;

    @Autowired
    private StudentExamQuestionMapper studentExamQuestionMapper;

    @Autowired
    private StudentExamMapper studentExamMapper;

    @Autowired
    private HomeworkMapper homeworkMapper;

    @Autowired
    private HomeworkAttachmentMapper homeworkAttachmentMapper;

    @Autowired
    private HomeworkSubmissionMapper homeworkSubmissionMapper;

    @Autowired
    private HomeworkSubmissionFileMapper homeworkSubmissionFileMapper;

    @Override
    public String getCoze(ChatDto dto) {
        String s = cozeConfig.analyzeTag(dto.getMsg());
        return s;
    }

    @Override
    public String postCoze(Long examId, Long studentId) {
        List<QuestionDto> paper = studentExamQuestionMapper.selectQuestion(studentId, examId);
        List<QuestionDto> list = new ArrayList<>();
        double totalScore = 0.0;
        for(QuestionDto q : paper) {
            if(q.getType().equals("综合题") && q.getStudentScore() == null) {
                list.add(q);
            } else {
                totalScore += q.getStudentScore();
            }
        }
        JSONArray array = JSONUtil.parseArray(list);
        // 调用AI
        log.info("--------------------------------------------------------------------");
        log.info("提交作业: {}", array);
        JSONArray c = cozeConfig.markPaper(array);

        // AI批改结果记录到数据库
        for(int i = 0; i < c.size(); i ++) {
            JSONObject d = c.getJSONObject(i);
            Long questionId = d.getLong("id");
            Double studentScore = d.getDouble("studentScore");
            String remark = d.getStr("remark");

            totalScore += studentScore;

            StudentExamQuestion seq = new StudentExamQuestion();
            seq.setScore(studentScore);
            seq.setRemark(remark);
            studentExamQuestionMapper.update(
                    seq,
                    new LambdaQueryWrapper<StudentExamQuestion>()
                            .eq(StudentExamQuestion::getStudentId, studentId)
                            .eq(StudentExamQuestion::getExamId, examId)
                            .eq(StudentExamQuestion::getQuestionId, questionId));
        }

        StudentExam se = new StudentExam();
        se.setScore(totalScore);
        se.setStatus("已批阅");
        studentExamMapper.update(
                se,
                new LambdaQueryWrapper<StudentExam>()
                        .eq(StudentExam::getStudentId, studentId)
                        .eq(StudentExam::getExamId, examId));

        return "ok";
    }

    @Override
    public SseEmitter streamCoze(ChatDto dto) {
        Long userId = JwtUtil.getUserId();
        // 定义日期时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 获取当前时间并格式化为字符串
        String userChatTime = LocalDateTime.now().format(formatter);
        // 创建Chat对象来存储用户聊天信息
        Chat userChat = new Chat();
        userChat.setType("user"); // 设置消息类型为用户
        userChat.setContent(dto.getMsg()); // 设置消息内容
        userChat.setCreateTime(userChatTime); // 设置消息创建时间
        userChat.setUserId(userId); // 设置用户ID
        userChat.setCourseId(dto.getCourseId()); // 设置班课ID
        // 将用户聊天信息插入数据库
        chatMapper.insert(userChat);

        // 基于历史对话进行回复
        JSONArray array = JSONUtil.createArray();
        if(dto.getMemory()) {
            List<Chat> chats = selectHistory(dto.getCourseId());
            for(Chat chat : chats) {
                array.add(chat.getType() + "说：" + chat.getContent());
            }
        }

        // 创建一个SSE发射器，设置超时时间为Long.MAX_VALUE
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        // 创建一个固定大小的线程池
        ExecutorService service = Executors.newFixedThreadPool(10);

        // 调用AI服务获取回答，返回一个Flux<String>对象
        Flux<String> flux = cozeConfig.getAIAnswer(array, dto.getMsg());

        // 用于拼接AI回答内容的字符串构建器
        StringBuilder sb = new StringBuilder();

        // 提交一个任务到线程池执行
        service.submit(() -> {
            // 订阅Flux<String>对象，处理AI的回答
            flux.subscribe(
                    res -> { // 当Flux发出一个元素时执行
                        JSONObject resJson = JSONUtil.parseObj(res); // 将JSON字符串解析为JSONObject
                        String s = resJson.getStr("content"); // 从JSONObject中获取"content"字段的值
                        if (s != null) {
                            try {
                                // 通过SSE发射器发送AI的回答给客户端
                                emitter.send(SseEmitter.event().data(s));
                                // 将AI的回答追加到字符串构建器中
                                sb.append(s);
                            } catch (IOException e) {
                                // 处理发送SSE事件时可能发生的IO异常
                                // e.printStackTrace();
                            }
                        }
                    },
                    error -> { // 当Flux发生错误时执行
                        // 处理错误
                        // error.printStackTrace();
                    },
                    () -> { // 当Flux完成时执行
                        // 完成SSE发射器的发送
                        emitter.complete();

                        // 获取当前时间并格式化为字符串，用于AI回答的创建时间
                        String aiChatTime = LocalDateTime.now().format(formatter);
                        // 创建Chat对象来存储AI聊天信息
                        Chat aiChat = new Chat();
                        aiChat.setType("ai"); // 设置消息类型为AI
                        aiChat.setContent(sb.toString()); // 设置消息内容，即AI的所有回答拼接后的字符串
                        aiChat.setCreateTime(aiChatTime); // 设置消息创建时间
                        aiChat.setUserId(userId); // 设置用户ID
                        aiChat.setCourseId(dto.getCourseId()); // 设置班课ID
                        // 将AI聊天信息插入数据库
                        chatMapper.insert(aiChat);
                    }
            );
        });

        // 返回SSE发射器，以便客户端可以接收SSE事件
        return emitter;
    }

    @Override
    public List<Chat> selectHistory(Long courseId) {
        Long userId = JwtUtil.getUserId();

        Page<Chat> chatPage = chatMapper.selectPage(
                new Page<>(1, 20),
                new LambdaQueryWrapper<Chat>()
                        .eq(Chat::getCourseId, courseId)
                        .eq(Chat::getUserId, userId)
                        .orderByDesc(Chat::getCreateTime));
        return chatPage.getRecords();
    }

    @Override
    public Object deleteHistory(Long courseId) {
        Long userId = JwtUtil.getUserId();

        return chatMapper.delete(
                new LambdaQueryWrapper<Chat>()
                        .eq(Chat::getCourseId, courseId)
                        .eq(Chat::getUserId, userId));
    }

    @Override
    public Object postCozeHomework(Long homeworkId, Long studentId) {
        ChatHomeworkVo chatHomeworkVo = new ChatHomeworkVo();
        Homework homework = homeworkMapper.selectById(homeworkId);
        chatHomeworkVo.setHomeworkId(homework.getId()); //设置作业ID
        chatHomeworkVo.setHomeworkScore(homework.getHomeworkScore());//设置作业分数
        chatHomeworkVo.setStudentId(studentId); //设置学生ID
        chatHomeworkVo.setDescription(homework.getDescription()); // 设置作业描述
        chatHomeworkVo.setAnswer(homework.getAnswer()); //设置作业答案


        List<HomeworkAttachment> attachment = homeworkAttachmentMapper.selectByHomeAssigId(homeworkId);
        List<String> list = new ArrayList<>();
        for(HomeworkAttachment attach : attachment) {
            list.add("http://8.148.218.255:9000/kkfileview/"+attach.getFileUrl());
        }
        chatHomeworkVo.setAttachment(list); //设置作业的文件

        HomeworkSubmission homeworkSubmission = homeworkSubmissionMapper.selectByStuHomId(homeworkId,studentId);

        chatHomeworkVo.setTextcontent(homeworkSubmission.getTextContent()); //设置学生的文本描述

        List<HomeworkSubmissionFile> homeworkSubmissionFile = homeworkSubmissionFileMapper.selectByStuHomeId(homeworkId,studentId);
        List<String> files = new ArrayList<>();
        for(HomeworkSubmissionFile file : homeworkSubmissionFile) {
            files.add("网址"+file.getFileUrl());
        }
        chatHomeworkVo.setStudentSub(files); //学生提交文件

        String json = JSON.toJSONString(chatHomeworkVo, JSONWriter.Feature.PrettyFormat);// 转换为json格式
        log.info("--------------------------------------------------------------------");
        log.info("提交作业: {}", json);
        JSONObject c = (JSONObject) cozeConfig.markHomework(json); //接收

        log.info("--------------------------------------------------");
        log.info("批改结果: {}", c);
        homeworkSubmission.setComment(c.get("comment").toString());
        homeworkSubmission.setScore((Integer) c.get("score"));



        homeworkSubmission.setChangStatus("true"); //设置为提交
        homeworkSubmissionMapper.updateById(homeworkSubmission);//实现批改

        return c;//用于测试，先不做修改
    }
}




