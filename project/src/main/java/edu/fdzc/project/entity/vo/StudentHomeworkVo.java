package edu.fdzc.project.entity.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class StudentHomeworkVo {


    @Schema(name = "学生ID")
    private Long id;

    @Schema(name = "学号")
    private String no;

    @Schema(name = "姓名")
    private String name;

    @Schema(name = "成绩")
    private Integer score;

    @Schema(name = "学生开始测验时间")
    private String startTime;


    @Schema(name = "状态", description = "进行中/待批阅/已批阅")
    private String status;

    @Schema(name = "评语")
    private String comment;
}
