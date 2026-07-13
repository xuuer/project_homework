package edu.fdzc.project.mapper;

import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import edu.fdzc.project.entity.StudentExamQuestion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.fdzc.project.entity.dto.QuestionDto;
import edu.fdzc.project.entity.excel.StudentPaperExcel;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
* @author zhangjiu
* @description 针对表【student_exam_question】的数据库操作Mapper
* @createDate 2025-06-21 17:10:18
* @Entity edu.fdzc.project.entity.StudentExamQuestion
*/
@Mapper
public interface StudentExamQuestionMapper extends BaseMapper<StudentExamQuestion> {

    @Select("select q.id, type, content, options, q.answer, analysis, difficulty, tags, teacher_id, category_id, e.score, sort, s.answer student_answer, s.score student_score, remark from question q left join exam_question e on q.id=e.question_id left join student_exam_question s on q.id=s.question_id and e.exam_id=s.exam_id where e.exam_id=#{examId} and s.student_id=#{studentId} order by sort")
    @Results(id = "questionMap", value = {
            @Result(property = "options", column = "options", typeHandler = Fastjson2TypeHandler.class),
            @Result(property = "tags", column = "tags", typeHandler = Fastjson2TypeHandler.class)
    })
    List<QuestionDto> selectQuestion(@Param("studentId") Long studentId, @Param("examId") Long examId);

    @Select("select sort, content, type, e.score, q.answer, options, s.answer student_answer, s.score student_score, remark from question q left join exam_question e on q.id=e.question_id left join student_exam_question s on q.id=s.question_id and e.exam_id=s.exam_id where e.exam_id=#{examId} and s.student_id=#{studentId} order by sort")
    @Results(id = "excelMap", value = {
            @Result(property = "options", column = "options", typeHandler = Fastjson2TypeHandler.class)
    })
    List<StudentPaperExcel> selectExcelQuestion(@Param("examId") Long examId, @Param("studentId") Long studentId);

}




