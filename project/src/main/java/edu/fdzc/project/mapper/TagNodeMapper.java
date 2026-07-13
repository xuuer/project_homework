package edu.fdzc.project.mapper;

import edu.fdzc.project.entity.TagNode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.fdzc.project.entity.vo.StudentTagNodeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author zhangjiu
* @description 针对表【tag_node】的数据库操作Mapper
* @createDate 2025-07-07 21:21:03
* @Entity edu.fdzc.project.entity.TagNode
*/
@Mapper
public interface TagNodeMapper extends BaseMapper<TagNode> {


    @Select("select t.id, t.name, t.description, round(s.right_num*100/s.total_num, 2) value, case when s.right_num*100/s.total_num < 60 then '薄弱' when s.right_num*100/s.total_num between 60 and 85 then '良好' when s.right_num*100/s.total_num > 85 then '优秀' end as category from student_tag s left join tag_node t on s.tag=t.name where category_id=#{id} and student_id=#{studentId}")
    List<StudentTagNodeVo> selectStudent(@Param("id") Long id, @Param("studentId") Long studentId);
}




