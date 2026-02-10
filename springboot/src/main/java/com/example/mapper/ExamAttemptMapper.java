package com.example.mapper;

import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

@Mapper
public interface ExamAttemptMapper {

    @Select("""
        SELECT COUNT(1)
        FROM exam_attempt
        WHERE exam_id = #{examId}
          AND student_id = #{studentId}
    """)
    Integer countByStudent(@Param("examId") Integer examId,
                           @Param("studentId") Integer studentId);

    @Insert("""
        INSERT INTO exam_attempt
        (exam_id, course_id, student_id, start_time, status)
        VALUES
        (#{examId}, #{courseId}, #{studentId}, #{startTime}, #{status})
    """)
    void insertAttempt(@Param("examId") Integer examId,
                       @Param("courseId") Integer courseId,
                       @Param("studentId") Integer studentId,
                       @Param("startTime") LocalDateTime startTime,
                       @Param("status") String status);

    @Select("SELECT LAST_INSERT_ID()")
    Integer lastId();

    @Update("""
        UPDATE exam_attempt
        SET submit_time = NOW(),
            score = #{score},
            status = 'SUBMITTED'
        WHERE id = #{attemptId}
    """)
    void finish(@Param("attemptId") Integer attemptId,
                @Param("score") Double score);
}
