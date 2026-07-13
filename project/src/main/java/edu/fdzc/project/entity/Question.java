package edu.fdzc.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(title = "题目")
@TableName(autoResultMap = true)
public class Question implements Serializable {

    @Schema(name = "题目ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(name = "题型", description = "单选题/多选题/填空题")
    private String type;

    @Schema(name = "题干")
    private String content;

    @Schema(name = "选项")
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> options;

    @Schema(name = "正确答案")
    private String answer;

    @Schema(name = "题目解析")
    private String analysis;

    @Schema(name = "题目难度", description = "简单/中等/困难")
    private String difficulty;

    @Schema(name = "题目标签")
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> tags;

    @Schema(name = "教师ID")
    private Long teacherId;

    @Schema(name = "分组ID")
    private Long categoryId;

    @Schema(name = "创建时间")
    private String createTime;

    @Schema(name = "修改时间")
    private String updateTime;

}
