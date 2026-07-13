package edu.fdzc.project.controller;

import edu.fdzc.project.common.Result;
import edu.fdzc.project.entity.Question;
import edu.fdzc.project.entity.vo.PageVo;
import edu.fdzc.project.service.QuestionService;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;


    //获取指定题目信息<>
    @GetMapping("/{id}")
    public Result<Question> selectById(@PathVariable("id") Long id) {
        return Result.success(questionService.selectByQuId(id));
    }

    //查询分页列表<>
    @GetMapping
    public Result<PageVo<Question>> selectPage(@RequestParam(name = "pageNum", defaultValue = "1") Long pageNum,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Long pageSize,
                                               Question question){
        return Result.success(questionService.selectPage(pageNum, pageSize, question));
    }

    //添加题目<>
    @PostMapping
    public Result<Integer> insert(@RequestBody Question question)
    {
        return Result.success("添加成功",questionService.insert(question));
    }

    //修改题目<>
    @PutMapping
    public Result<Integer> updateByQuId(@RequestBody Question question){
        return Result.success("修改成功",questionService.updateByQuId(question));
    }

//    @DeleteMapping{"/{ids}"}<>
    @DeleteMapping("/{ids}")
    public Result<Integer> deleteById(@PathVariable("ids") List<Long> ids){
        return Result.success("删除成功",questionService.deleteById(ids));
    }


}
