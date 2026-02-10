package com.example.mapper;

import com.example.entity.Teacher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教师数据访问接口
 */
public interface TeacherMapper {

    /**
     * 新增教师
     */
    int insert(Teacher teacher);

    /**
     * 删除教师
     */
    int deleteById(Integer id);

    /**
     * 修改教师
     */
    int updateById(Teacher teacher);

    /**
     * 根据ID查询教师
     */
    Teacher selectById(Integer id);

    /**
     * 查询所有教师
     */
    List<Teacher> selectAll(Teacher teacher);

    /**
     * 根据工号查询教师
     */
    Teacher selectByTeacherNo(String teacherNo);

    /**
     * 根据用户名查询教师
     */
    Teacher selectByUsername(String username);

    /**
     * 根据条件查询教师（用于登录验证）
     */
    Teacher selectByCondition(@Param("username") String username,
                              @Param("password") String password,
                              @Param("status") String status);

    /**
     * 统计教师数量
     */
    int count(@Param("department") String department,
              @Param("title") String title);

    /**
     * 根据院系查询教师
     */
    List<Teacher> selectByDepartment(String department);

    /**
     * 更新教师课程数量
     */
    int updateCourseCount(@Param("id") Integer id,
                          @Param("courseCount") Integer courseCount);

    /**
     * 批量插入教师
     */
    int batchInsert(List<Teacher> teachers);
}