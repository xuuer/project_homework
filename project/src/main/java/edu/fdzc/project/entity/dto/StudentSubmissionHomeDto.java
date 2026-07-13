package edu.fdzc.project.entity.dto;

import edu.fdzc.project.entity.HomeworkAttachment;
import edu.fdzc.project.entity.HomeworkSubmissionFile;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StudentSubmissionHomeDto {
    /**
     * 关联作业ID
     */
    private Long homeworkId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 作业文本内容
     */
    private String textContent;

    /**
     * 提交时间
     */
    private String submitTime; //提交时间

    /**
     * 教师评分
     */
    private Integer score; //设置的分数

    /**
     * 教师评语
     */
    private String comment; //老师的评论


    List<HomeworkSubmissionFile> files; //学生提交的文件


    /**
     * 提交状态 true / false
     */
    private String status;

    /**
     *  批阅状态 true / false
     */
    private String changeStatus;


    /**
     * 布置作业描述
     */
    private String description;


    /**
     * 作业文件
     */
    List<HomeworkAttachment> attachments;

    // 默认构造函数
    public StudentSubmissionHomeDto() {
        // 为防止空指针异常，初始化集合
        this.files = new java.util.ArrayList<>();
        this.attachments = new java.util.ArrayList<>();
    }

    // 便捷的构造函数，用于简单的批改场景
    public StudentSubmissionHomeDto(Integer score, String comment) {
        this();
        this.score = score;
        this.comment = comment;
        this.changeStatus = "true";
    }
}
