package edu.fdzc.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 学生作业提交表
 * @TableName homework_submission
 */
@TableName(value ="homework_submission")
@Data
public class HomeworkSubmission {
    /**
     * 提交记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
    private String  submitTime;

    /**
     * 教师评分
     */
    private Integer score;

    /**
     * 教师评语
     */
    private String comment;

    /**
     * 提交状态
     */
    private String status;

    /**
     * 批阅 状态
     */
    private String changStatus;

}