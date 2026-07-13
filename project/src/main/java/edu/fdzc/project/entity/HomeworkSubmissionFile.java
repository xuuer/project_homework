package edu.fdzc.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 学生作业提交附件表
 * @TableName homework_submission_file
 */
@TableName(value ="homework_submission_file")
@Data
public class HomeworkSubmissionFile {
    /**
     * 提交附件ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 作业id
     */
    private Long homeworkId;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 上传时间
     */
    private String  uploadTime;
}