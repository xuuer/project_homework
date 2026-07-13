package edu.fdzc.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName tag_node
 */
@TableName(value ="tag_node")
@Data
public class TagNode {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private String category;

    /**
     * 
     */
    private String description;

    /**
     * 
     */
    private Long teacherId;

    /**
     * 
     */
    private Long categoryId;
}