package edu.fdzc.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 作业附件表
 * @TableName homework_attachment
 */
@TableName(value ="homework_attachment")
@Data
public class HomeworkAttachment {
    /**
     * 附件ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联作业ID
     */
    private Long homeworkId;

    /**
     * 文件原始名
     */
    private String fileName;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 上传时间
     */
    private String uploadTime;
}