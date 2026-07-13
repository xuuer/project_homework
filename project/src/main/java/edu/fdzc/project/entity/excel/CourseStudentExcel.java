package edu.fdzc.project.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "班课学生excel导出模板")
public class CourseStudentExcel {

    @ExcelProperty("课程名")
    @Schema(name = "课程名")
    private String courseName;

    @ExcelProperty("学期")
    @Schema(name = "学期")
    private String semester;

    @ExcelProperty("班级")
    @Schema(name = "班级")
    private String clazz;

    @ExcelProperty("学号")
    @Schema(name = "学号")
    private String no;

    @ExcelProperty("学生姓名")
    @Schema(name = "学生姓名")
    private String studentName;

    @ExcelProperty("参与测验数量")
    @Schema(name = "参与测验数量")
    private Long examNum;

    @ExcelProperty("AI对话次数")
    @Schema(name = "AI对话次数")
    private Long chatNum;

    @ExcelProperty("已学习知识点个数")
    @Schema(name = "已学习知识点个数")
    private Long tagNum;

}
