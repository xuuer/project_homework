package edu.fdzc.project.entity.vo;

import edu.fdzc.project.entity.HomeworkSubmissionFile;
import lombok.Data;

import java.util.List;

@Data
public class StudentHomeworkResultVo {

    private StudentHomeworkVo studentHomeworkVo; //学生的基本信息

    private String textContent; // 学生提交的内容

    private List<HomeworkSubmissionFile> files; //学生上传的文件



}
