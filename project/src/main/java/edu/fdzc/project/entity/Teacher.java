package edu.fdzc.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(title = "教师")
public class Teacher implements Serializable {

    @Schema(name = "教师ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(name = "编号")
    private String no;

    @Schema(name = "姓名")
    private String name;

    @Schema(name = "密码")
    @JsonIgnore
    private String password;

    @Schema(name = "头像")
    private String avatar;

    @Schema(name = "创建时间")
    private String createTime;

    @Schema(name = "修改时间")
    private String updateTime;

}
