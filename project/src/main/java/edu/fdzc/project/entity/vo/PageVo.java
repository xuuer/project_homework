package edu.fdzc.project.entity.vo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "分页VO")
public class PageVo<T> {

    @Schema(name = "查询结果总数")
    private Long total;

    @Schema(name = "分页数据列表")
    private List<T> list;

}