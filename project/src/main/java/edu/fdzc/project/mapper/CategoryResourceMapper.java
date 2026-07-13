package edu.fdzc.project.mapper;

import edu.fdzc.project.entity.CategoryResource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.fdzc.project.entity.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author zhangjiu
* @description 针对表【category_resource】的数据库操作Mapper
* @createDate 2025-06-21 17:10:34
* @Entity edu.fdzc.project.entity.CategoryResource
*/
@Mapper
public interface CategoryResourceMapper extends BaseMapper<CategoryResource> {

    @Select("select r.* from category_resource cr left join resource r on cr.resource_id=r.id where cr.category_id=#{id}")
    List<Resource> selectResource(Long id);
}




