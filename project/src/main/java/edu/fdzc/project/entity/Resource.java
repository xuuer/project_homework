package edu.fdzc.project.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(title = "资源")
@TableName(autoResultMap = true)
public class Resource implements Serializable {

    @Schema(name = "资源ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(name = "资源名")
    private String name;

    @Schema(name = "资源链接")
    private String url;

    @Schema(name = "教师ID")
    private Long teacherId;

    @Schema(name = "分组ID")
    private Long categoryId;

    @Schema(name = "知识点")
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> tags;

    @Schema(name = "创建时间")
    private String createTime;

    @Schema(name = "修改时间")
    private String updateTime;

}
