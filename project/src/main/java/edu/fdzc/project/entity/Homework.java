package edu.fdzc.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 作业表
 * @TableName homework
 */
@TableName(value ="homework")
@Data
public class Homework {
    /**
     * 作业ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属课程ID
     */
    private Long courseId;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 作业标题
     */
    private String title;

    /**
     * 作业说明
     */
    private String description;

    /**
     * 截止时间
     */
    private String deadline;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 分值
     */
    private Integer homeworkScore;

    /**
     * 答案
     */
    private String answer;

    @TableField(exist = false)
    private List<HomeworkAttachment> attachments;
}