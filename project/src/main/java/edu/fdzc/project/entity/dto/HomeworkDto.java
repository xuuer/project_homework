package edu.fdzc.project.entity.dto;

import edu.fdzc.project.entity.HomeworkAttachment;
import lombok.Data;

import java.util.List;

@Data
public class HomeworkDto {

    // 作业描述
    private String description;

    private String answer;

    //存放作业文件
    List<HomeworkAttachment> attachments;

}
