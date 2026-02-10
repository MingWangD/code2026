package com.example.mapper;

import com.example.entity.Student;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生数据访问接口
 */
public interface StudentMapper {

    /**
     * 新增学生
     */
    int insert(Student student);

    /**
     * 删除学生
     */
    int deleteById(Integer id);

    /**
     * 修改学生
     */
    int updateById(Student student);

    /**
     * 根据ID查询学生
     */
    Student selectById(Integer id);

    /**
     * 查询所有学生
     */
    List<Student> selectAll(Student student);

    /**
     * 根据学号查询学生
     */
    Student selectByStudentNo(String studentNo);

    /**
     * 根据用户名查询学生
     */
    Student selectByUsername(String username);

    /**
     * 根据班级ID查询学生
     */
    List<Student> selectByClassId(Integer classId);

    /**
     * 根据条件查询学生（用于登录验证）
     */
    Student selectByCondition(@Param("username") String username,
                              @Param("password") String password,
                              @Param("status") String status);

    /**
     * 统计学生数量
     */
    int count(@Param("grade") String grade,
              @Param("major") String major,
              @Param("academicStatus") String academicStatus);

    /**
     * 批量插入学生
     */
    int batchInsert(List<Student> students);

    /**
     * 更新学业状态
     */
    int updateAcademicStatus(@Param("id") Integer id,
                             @Param("academicStatus") String academicStatus,
                             @Param("warningCount") Integer warningCount);

    /**
     * 分页查询学生
     */
    List<Student> selectByPage(@Param("student") Student student,
                               @Param("offset") Integer offset,
                               @Param("pageSize") Integer pageSize);
}