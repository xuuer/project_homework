package edu.fdzc.project.entity.vo;

import edu.fdzc.project.entity.Student;
import edu.fdzc.project.entity.dto.QuestionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


import java.util.List;

@Data
@Schema(title = "开始测验VO")
public class StartExamVo {

    @Schema(name = "学生信息")
    private Student student;

    @Schema(name = "测验信息")
    private ExamVo exam;

    @Schema(name = "最晚交卷时间")
    private String endTime;

    @Schema(name = "试卷信息")
    private List<QuestionDto> paper;

}
