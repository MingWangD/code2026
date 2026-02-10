package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.entity.Course;
import com.example.exception.CustomException;
import com.example.mapper.CourseMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseService {

    @Resource
    private CourseMapper courseMapper;

    /**
     * 新增课程
     */
    public void add(Course course) {
        // 检查课程编号是否重复
        if (ObjectUtil.isNotEmpty(course.getCourseNo())) {
            Course existCourse = courseMapper.selectByCourseNo(course.getCourseNo());
            if (ObjectUtil.isNotNull(existCourse)) {
                throw new CustomException("课程编号已存在");
            }
        }

        // 设置默认值
        if (ObjectUtil.isEmpty(course.getCourseNo())) {
            course.setCourseNo("COURSE" + System.currentTimeMillis());
        }
        if (ObjectUtil.isEmpty(course.getCredit())) {
            course.setCredit(2.0);
        }
        if (ObjectUtil.isEmpty(course.getTotalHours())) {
            course.setTotalHours(32);
        }
        if (ObjectUtil.isEmpty(course.getStatus())) {
            course.setStatus("未开始");
        }

        course.setStudentCount(0);
        course.setHomeworkCount(0);
        course.setCreateTime(LocalDateTime.now().toString());
        course.setUpdateTime(LocalDateTime.now().toString());

        courseMapper.insert(course);
    }

    /**
     * 删除课程
     */
    public void deleteById(Integer id) {
        Course course = courseMapper.selectById(id);
        if (ObjectUtil.isNull(course)) {
            throw new CustomException("课程不存在");
        }
        courseMapper.deleteById(id);
    }

    /**
     * 修改课程
     */
    public void updateById(Course course) {
        Course dbCourse = courseMapper.selectById(course.getId());
        if (ObjectUtil.isNull(dbCourse)) {
            throw new CustomException("课程不存在");
        }

        // 检查课程编号是否重复
        if (ObjectUtil.isNotEmpty(course.getCourseNo()) &&
                !dbCourse.getCourseNo().equals(course.getCourseNo())) {
            Course existCourse = courseMapper.selectByCourseNo(course.getCourseNo());
            if (ObjectUtil.isNotNull(existCourse)) {
                throw new CustomException("课程编号已存在");
            }
        }

        course.setUpdateTime(LocalDateTime.now().toString());
        courseMapper.updateById(course);
    }

    /**
     * 根据ID查询课程
     */
    public Course selectById(Integer id) {
        Course course = courseMapper.selectById(id);
        if (ObjectUtil.isNull(course)) {
            throw new CustomException("课程不存在");
        }
        return course;
    }

    /**
     * 根据课程编号查询课程
     */
    public Course selectByCourseNo(String courseNo) {
        Course course = courseMapper.selectByCourseNo(courseNo);
        if (ObjectUtil.isNull(course)) {
            throw new CustomException("课程不存在");
        }
        return course;
    }

    /**
     * 查询所有课程
     */
    public List<Course> selectAll(Course course) {
        return courseMapper.selectAll(course);
    }

    /**
     * 分页查询课程
     */
    public PageInfo<Course> selectPage(Course course, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Course> list = courseMapper.selectAll(course);
        return PageInfo.of(list);
    }

    /**
     * 根据教师ID查询课程
     */
    public List<Course> selectByTeacherId(Integer teacherId) {
        return courseMapper.selectByTeacherId(teacherId);
    }

    /**
     * 根据学期查询课程
     */
    public List<Course> selectBySemester(String semester, Integer year) {
        return courseMapper.selectBySemester(semester, year);
    }

    /**
     * 根据状态查询课程
     */
    public List<Course> selectByStatus(String status) {
        return courseMapper.selectByStatus(status);
    }

    /**
     * 更新课程学生人数
     */
    public void updateStudentCount(Integer id, Integer studentCount) {
        Course course = courseMapper.selectById(id);
        if (ObjectUtil.isNull(course)) {
            throw new CustomException("课程不存在");
        }
        courseMapper.updateStudentCount(id, studentCount);
    }

    /**
     * 更新课程作业数量
     */
    public void updateHomeworkCount(Integer id, Integer homeworkCount) {
        Course course = courseMapper.selectById(id);
        if (ObjectUtil.isNull(course)) {
            throw new CustomException("课程不存在");
        }
        courseMapper.updateHomeworkCount(id, homeworkCount);
    }

    /**
     * 增加课程学生人数
     */
    public void incrementStudentCount(Integer id) {
        Course course = courseMapper.selectById(id);
        if (ObjectUtil.isNull(course)) {
            throw new CustomException("课程不存在");
        }
        int newCount = course.getStudentCount() + 1;
        courseMapper.updateStudentCount(id, newCount);
    }

    /**
     * 增加课程作业数量
     */
    public void incrementHomeworkCount(Integer id) {
        Course course = courseMapper.selectById(id);
        if (ObjectUtil.isNull(course)) {
            throw new CustomException("课程不存在");
        }
        int newCount = course.getHomeworkCount() + 1;
        courseMapper.updateHomeworkCount(id, newCount);
    }

    /**
     * 统计课程数量
     */
    public Integer count(Integer teacherId, String status, String semester) {
        return courseMapper.count(teacherId, status, semester);
    }

    /**
     * 搜索课程
     */
    public List<Course> search(String keyword) {
        Course query = new Course();
        query.setCourseName(keyword);
        query.setCourseNo(keyword);
        query.setTeacherName(keyword);
        return courseMapper.selectAll(query);
    }

    /**
     * 更新课程状态
     */
    public void updateStatus(Integer id, String status) {
        Course course = courseMapper.selectById(id);
        if (ObjectUtil.isNull(course)) {
            throw new CustomException("课程不存在");
        }
        course.setStatus(status);
        course.setUpdateTime(LocalDateTime.now().toString());
        courseMapper.updateById(course);
    }

    /**
     * 批量更新课程状态
     */
    public void batchUpdateStatus(List<Integer> ids, String status) {
        for (Integer id : ids) {
            updateStatus(id, status);
        }
    }
}