package edu.fdzc.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName tag_relationship
 */
@TableName(value ="tag_relationship")
@Data
public class TagRelationship {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long source;

    /**
     * 
     */
    private Long target;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private Long teacherId;

    /**
     * 
     */
    private Long categoryId;
}