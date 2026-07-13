package edu.fdzc.project.entity.vo;

import edu.fdzc.project.entity.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
//import org.zc.net.entity.Resource;

import java.util.List;

@Data
@Schema(title = "资源分组VO")
public class CategoryResourceVo {

    @Schema(name = "分组ID")
    private Long id;

    @Schema(name = "分组名")
    private String name;

    @Schema(name = "资源")
    private List<Resource> children;

}

