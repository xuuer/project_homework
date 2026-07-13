package edu.fdzc.project.entity.dto;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "试题DTO")
@TableName(value = "QuestionDto", autoResultMap = true)
public class QuestionDto {

    @Schema(name = "题目ID")
    private Long id;

    @Schema(name = "题目类型")
    private String type;

    @Schema(name = "题干")
    private String content;

    @Schema(name = "选项")
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> options;

    @Schema(name = "题目答案")
    private String answer;

    @Schema(name = "题目解析")
    private String analysis;

    @Schema(name = "题目难度")
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

    @Schema(name = "题目分值")
    private Double score;

    @Schema(name = "题目顺序")
    private Long sort;

    @Schema(name = "评分误差")
    private Double markError;

    @Schema(name = "学生答案")
    private String studentAnswer;

    @Schema(name = "学生得分")
    private Double studentScore;

    @Schema(name = "备注")
    private String remark;

}

