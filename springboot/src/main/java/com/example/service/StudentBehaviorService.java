package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.controller.dto.VideoProgressDTO;
import com.example.entity.StudentBehavior;
import com.example.exception.CustomException;
import com.example.mapper.StudentBehaviorMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentBehaviorService {

    @Resource
    private StudentBehaviorMapper studentBehaviorMapper;

    @Resource
    private com.example.mapper.StudentBehaviorEventMapper studentBehaviorEventMapper;

    // ✅ 用于把 extra 写成 JSON
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    public Integer countHomeworkSubmit(Integer studentId, Integer courseId, Integer homeworkId) {
        return studentBehaviorEventMapper.countHomeworkSubmit(studentId, courseId, homeworkId.toString());
    }

    /** ✅ 统一 DATETIME 格式（MySQL datetime 稳定可用） */
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String dt(LocalDateTime t) {
        return (t == null ? LocalDateTime.now() : t).format(DT);
    }

    // =========================
    // ✅ 通用 CRUD（StudentBehavior 表）
    // =========================

    public void add(StudentBehavior behavior) {
        if (behavior == null) throw new CustomException("行为数据不能为空");
        if (behavior.getStudentId() == null) throw new CustomException("学生ID不能为空");
        if (behavior.getCourseId() == null) throw new CustomException("课程ID不能为空");
        if (ObjectUtil.isEmpty(behavior.getBehaviorType())) throw new CustomException("行为类型不能为空");

        if (ObjectUtil.isEmpty(behavior.getBehaviorTime())) {
            behavior.setBehaviorTime(dt(null));
        }
        if (ObjectUtil.isEmpty(behavior.getCreateTime())) {
            behavior.setCreateTime(dt(null));
        }

        studentBehaviorMapper.insert(behavior);
    }

    public void deleteById(Integer id) {
        StudentBehavior behavior = studentBehaviorMapper.selectById(id);
        if (behavior == null) throw new CustomException("学习行为记录不存在");
        studentBehaviorMapper.deleteById(id);
    }

    public StudentBehavior selectById(Integer id) {
        StudentBehavior behavior = studentBehaviorMapper.selectById(id);
        if (behavior == null) throw new CustomException("学习行为记录不存在");
        return behavior;
    }

    public List<StudentBehavior> selectAll(StudentBehavior query) {
        return studentBehaviorMapper.selectAll(query);
    }

    public PageInfo<StudentBehavior> selectPage(StudentBehavior query, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(studentBehaviorMapper.selectAll(query));
    }

    public List<StudentBehavior> selectByStudentId(Integer studentId) {
        return studentBehaviorMapper.selectByStudentId(studentId);
    }

    public List<StudentBehavior> selectByCourseId(Integer courseId) {
        return studentBehaviorMapper.selectByCourseId(courseId);
    }

    public List<StudentBehavior> selectByStudentAndCourse(Integer studentId, Integer courseId) {
        return studentBehaviorMapper.selectByStudentAndCourse(studentId, courseId);
    }

    public List<StudentBehavior> selectByStudentAndCourseAndDate(Integer studentId, Integer courseId, String date) {
        return studentBehaviorMapper.selectEventsByStudentCourseAndDate(studentId, courseId, date);
    }

    /**
     * ✅ 作业提交事件 -> student_behavior_event（事实表）
     * 统一事件源：只写 student_behavior_event
     */
    @Transactional(rollbackFor = Exception.class)
    public void recordHomeworkSubmit(
            Integer studentId,
            Integer courseId,
            Integer homeworkId,
            Double score,
            boolean isLate,
            Integer attemptNo,
            LocalDateTime submitTime
    ) {
        if (studentId == null || courseId == null || homeworkId == null) {
            throw new CustomException("参数不完整");
        }

        if (attemptNo == null || attemptNo <= 0) {
            throw new CustomException("attemptNo 必须 > 0（作业允许多次提交）");
        }

        if (submitTime == null) submitTime = LocalDateTime.now();

        studentBehaviorEventMapper.insertEvent(
                studentId,
                courseId,
                "HOMEWORK_SUBMIT",
                String.valueOf(homeworkId),
                score,
                isLate ? 1 : 0,
                attemptNo,
                null,     // behaviorValue
                null,     // behaviorExtra
                submitTime
        );
    }

    // ==========================================
    // ✅ 看视频事件（你旧的 StudentBehavior 表写法保留）
    // ==========================================
    @Transactional(rollbackFor = Exception.class)
    public void recordVideoWatch(
            Integer studentId,
            Integer courseId,
            Integer videoId,
            Integer watchMinutes,
            Double completionRate,
            LocalDateTime watchTime
    ) {
        if (studentId == null || courseId == null) throw new CustomException("参数不完整");
        if (watchTime == null) watchTime = LocalDateTime.now();

        StudentBehavior behavior = new StudentBehavior();
        behavior.setStudentId(studentId);
        behavior.setCourseId(courseId);
        behavior.setBehaviorType("VIDEO_WATCH");
        behavior.setRelatedId(videoId == null ? null : String.valueOf(videoId));

        if (completionRate != null) behavior.setScore(completionRate);
        if (watchMinutes != null) behavior.setAttemptNo(watchMinutes); // 临时复用

        behavior.setBehaviorTime(dt(watchTime));
        behavior.setCreateTime(dt(null));

        studentBehaviorMapper.insert(behavior);
    }

    @Transactional(rollbackFor = Exception.class)
    public void recordLogin(Integer studentId, Integer courseId) {
        recordLogin(studentId, courseId, LocalDateTime.now());
    }

    // ✅ 给 Controller 用的【简化版】
    @Transactional(rollbackFor = Exception.class)
    public void recordVideoWatch(
            Integer studentId,
            Integer courseId,
            Integer watchMinutes,
            Double completionRate
    ) {
        recordVideoWatch(
                studentId,
                courseId,
                null,
                watchMinutes,
                completionRate,
                LocalDateTime.now()
        );
    }

    // ==========================================
    // ✅ 登录事件（旧 StudentBehavior 表写法保留）
    // ==========================================
    @Transactional(rollbackFor = Exception.class)
    public void recordLogin(Integer studentId, Integer courseId, LocalDateTime loginTime) {
        if (studentId == null || courseId == null) throw new CustomException("参数不完整");
        if (loginTime == null) loginTime = LocalDateTime.now();

        StudentBehavior behavior = new StudentBehavior();
        behavior.setStudentId(studentId);
        behavior.setCourseId(courseId);
        behavior.setBehaviorType("LOGIN");

        behavior.setBehaviorTime(dt(loginTime));
        behavior.setCreateTime(dt(null));

        studentBehaviorMapper.insert(behavior);
    }

    // ==========================================
    // ✅ 考试事件（旧 StudentBehavior 表写法保留）
    // ==========================================
    @Transactional(rollbackFor = Exception.class)
    public void recordExam(
            Integer studentId,
            Integer courseId,
            Integer examId,
            Double score,
            LocalDateTime examTime
    ) {
        if (studentId == null || courseId == null || examId == null) throw new CustomException("参数不完整");
        if (examTime == null) examTime = LocalDateTime.now();

        StudentBehavior behavior = new StudentBehavior();
        behavior.setStudentId(studentId);
        behavior.setCourseId(courseId);
        behavior.setBehaviorType("EXAM");
        behavior.setRelatedId(String.valueOf(examId));
        behavior.setScore(score);

        behavior.setBehaviorTime(dt(examTime));
        behavior.setCreateTime(dt(null));

        studentBehaviorMapper.insert(behavior);
    }

    // ==========================================
    // ✅ 关键：视频心跳 + 完成判定（事件表闭环）
    // ==========================================
    @Transactional(rollbackFor = Exception.class)
    public void recordVideoProgress(VideoProgressDTO dto) {
        if (dto == null) throw new CustomException("dto 不能为空");
        if (dto.getStudentId() == null || dto.getCourseId() == null) throw new CustomException("studentId/courseId 不能为空");
        if (dto.getDeltaSeconds() == null || dto.getDeltaSeconds() <= 0) throw new CustomException("deltaSeconds 必须 > 0");

        try {
            // 1) 写入 VIDEO_PROGRESS 事件（心跳）
            Map<String, Object> extra = new HashMap<>();
            extra.put("currentTime", dto.getCurrentTime());
            extra.put("duration", dto.getDuration());
            extra.put("playbackRate", dto.getPlaybackRate());
            String extraJson = objectMapper.writeValueAsString(extra);

            studentBehaviorEventMapper.insertEvent(
                    dto.getStudentId(),
                    dto.getCourseId(),
                    "VIDEO_PROGRESS",
                    null,              // relatedId
                    null,              // score
                    0,                 // isLate
                    1,                 // attemptNo（视频心跳固定 1）
                    dto.getDeltaSeconds().doubleValue(),
                    extraJson,
                    LocalDateTime.now()
            );

            // 2) 完成判定：当天累计观看秒数 >= duration * 0.9
            //    说明：你目前 course 表没有视频时长字段，只能先用前端传的 duration（后续建议改为 course.video_duration_sec）
            double duration = dto.getDuration() == null ? 0 : dto.getDuration();
            if (duration <= 0) return; // 没有时长就无法判定完成

            LocalDate today = LocalDate.now();
            int watchedSec = studentBehaviorEventMapper.sumVideoSeconds(dto.getStudentId(), dto.getCourseId(), today);

            boolean completed = watchedSec >= (int) Math.floor(duration * 0.9);
            if (!completed) return;

            // 3) 幂等：同一天只写一次 VIDEO_COMPLETE
            boolean already = studentBehaviorEventMapper.existsCompleteEvent(dto.getStudentId(), dto.getCourseId(), today);
            if (already) return;

            studentBehaviorEventMapper.insertEvent(
                    dto.getStudentId(),
                    dto.getCourseId(),
                    "VIDEO_COMPLETE",
                    String.valueOf(dto.getCourseId()), // relatedId = courseId
                    null,
                    0,
                    1,
                    null,
                    null,
                    LocalDateTime.now()
            );

        } catch (Exception e) {
            throw new CustomException("写入 VIDEO_PROGRESS/VIDEO_COMPLETE 失败：" + e.getMessage());
        }
    }
}
