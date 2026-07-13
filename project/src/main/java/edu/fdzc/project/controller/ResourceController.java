package edu.fdzc.project.controller;

import edu.fdzc.project.common.Result;
import edu.fdzc.project.entity.Resource;
import edu.fdzc.project.entity.vo.PageVo;
import edu.fdzc.project.service.ResourceService;
//import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    //通过id 找文件
    @GetMapping("/{id}")
    public Result<Resource> getResourceById(@PathVariable("id") Long id) {
        return Result.success(resourceService.selectById(id));
    }

//    分页显示文件
    @GetMapping
    public Result<PageVo<Resource>> selectPage(@RequestParam(name = "pageNum", defaultValue = "1") Long pageNum,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Long pageSize,
                                               Resource resource){
        return Result.success(resourceService.selectPage(pageNum, pageSize, resource));
    }
    //上传文件
    @PostMapping("/upload")
    public Result<Resource> upload(@RequestParam(name="file",required=true ) MultipartFile file ){
        System.out.println(file.getOriginalFilename());
        return Result.success(resourceService.upload(file));
    }

    //添加文件
    @PostMapping
    public Result<Integer> insertResource(@RequestBody Resource resource){
        return Result.success("添加成功",resourceService.insert(resource));
    }

    //修改文件
    @PutMapping
    public Result<Integer> updateResource(@RequestBody Resource resource){
        return Result.success("修改成功",resourceService.updateByResId(resource));
    }

    //下载文件资源
    @GetMapping("/{id}/download")
    public void download(HttpServletResponse response, @PathVariable("id") Long id){
        resourceService.download(response,id);
    }

    //文件的预览
    @GetMapping("/{id}/preview")
    public Result<String> getPreviewUrl(@PathVariable("id") Long id){
        return Result.success(resourceService.getPreviewUrl(id));
    }


    //文件的删除
    @DeleteMapping("/{id}")
    public Result<Integer> deleteResourceById(@PathVariable("id") Long id){
        return Result.success("删除成功",resourceService.deleteById(id));
    }




}
