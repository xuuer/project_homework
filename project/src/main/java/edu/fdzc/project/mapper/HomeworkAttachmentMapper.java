package edu.fdzc.project.mapper;

import edu.fdzc.project.entity.HomeworkAttachment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author zhangjiu
* @description 针对表【homework_attachment(作业附件表)】的数据库操作Mapper
* @createDate 2025-07-13 15:34:25
* @Entity edu.fdzc.project.entity.HomeworkAttachment
*/
@Mapper
public interface HomeworkAttachmentMapper extends BaseMapper<HomeworkAttachment> {

    @Select("select * from homework_attachment where homework_id = #{id}")
    List<HomeworkAttachment> selectByHomeAssigId(Long id);

}




