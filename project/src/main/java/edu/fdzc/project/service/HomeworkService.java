package edu.fdzc.project.service;

import edu.fdzc.project.entity.Homework;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.fdzc.project.entity.dto.HomeworkDto;
import edu.fdzc.project.entity.dto.StudentSubmissionHomeDto;
import edu.fdzc.project.entity.vo.HomeAssignmentVo;
import edu.fdzc.project.entity.vo.PageVo;
import edu.fdzc.project.entity.vo.StudentHomeworkResultVo;
import edu.fdzc.project.entity.vo.StudentHomeworkVo;

/**
 * @author zhangjiu
 * @description 针对表【homework(作业表)】的数据库操作Service
 * @createDate 2025-07-13 15:34:25
 */
public interface HomeworkService extends IService<Homework> {

    Homework selectByHomeId(Long id);

    PageVo<Homework> selectPage(Long pageNum, Long pageSize, Homework homework);

    Object insert(Homework homework);

    Object updateByHomeId(Homework homework);

    Object deleteByid(Long id);

    Object insertFile(Long id, HomeworkDto homeworkDto);

    Object deleteByAId(Long id);

    Object updateByHomAssigId(Long id, HomeworkDto homeworkDto);

    HomeAssignmentVo selectByHomeAssigId(Long id);

    Object insertStuHome(Long id, StudentSubmissionHomeDto studentSubmissionHomeDto);

    Object deleteByStuSubFile(Long id);

    Object updateStuHome(Long id, StudentSubmissionHomeDto studentSubmissionHomeDto);

    StudentHomeworkResultVo selectStuHomeResult(Long id, Long stuId);


    PageVo<StudentHomeworkVo> selectStuHomeList(Long id, Long pageSize, Long pageNum, String status);

    Object updateStudentHomework(Long id, Long stuId ,StudentSubmissionHomeDto studentSubmissionHomeDto);

    PageVo<Homework> selectPageByCourse(Long pageNum, Long pageSize, Homework homework);
}
