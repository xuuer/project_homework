package edu.fdzc.project.mapper;

import edu.fdzc.project.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author zhangjiu
* @description 针对表【category】的数据库操作Mapper
* @createDate 2025-06-21 17:10:34
* @Entity edu.fdzc.project.entity.Category
*/
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}




