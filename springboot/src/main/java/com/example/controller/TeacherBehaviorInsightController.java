package com.example.controller;

import com.example.common.Result;
import com.example.entity.StudentBehavior;
import com.example.mapper.StudentBehaviorMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 教师侧：行为判读层（只读）
 * 从 student_behavior_event 聚合出“可解释结论”
 */
@RestController
@RequestMapping("/teacher/behavior")
public class TeacherBehaviorInsightController {

    @Resource
    private StudentBehaviorMapper studentBehaviorMapper;

    /**
     * 按 学生+课程+日期 生成“判读结论”
     * date: yyyy-MM-dd，不传默认今天
     */
    @GetMapping("/insights")
    public Result insights(@RequestParam Integer studentId,
                           @RequestParam Integer courseId,
                           @RequestParam(required = false) String date) {

        if (date == null || date.isBlank()) {
            date = LocalDate.now().toString();
        }

        // 你已经在 Mapper 里加了这个方法 + XML 也写了
        List<StudentBehavior> events =
                studentBehaviorMapper.selectEventsByStudentCourseAndDate(studentId, courseId, date);

        Map<String, Object> data = buildInsights(events, date);
        return Result.success(data);
    }

    /**
     * 规则：极简可用版（答辩够用）
     * 后面你要“更像AI”，我们再迭代规则/阈值/权重
     */
    private Map<String, Object> buildInsights(List<StudentBehavior> events, String date) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("date", date);
        res.put("eventCount", events == null ? 0 : events.size());

        if (events == null || events.isEmpty()) {
            res.put("summary", "当日暂无学习行为记录");
            res.put("tags", Collections.emptyList());
            res.put("metrics", Collections.emptyMap());
            res.put("latestEvents", Collections.emptyList());
            return res;
        }

        // 事件按类型分组
        Map<String, List<StudentBehavior>> byType = events.stream()
                .collect(Collectors.groupingBy(e -> e.getBehaviorType() == null ? "UNKNOWN" : e.getBehaviorType()));

        int loginCount = byType.getOrDefault("LOGIN", Collections.emptyList()).size();

        List<StudentBehavior> videoEvents = byType.getOrDefault("VIDEO_WATCH", Collections.emptyList());
        int videoCount = videoEvents.size();
        // 约定：你当前 VIDEO_WATCH 的 watchTime/completionRate 怎么存？
        // 你现有 StudentBehavior 字段里只有 score / attemptNo / relatedId / isLate
        // 所以这里先做“保底”：只统计次数，不统计时长/完成率（后面再补字段）
        // 如果你确实把 watchTime 存在 attemptNo、completionRate 存在 score，也能在下一步调整映射规则

        List<StudentBehavior> hwEvents = byType.getOrDefault("HOMEWORK_SUBMIT", Collections.emptyList());
        int hwSubmitCount = hwEvents.size();
        long hwLateCount = hwEvents.stream().filter(e -> e.getIsLate() != null && e.getIsLate() == 1).count();
        Double hwAvgScore = hwEvents.stream()
                .filter(e -> e.getScore() != null)
                .mapToDouble(StudentBehavior::getScore)
                .average().isPresent()
                ? hwEvents.stream().filter(e -> e.getScore() != null).mapToDouble(StudentBehavior::getScore).average().getAsDouble()
                : null;

        int examCount = byType.getOrDefault("EXAM", Collections.emptyList()).size();

        // ---- 规则标签（可解释）----
        List<String> tags = new ArrayList<>();

        if (loginCount == 0) tags.add("今日未登录");
        if (videoCount == 0) tags.add("今日无视频学习");
        if (hwSubmitCount == 0) tags.add("今日未提交作业");
        if (hwLateCount >= 1) tags.add("存在迟交作业");
        if (hwAvgScore != null && hwAvgScore < 60) tags.add("作业均分偏低(<60)");
        if (examCount >= 1) tags.add("今日有考试行为");

        // 生成一句 summary
        StringBuilder summary = new StringBuilder();
        summary.append("今日：登录 ").append(loginCount).append(" 次；");
        summary.append("视频 ").append(videoCount).append(" 次；");
        summary.append("作业提交 ").append(hwSubmitCount).append(" 次");
        if (hwLateCount > 0) summary.append("（迟交 ").append(hwLateCount).append(" 次）");
        if (hwAvgScore != null) summary.append("；作业均分 ").append(String.format(Locale.ROOT, "%.1f", hwAvgScore));
        summary.append("；考试 ").append(examCount).append(" 次。");

        // metrics
        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("loginCount", loginCount);
        metrics.put("videoCount", videoCount);
        metrics.put("homeworkSubmitCount", hwSubmitCount);
        metrics.put("homeworkLateCount", hwLateCount);
        metrics.put("homeworkAvgScore", hwAvgScore);
        metrics.put("examCount", examCount);

        // latestEvents（取最近10条）
        List<StudentBehavior> latest = events.stream()
                .sorted(Comparator.comparing(StudentBehavior::getBehaviorTime, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(10)
                .collect(Collectors.toList());

        res.put("summary", summary.toString());
        res.put("tags", tags);
        res.put("metrics", metrics);
        res.put("latestEvents", latest);
        return res;
    }
}
