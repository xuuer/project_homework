package edu.fdzc.project.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "分组树VO")
public class CategoryVo {

    @Schema(name = "分组ID")
    private Long id;

    @Schema(name = "分组名")
    private String label;

    @Schema(name = "节点值", description = "值为分组ID")
    private Long value;

    @Schema(name = "子节点")
    private List<CategoryVo> children;

}
