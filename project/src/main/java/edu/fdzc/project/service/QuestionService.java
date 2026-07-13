package edu.fdzc.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.fdzc.project.entity.Question;
import edu.fdzc.project.entity.vo.PageVo;

import java.util.List;

public interface QuestionService extends IService<Question> {
    Question selectByQuId(Long id);

    PageVo<Question> selectPage(Long pageNum, Long pageSize, Question question);

    Integer insert(Question question);

    Integer updateByQuId(Question question);

    Integer deleteById(List<Long> ids);
}
