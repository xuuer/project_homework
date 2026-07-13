package edu.fdzc.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(title = "班课学生")
public class CourseStudent implements Serializable {

    @Schema(name = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(name = "班课ID")
    private Long courseId;

    @Schema(name = "学生ID")
    private Long studentId;

}
