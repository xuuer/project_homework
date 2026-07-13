package edu.fdzc.project.mapper;

import edu.fdzc.project.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author zhangjiu
* @description 针对表【question】的数据库操作Mapper
* @createDate 2025-06-21 17:10:34
* @Entity edu.fdzc.project.entity.Question
*/
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

}




