package edu.fdzc.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Schema(title = "测验题目")
public class ExamQuestion implements Serializable {

    @Schema(name = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(name = "测验ID")
    private Long examId;

    @Schema(name = "题目ID")
    private Long questionId;

    @Schema(name = "题目分值")
    private Double score;

    @Schema(name = "题目顺序")
    private Long sort;

    @Schema(name = "评分误差")
    private Double markError;

}
