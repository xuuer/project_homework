package edu.fdzc.project.entity.vo;

import edu.fdzc.project.entity.HomeworkAttachment;
import lombok.Data;

import java.util.List;

@Data
public class HomeAssignmentVo {

    private Long id;

    /**
     * 所属课程ID
     */
    private Long courseId;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 作业标题
     */
    private String title;

    /**
     * 作业说明
     */
    private String description;

    /**
     * 截止时间
     */
    private String deadline;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 参考答案
     */
    private String answer;

    List<HomeworkAttachment> attachments;

}