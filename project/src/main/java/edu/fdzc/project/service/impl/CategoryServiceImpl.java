package edu.fdzc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.fdzc.project.entity.Category;
import edu.fdzc.project.entity.CategoryResource;
import edu.fdzc.project.entity.vo.CategoryVo;
import edu.fdzc.project.mapper.CategoryResourceMapper;
import edu.fdzc.project.service.CategoryService;
import edu.fdzc.project.mapper.CategoryMapper;
import edu.fdzc.project.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author zhangjiu
* @description 针对表【category】的数据库操作Service实现
* @createDate 2025-06-21 17:10:34
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryResourceMapper categoryResourceMapper;

    @Override
    public Category selectById(int id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public List<CategoryVo> selectTree(String type) {
        Long teacherId = JwtUtil.getUserId();

        // 查询分组
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getTeacherId, teacherId)
                        .eq(Category::getType, type));

        // 数据库中的结构是子节点指向父节点，需要转换成父节点包含子节点的结构
        List<CategoryVo> ans = new ArrayList<>(); // 第一层的节点集合
        List<CategoryVo> que = new ArrayList<>(); // 用于层序遍历的队列
        for(Category category : categories) { // 先把第一层的节点加入集合
            if(category.getParentId() == 0) {
                CategoryVo vo = new CategoryVo();
                vo.setId(category.getId());
                vo.setValue(category.getId());
                vo.setLabel(category.getName());
                vo.setChildren(new ArrayList<>());
                ans.add(vo);
                que.add(vo);
            }
        }
        for(int i = 0; i < que.size(); i ++) { // 再层序遍历，处理每一个节点包含有哪些子节点
            for(Category category : categories) {
                if(category.getParentId().equals(que.get(i).getId())) {
                    CategoryVo vo = new CategoryVo();
                    vo.setId(category.getId());
                    vo.setValue(category.getId());
                    vo.setLabel(category.getName());
                    vo.setChildren(new ArrayList<>());
                    que.get(i).getChildren().add(vo);
                    que.add(vo);
                }
            }
        }
        return ans;
    }

    @Override
    public Object insert(Category category) {
        Long teacherId = JwtUtil.getUserId();
        category.setTeacherId(teacherId);
        //添加分组
        return categoryMapper.insert(category);
    }

    @Override
    public Object updateByCateId(Category category) {
        Long teacherId = JwtUtil.getUserId();
        category.setTeacherId(teacherId);
        System.out.println(category);
        return categoryMapper.updateById(category);
    }

    @Override
    public Object deleteByCateId(Long id) {
//        return null;
        return categoryMapper.deleteById(id);
    }


    @Override
    public Object insertResource(Long categoryId, Long[] resourceIds) {
        for(Long resourceId : resourceIds) {
            LambdaQueryWrapper<CategoryResource> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CategoryResource::getCategoryId, categoryId);
            wrapper.eq(CategoryResource::getResourceId, resourceId);
            CategoryResource res = categoryResourceMapper.selectOne(wrapper);
            if(res == null) {
                CategoryResource newResource = new CategoryResource();
                newResource.setCategoryId(categoryId);
                newResource.setResourceId(resourceId);
                int cnt = categoryResourceMapper.insert(newResource);
                if(cnt == 0) {
                    return 0;
                }
            }
        }
        return 1;
    }

    @Override
    public Object deleteResource(Long id, Long resourceId) {
        LambdaQueryWrapper<CategoryResource> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CategoryResource::getCategoryId,id);
        queryWrapper.eq(CategoryResource::getResourceId,resourceId);
        return categoryResourceMapper.delete(queryWrapper);
    }
}




