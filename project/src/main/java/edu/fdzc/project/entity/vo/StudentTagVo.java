package edu.fdzc.project.entity.vo;

import edu.fdzc.project.entity.TagRelationship;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "学生知识图谱掌握情况Vo")
public class StudentTagVo {

    @Schema(name = "知识点节点")
    private List<StudentTagNodeVo> nodes;

    @Schema(name = "知识点关系")
    private List<TagRelationship> links;

}
