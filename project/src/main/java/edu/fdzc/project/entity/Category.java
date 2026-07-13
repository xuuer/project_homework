package edu.fdzc.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(title = "分组")
public class Category implements Serializable {

    @Schema(name = "分组ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(name = "分组名")
    private String name;

    @Schema(name = "班课ID")
    private Long courseId;

    @Schema(name = "父级分组ID")
    private Long parentId;

    @Schema(name = "分组类型", description = "资源库/题库/知识图谱/班课资源")
    private String type;

    @Schema(name = "教师ID")
    private Long teacherId;

}
