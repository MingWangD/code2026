package com.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExamMapper {

    @Select("""
        SELECT *
        FROM exam
        WHERE course_id = #{courseId}
          AND status = '发布'
    """)
    List<Map<String, Object>> selectAvailable(Integer courseId);

    @Select("""
        SELECT *
        FROM exam
        WHERE id = #{examId}
    """)
    Map<String, Object> selectById(Integer examId);
}
