package edu.fdzc.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 
 * @TableName course
 */
@TableName(value ="course")
@Data
public class Course {
    @Schema(name = "班课ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(name = "班课名")
    private String name;

    @Schema(name = "学期")
    private String semester;

    @Schema(name = "教师ID")
    private Long teacherId;

    @Schema(name = "封面")
    private String cover;

    @Schema(name = "班级")
    private String clazz;

    @Schema(name = "知识点分组ID")
    private Long tagCategoryId;

    @Schema(name = "题库分组ID")
    private Long questionCategoryId;

    @Schema(name = "创建时间")
    private String createTime;

    @Schema(name = "修改时间")
    private String updateTime;
}