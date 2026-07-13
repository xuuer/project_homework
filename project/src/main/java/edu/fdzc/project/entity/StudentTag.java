package edu.fdzc.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName student_tag
 */
@TableName(value ="student_tag")
@Data
public class StudentTag {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long studentId;

    /**
     * 
     */
    private String tag;

    /**
     * 
     */
    private Long rightNum;

    /**
     * 
     */
    private Long totalNum;
}