package edu.fdzc.project.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "学生知识点掌握情况Vo")
public class StudentTagNodeVo {

    @Schema(name = "知识点ID")
    private Long id;

    @Schema(name = "知识点名称")
    private String name;

    @Schema(name = "学生掌握情况", description = "未学/优秀/良好/薄弱")
    private String category;

    @Schema(name = "知识点描述")
    private String description;

    @Schema(name = "学生掌握程度", description = "取值0到100")
    private Double value;

}

