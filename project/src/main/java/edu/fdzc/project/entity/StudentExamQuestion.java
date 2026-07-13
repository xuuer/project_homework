package edu.fdzc.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(title = "学生测验题目")
public class StudentExamQuestion implements Serializable {

    @Schema(name = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(name = "学生ID")
    private Long studentId;

    @Schema(name = "测验ID")
    private Long examId;

    @Schema(name = "题目ID")
    private Long questionId;

    @Schema(name = "学生得分")
    private Double score;

    @Schema(name = "学生答案")
    private String answer;

    @Schema(name = "备注")
    private String remark;

}
