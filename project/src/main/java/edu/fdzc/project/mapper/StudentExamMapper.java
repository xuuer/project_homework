package edu.fdzc.project.mapper;

import edu.fdzc.project.entity.StudentExam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.fdzc.project.entity.dto.StatisticsDto;
import edu.fdzc.project.entity.excel.ExamRankingExcel;
import edu.fdzc.project.entity.vo.StudentExamVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author zhangjiu
* @description 针对表【student_exam】的数据库操作Mapper
* @createDate 2025-06-21 17:10:18
* @Entity edu.fdzc.project.entity.StudentExam
*/
@Mapper
public interface StudentExamMapper extends BaseMapper<StudentExam> {

    @Select("select s.id, no, name, score, start_time, end_time from student_exam se left join student s on se.student_id=s.id where exam_id=#{id} order by score desc limit #{size} offset #{offset}")
    List<StudentExamVo> selectRanking(@Param("id") Long id, @Param("size") Long size, @Param("offset") Long offset);

    @Select("select no, name, score, start_time, end_time from student_exam se left join student s on se.student_id=s.id where exam_id=#{id} order by score desc")
    List<ExamRankingExcel> selectExcelRanking(@Param("id") Long id);

    @Select("select s.id, no, name, score, start_time, end_time from student_exam se left join student s on se.student_id=s.id where exam_id=#{examId} and s.id=#{studentId} order by start_time desc")
    StudentExamVo selectExam(@Param("studentId") Long studentId, @Param("examId") Long examId);

    @Select("select max(score) max_score, min(score) min_score, avg(score) avg_score from student_exam where exam_id=#{id}")
    StatisticsDto selectStatistics(@Param("id") Long id);

    @Select("select count(*) from exam e left join student_exam se on se.exam_id=e.id where course_id=#{courseId} and student_id=#{studentId}")
    Long selectExamCount(@Param("courseId") Long courseId, @Param("studentId") Long studentId);


}




