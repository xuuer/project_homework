package edu.fdzc.project.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.fdzc.project.entity.Question;
import edu.fdzc.project.entity.vo.PageVo;
import edu.fdzc.project.service.QuestionService;
import edu.fdzc.project.mapper.QuestionMapper;
import edu.fdzc.project.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
* @author zhangjiu
* @description 针对表【question】的数据库操作Service实现
* @createDate 2025-06-21 17:10:34
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService{

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Question selectByQuId(Long id) {
        return questionMapper.selectById(id);
    }

    @Override
    public PageVo<Question> selectPage(Long pageNum, Long pageSize, Question question) {
        Long teacherId = JwtUtil.getUserId();
        String content = question.getContent();
        Long categoryId = question.getCategoryId();
        String difficult = question.getDifficulty();
        String type = question.getType();

        //拼接查询
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getTeacherId, teacherId);
        if(content != null && !content.isEmpty()){
            wrapper.like(Question::getContent, content);
        }
        if(categoryId != null){
            wrapper.eq(Question::getCategoryId, categoryId);
        }
        if(difficult != null && !difficult.isEmpty()){
            wrapper.like(Question::getDifficulty, difficult);
        }
        if(type != null && !type.isEmpty()){
            wrapper.eq(Question::getType, type);
        }

        wrapper.orderByDesc(Question::getCreateTime);

        //执行查询
        Page<Question> page = questionMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        //查询结果
        PageVo<Question> pageVo = new PageVo<>();
        pageVo.setTotal(page.getTotal());
        pageVo.setList(page.getRecords());
        return pageVo;
    }

    @Override
    public Integer insert(Question question) {
        Long teacherId = JwtUtil.getUserId();
        question.setTeacherId(teacherId);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createTime = LocalDateTime.now().format(formatter);
        question.setCreateTime(createTime);

        return questionMapper.insert(question);
    }

    @Override
    public Integer updateByQuId(Question question) {
        Long teacherId = JwtUtil.getUserId();
        question.setTeacherId(teacherId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createTime = LocalDateTime.now().format(formatter);
        question.setUpdateTime(createTime);

        return questionMapper.updateById(question);
    }

    @Override
    public Integer deleteById(List<Long> ids) {
        return questionMapper.deleteBatchIds(ids);
    }
}




