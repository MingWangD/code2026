package com.example.mapper;

import com.example.entity.Course;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程数据访问接口
 */
public interface CourseMapper {

    /**
     * 新增课程
     */
    int insert(Course course);

    /**
     * 删除课程
     */
    int deleteById(Integer id);

    /**
     * 修改课程
     */
    int updateById(Course course);

    /**
     * 根据ID查询课程
     */
    Course selectById(Integer id);

    /**
     * 查询所有课程
     */
    List<Course> selectAll(Course course);

    /**
     * 根据教师ID查询课程
     */
    List<Course> selectByTeacherId(Integer teacherId);

    /**
     * 根据课程编号查询课程
     */
    Course selectByCourseNo(String courseNo);

    /**
     * 根据学期查询课程
     */
    List<Course> selectBySemester(@Param("semester") String semester, @Param("year") Integer year);

    /**
     * 统计课程数量
     */
    int count(@Param("teacherId") Integer teacherId,
              @Param("status") String status,
              @Param("semester") String semester);

    /**
     * 更新课程学生人数
     */
    int updateStudentCount(@Param("id") Integer id,
                           @Param("studentCount") Integer studentCount);

    /**
     * 更新课程作业数量
     */
    int updateHomeworkCount(@Param("id") Integer id,
                            @Param("homeworkCount") Integer homeworkCount);

    /**
     * 根据状态查询课程
     */
    List<Course> selectByStatus(String status);

    /**
     * 分页查询课程
     */
    List<Course> selectByPage(@Param("course") Course course,
                              @Param("offset") Integer offset,
                              @Param("pageSize") Integer pageSize);
}