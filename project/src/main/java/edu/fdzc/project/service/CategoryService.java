package edu.fdzc.project.service;

import edu.fdzc.project.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.fdzc.project.entity.vo.CategoryVo;

import java.util.List;

/**
* @author zhangjiu
* @description 针对表【category】的数据库操作Service
* @createDate 2025-06-21 17:10:34
*/
public interface CategoryService extends IService<Category> {

    Category selectById(int id);

    List<CategoryVo> selectTree(String type);

    Object insert(Category category);

    Object updateByCateId(Category category);

    Object deleteByCateId(Long id);

    Object insertResource(Long id, Long[] resourceIds);

    Object deleteResource(Long id, Long resourceId);
}
