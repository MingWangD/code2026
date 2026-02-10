package com.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExamQuestionMapper {

    @Select("""
        SELECT
            eq.exam_id,
            q.id            AS question_id,
            q.content,
            q.option_a,
            q.option_b,
            q.option_c,
            q.option_d,
            q.correct_option,
            eq.score
        FROM exam_question eq
        JOIN question q ON eq.question_id = q.id
        WHERE eq.exam_id = #{examId}
        ORDER BY eq.sort_no
    """)
    List<Map<String, Object>> selectPaper(Integer examId);
}
