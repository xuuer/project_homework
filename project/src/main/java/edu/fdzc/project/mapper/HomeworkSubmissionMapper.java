package edu.fdzc.project.mapper;

import edu.fdzc.project.entity.HomeworkSubmission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
* @author zhangjiu
* @description 针对表【homework_submission(学生作业提交表)】的数据库操作Mapper
* @createDate 2025-07-13 15:34:25
* @Entity edu.fdzc.project.entity.HomeworkSubmission
*/
@Mapper
public interface HomeworkSubmissionMapper extends BaseMapper<HomeworkSubmission> {

    @Select("select * from homework_submission where homework_id = #{id} and student_id = #{studentId}")
    HomeworkSubmission selectByStuHomId(Long id, Long studentId);
}




