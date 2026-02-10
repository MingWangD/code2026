package com.example.service;

import com.example.exception.CustomException;
import com.example.mapper.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ExamService {

    @Resource private ExamMapper examMapper;
    @Resource private ExamQuestionMapper examQuestionMapper;
    @Resource private ExamAttemptMapper examAttemptMapper;
    @Resource private ExamAttemptAnswerMapper examAttemptAnswerMapper;
    @Resource private StudentBehaviorEventMapper studentBehaviorEventMapper;

    // ===== DTO =====
    public static class SubmitDTO {
        public Integer studentId;
        public Integer courseId;
        public Integer examId;
        public Map<String, String> answers;
    }

    // ===== 查询 =====
    public List<Map<String,Object>> listAvailable(Integer courseId){
        return examMapper.selectAvailable(courseId);
    }

    public Map<String,Object> getPaper(Integer examId){
        List<Map<String,Object>> qs = examQuestionMapper.selectPaper(examId);
        Map<String,Object> r = new HashMap<>();
        r.put("exam", examMapper.selectById(examId));
        r.put("questions", qs);
        return r;
    }

    // ===== 提交 =====
    @Transactional(rollbackFor = Exception.class)
    public Map<String,Object> submit(SubmitDTO dto){
        if(dto==null || dto.studentId==null || dto.courseId==null || dto.examId==null)
            throw new CustomException("参数不完整");

        // 第几次考试
        Integer attemptNo = examAttemptMapper.countByStudent(dto.examId, dto.studentId) + 1;

        // 拉定卷
        List<Map<String,Object>> paper = examQuestionMapper.selectPaper(dto.examId);
        double total = 0d;

        // 新 attempt
        examAttemptMapper.insertAttempt(dto.examId, dto.courseId, dto.studentId,
                LocalDateTime.now(), "SUBMITTED");

        Integer attemptId = examAttemptMapper.lastId();

        for(Map<String,Object> q : paper){
            Integer qid = (Integer) q.get("question_id");
            Double score = ((Number) q.get("score")).doubleValue();
            String correct = (String) q.get("correct_option");
            String chosen = dto.answers.get(String.valueOf(qid));
            boolean ok = Objects.equals(correct, chosen);
            double got = ok ? score : 0d;
            total += got;

            examAttemptAnswerMapper.insert(attemptId, qid, chosen, ok?1:0, got);
        }

        examAttemptMapper.finish(attemptId, total);

        // 写事件（事实源）
        studentBehaviorEventMapper.insertEvent(
                dto.studentId,
                dto.courseId,
                "EXAM_SUBMIT",
                String.valueOf(dto.examId),
                total,
                0,
                attemptNo,
                null,
                null,
                LocalDateTime.now()
        );

        Map<String,Object> r = new HashMap<>();
        r.put("attemptNo", attemptNo);
        r.put("score", total);
        return r;
    }
}
