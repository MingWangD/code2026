package com.example.mapper;

import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper
public interface StudentBehaviorEventMapper {

    @Insert("""
    INSERT INTO student_behavior_event
    (student_id, course_id, behavior_type, related_id, score, is_late, attempt_no, behavior_value, behavior_extra, behavior_time)
    VALUES
    (#{studentId}, #{courseId}, #{behaviorType}, #{relatedId}, #{score}, #{isLate}, #{attemptNo}, #{behaviorValue},
     CASE 
       WHEN #{behaviorExtra} IS NULL OR #{behaviorExtra} = '' THEN NULL
       ELSE CAST(#{behaviorExtra} AS JSON)
     END, #{behaviorTime})
    """)
    int insertEvent(@Param("studentId") Integer studentId,
                    @Param("courseId") Integer courseId,
                    @Param("behaviorType") String behaviorType,
                    @Param("relatedId") String relatedId,
                    @Param("score") Double score,
                    @Param("isLate") Integer isLate,
                    @Param("attemptNo") Integer attemptNo,
                    @Param("behaviorValue") Double behaviorValue,
                    @Param("behaviorExtra") String behaviorExtra,
                    @Param("behaviorTime") LocalDateTime behaviorTime);

    @Select("""
    SELECT COUNT(1)
    FROM student_behavior_event
    WHERE student_id = #{studentId}
      AND course_id = #{courseId}
      AND behavior_type = 'HOMEWORK_SUBMIT'
      AND related_id = #{homeworkId}
    """)
    Integer countHomeworkSubmit(@Param("studentId") Integer studentId,
                                @Param("courseId") Integer courseId,
                                @Param("homeworkId") String homeworkId);

    /**
     * 你原来就有：从某个时间开始累计观看秒数（保留，后续也可能用）
     */
    @Select("""
        SELECT COALESCE(SUM(behavior_value), 0)
        FROM student_behavior_event
        WHERE student_id = #{studentId}
          AND course_id = #{courseId}
          AND behavior_type = 'VIDEO_PROGRESS'
          AND behavior_time >= #{fromTime}
        """)
    Double sumWatchSecondsFrom(@Param("studentId") Integer studentId,
                               @Param("courseId") Integer courseId,
                               @Param("fromTime") LocalDateTime fromTime);

    /**
     * ✅ 新增：当天累计观看秒数（用于完成判定）
     * 注意：这里返回 Integer，和 Service 里的 int watchedSec 对齐
     */
    @Select("""
        SELECT COALESCE(SUM(behavior_value), 0)
        FROM student_behavior_event
        WHERE student_id = #{studentId}
          AND course_id = #{courseId}
          AND behavior_type = 'VIDEO_PROGRESS'
          AND DATE(behavior_time) = #{date}
        """)
    Integer sumVideoSeconds(@Param("studentId") Integer studentId,
                            @Param("courseId") Integer courseId,
                            @Param("date") LocalDate date);

    /**
     * ✅ 新增：当天是否已经写入 VIDEO_COMPLETE（幂等）
     */
    @Select("""
        SELECT (COUNT(1) > 0)
        FROM student_behavior_event
        WHERE student_id = #{studentId}
          AND course_id = #{courseId}
          AND behavior_type = 'VIDEO_COMPLETE'
          AND DATE(behavior_time) = #{date}
        """)
    Boolean existsCompleteEvent(@Param("studentId") Integer studentId,
                                @Param("courseId") Integer courseId,
                                @Param("date") LocalDate date);
}
