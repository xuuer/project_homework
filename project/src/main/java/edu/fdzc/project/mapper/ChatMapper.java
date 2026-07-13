package edu.fdzc.project.mapper;

import edu.fdzc.project.entity.Chat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
* @author zhangjiu
* @description 针对表【chat】的数据库操作Mapper
* @createDate 2025-06-21 17:10:34
* @Entity edu.fdzc.project.entity.Chat
*/
@Mapper
public interface ChatMapper extends BaseMapper<Chat> {

    @Select("select count(*) from chat where course_id=#{courseId} and user_id=#{studentId} and type='user'")
    Long selectChatCount(Long courseId, Long studentId);
}




