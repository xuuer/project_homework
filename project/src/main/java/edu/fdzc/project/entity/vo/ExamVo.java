package edu.fdzc.project.entity.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "测验Vo")
public class ExamVo {

    @Schema(name = "测验ID")
    private Long id;

    @Schema(name = "测验名")
    private String name;

    @Schema(name = "测验开放时间")
    private String openTime;

    @Schema(name = "测验关闭时间")
    private String closeTime;

    @Schema(name = "测验时长", description = "单位：分钟")
    private Long duration;

    @Schema(name = "描述")
    private String description;

    @Schema(name = "题目总数")
    private Long questionNum;

    @Schema(name = "总分")
    private Double totalScore;

    @Schema(name = "切屏时长", description = "单位：秒")
    private Long screenCutTime;

    @Schema(name = "切屏次数")
    private Long screenCutNum;

    @Schema(name = "创建时间")
    private String createTime;

    @Schema(name = "修改时间")
    private String updateTime;

}
