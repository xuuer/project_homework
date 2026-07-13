package edu.fdzc.project.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.fdzc.project.config.CozeConfig;
import edu.fdzc.project.entity.*;
import edu.fdzc.project.entity.dto.QuestionDto;
import edu.fdzc.project.entity.excel.CourseStudentExcel;
import edu.fdzc.project.entity.vo.*;
import edu.fdzc.project.mapper.*;
import edu.fdzc.project.service.CourseService;
import edu.fdzc.project.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangjiu
 * @description 针对表【course】的数据库操作Service实现
 * @createDate 2025-06-20 22:54:05
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService{

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private CourseStudentMapper courseStudentMapper;
    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryResourceMapper categoryResourceMapper;

    @Autowired
    private StudentExamMapper studentExamMapper;

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private TagNodeMapper tagNodeMapper;

    @Autowired
    private TagRelationshipMapper tagRelationshipMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private StudentTagMapper studentTagMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private CozeConfig cozeConfig;

    @Autowired
    private HomeworkMapper homeworkMapper;

    @Override
    public CourseVo selectById(Long courseId) {
        Course course = courseMapper.selectById(courseId);
        Teacher teacher = teacherMapper.selectById(course.getTeacherId());
        CourseVo vo = new CourseVo();
        vo.setId(course.getId());
        vo.setName(course.getName());
        vo.setSemester(course.getSemester());
        vo.setClazz(course.getClazz());
        vo.setCover(course.getCover());
        vo.setTeacherId(course.getTeacherId());
        vo.setTeacherName(teacher.getName());
        vo.setTagCategoryId(course.getTagCategoryId());
        vo.setQuestionCategoryId(course.getQuestionCategoryId());
        return vo;
    }

    @Override
    public PageVo<Course> selectPage(Long pageNum, Long pageSize, Course course) {
        Long userId = JwtUtil.getUserId();
        String role = JwtUtil.getUserRole();
        String name = course.getName();
        String semester = course.getSemester();

        PageVo<Course> vo = new PageVo<>();
        if(role.equals("teacher")) {

            LambdaQueryWrapper<Course> query = new LambdaQueryWrapper<>();
            query.eq(Course::getTeacherId, userId);
            if(name != null && !name.isEmpty()) {
                query.like(Course::getName, name);
            }
            if(semester != null && !semester.isEmpty()) {
                query.like(Course::getSemester, semester);
            }
            query.orderByDesc(Course::getCreateTime);
            Page<Course> page = courseMapper.selectPage(new Page<>(pageNum, pageSize), query);
            vo.setList(page.getRecords());
            vo.setTotal(page.getTotal());

        } else if(role.equals("student")) {

            List<Course> list = courseStudentMapper.selectCoursePage(userId, (pageNum - 1) * pageSize, pageSize);
            Long total = courseStudentMapper.selectCount(
                    new LambdaQueryWrapper<CourseStudent>()
                            .eq(CourseStudent::getStudentId, userId));
            vo.setList(list);
            vo.setTotal(total);

        }
        return vo;
    }

    @Override
    public Object insert(Course course) {
        Long teacherId = JwtUtil.getUserId();
        course.setTeacherId(teacherId);

        // 设置创建时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createTime = LocalDateTime.now().format(formatter);
        course.setCreateTime(createTime);

        // 添加班课
        return courseMapper.insert(course);
    }

    @Override
    public Object updateByCouId(Course course) {
        Long teacherId = JwtUtil.getUserId();
        course.setTeacherId(teacherId);

        // 设置修改时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String updateTime = LocalDateTime.now().format(formatter);
        course.setCreateTime(updateTime);

        // 修改班课
        return courseMapper.updateById(course);
    }

    @Override
    public Object deleteById(Long id) {
        return courseMapper.deleteById(id);
    }

    @Override
    public PageVo<Exam> selectExamPage(Long id, Long pageNum, Long pageSize, Exam exam) {
        String name = exam.getName();

        // 拼接查询条件
        LambdaQueryWrapper<Exam> query = new LambdaQueryWrapper<>();
        query.eq(Exam::getCourseId, id);
        if(name != null && !name.isEmpty()) {
            query.like(Exam::getName, name);
        }
        query.orderByDesc(Exam::getCloseTime);

        // 执行查询
        Page<Exam> res = examMapper.selectPage(new Page<>(pageNum, pageSize), query);
        PageVo<Exam> vo = new PageVo<>();
        vo.setList(res.getRecords());
        vo.setTotal(res.getTotal());
        return vo;
    }

    @Override
    public List<CategoryResourceVo> selectResource(Long id) {
        // 获取分组
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getCourseId, id));
        // 获取分组下的资源
        List<CategoryResourceVo> list = new ArrayList<>();
        for(Category category : categories) {
            List<Resource> resources = categoryResourceMapper.selectResource(category.getId());
            CategoryResourceVo vo = new CategoryResourceVo();
            vo.setId(category.getId());
            vo.setName(category.getName());
            vo.setChildren(resources);
            list.add(vo);
        }
        return list;
    }

    @Override
    public PageVo<Student> selectStudentPage(Long id, Long pageNum, Long pageSize, Student student) {
        String no = student.getNo();
        String name = student.getName();

        List<Student> list = courseStudentMapper.selectStudentPage(id, (pageNum-1)*pageSize, pageSize, no, name);
        Long total = courseStudentMapper.selectStudentCount(id, no, name);
        PageVo<Student> vo = new PageVo<>();
        vo.setList(list);
        vo.setTotal(total);
        return vo;
    }

    @Override
    public Object insertStudent(Long courseId, String[] studentNos) {
        // 检查学号是否都存在
        List<Long> studentIds = new ArrayList<>();
        for(String no : studentNos) {
            Student student = studentMapper.selectOne(
                    new LambdaQueryWrapper<Student>()
                            .eq(Student::getNo, no));
            if (student == null) return 0;
            studentIds.add(student.getId());
        }
        for(Long studentId : studentIds) {
            // 检查是否已经添加过了
            CourseStudent res = courseStudentMapper.selectOne(
                    new LambdaQueryWrapper<CourseStudent>()
                            .eq(CourseStudent::getCourseId, courseId)
                            .eq(CourseStudent::getStudentId, studentId));
            if(res == null) {
                CourseStudent cs = new CourseStudent();
                cs.setCourseId(courseId);
                cs.setStudentId(studentId);
                int cnt = courseStudentMapper.insert(cs);
                if(cnt == 0) return 0;
            }
        }
        return 1;
    }

    @Override
    public Object deleteStudent(Long courseId, Long studentId) {
        return courseStudentMapper.delete(
                new LambdaQueryWrapper<CourseStudent>()
                        .eq(CourseStudent::getCourseId, courseId)
                        .eq(CourseStudent::getStudentId, studentId));
    }

    @Override
    public void downloadStudent(HttpServletResponse response, Long courseId) {
        Course course = courseMapper.selectById(courseId);
        String fileName = course.getName() + course.getSemester() + course.getClazz() + "学生名单.xlsx";
        List<CourseStudentExcel> list = new ArrayList<>();
        List<CourseStudent> students = courseStudentMapper.selectList(
                new LambdaQueryWrapper<CourseStudent>()
                        .eq(CourseStudent::getCourseId, courseId));
        for(CourseStudent cs : students) {
            CourseStudentExcel cse = new CourseStudentExcel();
            Long studentId = cs.getStudentId();
            Student student = studentMapper.selectById(studentId);
            Long examNum = studentExamMapper.selectExamCount(courseId, studentId);
            Long chatNum = chatMapper.selectChatCount(courseId, studentId);
            List<StudentTagNodeVo> studentTags = tagNodeMapper.selectStudent(course.getTagCategoryId(), studentId);
            Long tagNum = (long) studentTags.size();

            cse.setCourseName(course.getName());
            cse.setSemester(course.getSemester());
            cse.setClazz(cse.getClazz());
            cse.setNo(student.getNo());
            cse.setStudentName(student.getName());
            cse.setExamNum(examNum);
            cse.setChatNum(chatNum);
            cse.setTagNum(tagNum);

            list.add(cse);
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition"); // 将Content-Disposition属性暴露给浏览器
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        try {
            EasyExcel.write(response.getOutputStream(), CourseStudentExcel.class).autoCloseStream(Boolean.FALSE).sheet("学生").doWrite(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public TagVo selectTag(Long id) {
        Course course = courseMapper.selectById(id);
        List<TagNode> nodes = tagNodeMapper.selectList(
                new LambdaQueryWrapper<TagNode>()
                        .eq(TagNode::getCategoryId, course.getTagCategoryId()));
        List<TagRelationship> links = tagRelationshipMapper.selectList(
                new LambdaQueryWrapper<TagRelationship>()
                        .eq(TagRelationship::getCategoryId, course.getTagCategoryId()));
        TagVo vo = new TagVo();
        vo.setNodes(nodes);
        vo.setLinks(links);
        return vo;
    }

    @Override
    public StudentTagVo selectStudentTag(Long id, Long studentId) {
        Course course = courseMapper.selectById(id);
        List<TagNode> allNodes = tagNodeMapper.selectList(
                new LambdaQueryWrapper<TagNode>()
                        .eq(TagNode::getCategoryId, course.getTagCategoryId()));
        List<StudentTagNodeVo> studentNodes = tagNodeMapper.selectStudent(course.getTagCategoryId(), studentId);
        List<TagRelationship> links = tagRelationshipMapper.selectList(
                new LambdaQueryWrapper<TagRelationship>()
                        .eq(TagRelationship::getCategoryId, course.getTagCategoryId()));

        Map<Long, TagNode> allNodesMap = new HashMap<>();
        for(TagNode node : allNodes) {
            allNodesMap.put(node.getId(), node);
        }
        Map<Long, StudentTagNodeVo> studentNodesMap = new HashMap<>();
        Map<Long, Boolean> vis = new HashMap<>();
        for(StudentTagNodeVo node : studentNodes) {
            studentNodesMap.put(node.getId(), node);
            vis.put(node.getId(), true);
        }
        for(TagRelationship link : links) {
            if(studentNodesMap.get(link.getSource()) != null
                    && studentNodesMap.get(link.getTarget()) == null
                    && vis.get(link.getTarget()) == null) {
                vis.put(link.getTarget(), true);
                TagNode targetNode = allNodesMap.get(link.getTarget());
                StudentTagNodeVo node = new StudentTagNodeVo();
                node.setId(targetNode.getId());
                node.setName(targetNode.getName());
                node.setCategory("未学");
                node.setDescription(targetNode.getDescription());
                node.setValue(0.0);
                studentNodes.add(node);
            }
        }

        StudentTagVo vo = new StudentTagVo();
        vo.setNodes(studentNodes);
        vo.setLinks(links);
        return vo;
    }

    @Override
    public List<Resource> selectTagResource(Long id, Long tagId) {
        TagNode tagNode = tagNodeMapper.selectById(tagId);
        List<CategoryResourceVo> list = selectResource(id);
        List<Resource> ans = new ArrayList<>();
        for(CategoryResourceVo vo : list) {
            List<Resource> children = vo.getChildren();
            for(Resource resource : children) {
                if(resource.getName().contains(tagNode.getName())) {
                    ans.add(resource);
                    continue;
                }
                Resource res = resourceMapper.selectById(resource.getId());
                List<String> tags = res.getTags();
                for(String tag : tags) {
                    if(tag.contains(tagNode.getName())) {
                        ans.add(resource);
                        break;
                    }
                }
            }
        }
        return ans;
    }

    @Override
    public Question selectPractice(Long courseId, Long tagId) {
        Long studentId = JwtUtil.getUserId();
        TagNode tagNode = tagNodeMapper.selectById(tagId);
        Course course = courseMapper.selectById(courseId);
        StudentTag studentTag = studentTagMapper.selectOne(
                new LambdaQueryWrapper<StudentTag>()
                        .eq(StudentTag::getStudentId, studentId)
                        .eq(StudentTag::getTag, tagNode.getName()));
        LambdaQueryWrapper<Question> query = new LambdaQueryWrapper<Question>()
                .eq(Question::getCategoryId, course.getQuestionCategoryId())
                .ne(Question::getType, "综合题")
                .like(Question::getTags, "\"" + tagNode.getName() + "\"");
        double mastery = 0.0;
        if(studentTag != null) {
            mastery = studentTag.getRightNum() * 1.0 / studentTag.getTotalNum();
        }
        if(mastery < 60) {
            query.eq(Question::getDifficulty, "简单");
        } else if(mastery < 85) {
            query.eq(Question::getDifficulty, "中等");
        } else {
            query.eq(Question::getDifficulty, "困难");
        }
        List<Question> questions = questionMapper.selectList(query);
        int randomNumber = (int) (Math.random() * questions.size());
        Question question = new Question();
        if(!questions.isEmpty()) {
            question = questions.get(randomNumber);
        } else {
            JSONObject c = cozeConfig.makePractice(tagNode.getName(), mastery);
            question.setContent(c.getStr("content"));
            question.setType(c.getStr("type"));
            question.setAnswer(c.getStr("answer"));
            JSONArray options = JSONUtil.parseArray(c.getStr("options"));
            question.setOptions(options.toList(String.class));
            JSONArray tags = JSONUtil.parseArray(c.getStr("tags"));
            question.setTags(tags.toList(String.class));
            System.out.println(question);
        }
        return question;
    }

    @Override
    public Object submitPractice(QuestionDto q) {
        Long studentId = JwtUtil.getUserId();

        // 计算本题得分
        Long rightNum = 0L;
        if(q.getAnswer().equals(q.getStudentAnswer())) {
            rightNum = 1L;
        }

        // 记录知识点正确率
        List<String> tags = q.getTags();
        for(String tag : tags) {
            StudentTag st = studentTagMapper.selectOne(
                    new LambdaQueryWrapper<StudentTag>()
                            .eq(StudentTag::getStudentId, studentId)
                            .eq(StudentTag::getTag, tag));
            if(st == null) {
                st = new StudentTag();
                st.setStudentId(studentId);
                st.setTag(tag);
                st.setRightNum(rightNum);
                st.setTotalNum(1L);
                studentTagMapper.insert(st);
            } else {
                st.setRightNum(st.getRightNum() + rightNum);
                st.setTotalNum(st.getTotalNum() + 1);
                studentTagMapper.updateById(st);
            }
        }

        return 1;
    }

    @Override
    public PageVo<Homework> selectHomeworkPage(Long id, Long pageNum, Long pageSize, Homework homework) {
        String title = homework.getTitle();

        // 拼接查询条件
        LambdaQueryWrapper<Homework> query = new LambdaQueryWrapper<>();
        query.eq(Homework::getCourseId, id);
        if(title != null && !title.isEmpty()) {
            query.like(Homework::getTitle, title);
        }
        query.orderByDesc(Homework::getDeadline);

        // 执行查询
        Page<Homework> res = homeworkMapper.selectPage(new Page<>(pageNum, pageSize), query);
        PageVo<Homework> vo = new PageVo<>();
        vo.setList(res.getRecords());
        vo.setTotal(res.getTotal());
        return vo;
    }

    @Override
    public Long getTeacherHomeworkCount() {
        Long teacherId = JwtUtil.getUserId();
        return homeworkMapper.selectCount(
                new LambdaQueryWrapper<Homework>()
                        .eq(Homework::getTeacherId, teacherId));
    }

    @Override
    public Long getCourseHomeworkCount(Long courseId) {
        return homeworkMapper.selectCount(
                new LambdaQueryWrapper<Homework>()
                        .eq(Homework::getCourseId, courseId));
    }
}




