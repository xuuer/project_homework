package edu.fdzc.project.mapper;

import edu.fdzc.project.entity.HomeworkSubmissionFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author zhangjiu
* @description 针对表【homework_submission_file(学生作业提交附件表)】的数据库操作Mapper
* @createDate 2025-07-13 15:34:25
* @Entity edu.fdzc.project.entity.HomeworkSubmissionFile
*/
@Mapper
public interface HomeworkSubmissionFileMapper extends BaseMapper<HomeworkSubmissionFile> {

    @Select("select * from homework_submission_file where student_id = #{studentId} and homework_id = #{homeworkId}")
    List<HomeworkSubmissionFile> selectByStuHomeId(Long homeworkId, Long studentId);
}




