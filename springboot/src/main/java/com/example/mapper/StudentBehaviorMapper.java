package com.example.mapper;

import com.example.entity.StudentBehavior;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StudentBehaviorMapper {

    int insert(StudentBehavior behavior);

    int batchInsert(List<StudentBehavior> list);

    int deleteById(Integer id);

    StudentBehavior selectById(Integer id);

    List<StudentBehavior> selectAll(StudentBehavior behavior);

    List<StudentBehavior> selectByStudentId(Integer studentId);

    List<StudentBehavior> selectByCourseId(Integer courseId);

    List<StudentBehavior> selectByStudentAndCourse(
            @Param("studentId") Integer studentId,
            @Param("courseId") Integer courseId
    );

    // ✅ 新增：查某天事件（按 behavior_time 归属日期）
    List<StudentBehavior> selectEventsByStudentCourseAndDate(
            @Param("studentId") Integer studentId,
            @Param("courseId") Integer courseId,
            @Param("date") String date // yyyy-MM-dd
    );

    // ✅ 新增：查某天作业提交事件（可少扫数据）
    List<StudentBehavior> selectHomeworkSubmitEventsByStudentCourseAndDate(
            @Param("studentId") Integer studentId,
            @Param("courseId") Integer courseId,
            @Param("date") String date // yyyy-MM-dd
    );
}
