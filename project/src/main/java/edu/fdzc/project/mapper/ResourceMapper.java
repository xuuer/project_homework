package edu.fdzc.project.mapper;

import edu.fdzc.project.entity.Resource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author zhangjiu
* @description 针对表【resource】的数据库操作Mapper
* @createDate 2025-06-21 17:10:34
* @Entity edu.fdzc.project.entity.Resource
*/
@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {

}




