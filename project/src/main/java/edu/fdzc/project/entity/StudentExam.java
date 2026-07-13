package edu.fdzc.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(title = "学生测验")
public class StudentExam implements Serializable {

    @Schema(name = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(name = "学生ID")
    private Long studentId;

    @Schema(name = "测验ID")
    private Long examId;

    @Schema(name = "学生开始测验时间")
    private String startTime;

    @Schema(name = "学生结束测验时间")
    private String endTime;

    @Schema(name = "学生成绩")
    private Double score;

    @Schema(name = "状态", description = "进行中/待批阅/已批阅")
    private String status;

}
