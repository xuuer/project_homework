package edu.fdzc.project.controller;

import edu.fdzc.project.common.Result;
import edu.fdzc.project.entity.Category;
import edu.fdzc.project.entity.vo.CategoryVo;
import edu.fdzc.project.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    //获取该分组的信息<>
    @GetMapping("/{id}")
    public Result<Category> selectCategoryById(@PathVariable("id") int id) {
        return Result.success(categoryService.selectById(id));
    }
    // 获取分组树<>
    @GetMapping
    public Result<List<CategoryVo>> selectTree(@RequestParam("type") String type) {
        return Result.success(categoryService.selectTree(type));
    }

    //添加扽组<>
    @PostMapping
    public Result<Object> insert(@RequestBody Category category) {
        return Result.success("添加成功", categoryService.insert(category));
    }

    //修改分组<>
    @PutMapping
    public Result<Object> updateByCategoryId(@RequestBody Category category) {
        System.out.println(category);
        return Result.success("修改成功",categoryService.updateByCateId(category));
    }

//    删除分组<>
    @DeleteMapping("/{id}")
    public Result<Object> deleteById(@PathVariable("id") Long id) {
        return Result.success("删除成功",categoryService.deleteByCateId(id));
    }
//分组下添加资源**
    @PostMapping("/{id}/resource")
    public Result<Object> insertResource(@PathVariable("id") Long id,
                                         @RequestBody Long[] resourceIds) {
        return Result.success("添加成功", categoryService.insertResource(id, resourceIds));
    }
//删除该分组id 下的resourceID**
    @DeleteMapping("/{id}/resource/{resourceId}")
    public Result<Object> deleteResourceById(@PathVariable("id") Long id,
                                             @PathVariable("resourceId") Long resourceId) {
        return Result.success("删除成功", categoryService.deleteResource(id, resourceId));
    }

}
