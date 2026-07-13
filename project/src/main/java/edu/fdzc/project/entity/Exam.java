package edu.fdzc.project.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(title = "测验")
public class Exam implements Serializable {

    @Schema(name = "测验ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(name = "测验名")
    private String name;

    @Schema(name = "测验开放时间")
    private String openTime;

    @Schema(name = "测验关闭时间")
    private String closeTime;

    @Schema(name = "班课ID")
    private Long courseId;

    @Schema(name = "教师ID")
    private Long teacherId;

    @Schema(name = "测验时长", description = "单位：分钟")
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long duration;

    @Schema(name = "测验描述")
    private String description;

    @Schema(name = "切屏时长", description = "单位：秒")
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long screenCutTime;

    @Schema(name = "切屏次数")
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long screenCutNum;

    @Schema(name = "创建时间")
    private String createTime;

    @Schema(name = "修改时间")
    private String updateTime;

}
