package edu.fdzc.project.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "测验成绩排名excel导出模板")
@TableName(autoResultMap = true)
public class ExamRankingExcel {

    @ExcelProperty("排名")
    @Schema(name = "排名")
    private Long ranking;

    @ExcelProperty("学号")
    @Schema(name = "学号")
    private String no;

    @ExcelProperty("姓名")
    @Schema(name = "姓名")
    private String name;

    @ExcelProperty("成绩")
    @Schema(name = "成绩")
    private Double score;

    @ExcelProperty("开始时间")
    @Schema(name = "开始时间")
    private String startTime;

    @ExcelProperty("结束时间")
    @Schema(name = "结束时间")
    private String endTime;

}
