package edu.fdzc.project.entity.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "测验统计DTO")
public class StatisticsDto {

    @Schema(name = "最高分")
    private Double maxScore;

    @Schema(name = "最低分")
    private Double minScore;

    @Schema(name = "平均分")
    private Double avgScore;

}
