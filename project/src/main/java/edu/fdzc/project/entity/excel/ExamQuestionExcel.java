package edu.fdzc.project.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "试卷excel导出模板")
@TableName(autoResultMap = true)
public class ExamQuestionExcel {

    @ExcelProperty("题号")
    @Schema(name = "题号")
    private Long sort;

    @ExcelProperty("题干")
    @Schema(name = "题干")
    private String content;

    @ExcelProperty("题型")
    @Schema(name = "题型")
    private String type;

    @ExcelProperty("分值")
    @Schema(name = "分值")
    private Double score;

    @ExcelProperty("答案")
    @Schema(name = "答案")
    private String answer;

    @ExcelProperty("选项")
    @Schema(name = "选项")
    private String options;

}