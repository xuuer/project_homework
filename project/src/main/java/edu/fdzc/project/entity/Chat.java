package edu.fdzc.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(title = "对话")
public class Chat implements Serializable {

    @Schema(name = "对话ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(name = "用户ID")
    private Long userId;

    @Schema(name = "班课ID")
    private Long courseId;

    @Schema(name = "角色", description = "AI/用户")
    private String type;

    @Schema(name = "对话内容")
    private String content;

    @Schema(name = "创建时间")
    private String createTime;

}