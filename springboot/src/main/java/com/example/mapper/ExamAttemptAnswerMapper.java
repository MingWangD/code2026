package com.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ExamAttemptAnswerMapper {

    @Insert("""
        INSERT INTO exam_attempt_answer
        (attempt_id, question_id, chosen_option, is_correct, score)
        VALUES
        (#{attemptId}, #{questionId}, #{chosenOption}, #{isCorrect}, #{score})
    """)
    void insert(@Param("attemptId") Integer attemptId,
                @Param("questionId") Integer questionId,
                @Param("chosenOption") String chosenOption,
                @Param("isCorrect") Integer isCorrect,
                @Param("score") Double score);
}
