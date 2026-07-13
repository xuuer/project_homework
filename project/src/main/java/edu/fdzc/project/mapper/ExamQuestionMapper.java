package edu.fdzc.project.mapper;

import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import edu.fdzc.project.entity.ExamQuestion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.fdzc.project.entity.dto.QuestionDto;
import edu.fdzc.project.entity.excel.ExamQuestionExcel;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
* @author zhangjiu
* @description 针对表【exam_question】的数据库操作Mapper
* @createDate 2025-06-21 17:07:33
* @Entity edu.fdzc.project.entity.ExamQuestion
*/
@Mapper
public interface ExamQuestionMapper extends BaseMapper<ExamQuestion> {

    @Select("select q.id, type, content, options, answer, analysis, difficulty, tags, teacher_id, category_id, score, sort, mark_error from exam_question eq left join question q on q.id=eq.question_id where eq.exam_id=#{id} order by sort")
    @Results(id = "questionMap", value = {
            @Result(property = "options", column = "options", typeHandler = Fastjson2TypeHandler.class),
            @Result(property = "tags", column = "tags", typeHandler = Fastjson2TypeHandler.class)
    })
    List<QuestionDto> selectQuestion(@Param("id") Long id);

    @Select("select sort, content, type, score, answer, options from exam_question eq left join question q on q.id=eq.question_id where eq.exam_id=#{id} order by sort")
    @Results(id = "excelMap", value = {
            @Result(property = "options", column = "options", typeHandler = Fastjson2TypeHandler.class)
    })
    List<ExamQuestionExcel> selectExcelQuestion(@Param("id") Long id);

}




