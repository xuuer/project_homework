package edu.fdzc.project.entity.vo;


import edu.fdzc.project.entity.dto.QuestionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
//import org.zc.net.entity.dto.QuestionDto;

import java.util.List;

@Data
@Schema(title = "学生测验结果VO")
public class StudentExamResultVo {

    @Schema(name = "学生测验概况")
    private StudentExamVo student;

    @Schema(name = "测验概况")
    private ExamVo exam;

    @Schema(name = "学生测验试卷")
    private List<QuestionDto> paper;

}
