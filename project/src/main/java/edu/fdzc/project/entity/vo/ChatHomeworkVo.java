package edu.fdzc.project.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class ChatHomeworkVo {

    // homework + homewprkAttachment

    private Long homeworkId; // 作业ID

    private String description; //作业描述

    List<String> attachment; // 作业文件

    private Integer homeworkScore; //这次作业得分数

    private String answer; // 作业答案

    // homeworkSubmiss + homeworksubfile

    private Long studentId; //学生id

    private String textcontent; //学生回答

    List<String> studentSub; //学生提交的作业


}
