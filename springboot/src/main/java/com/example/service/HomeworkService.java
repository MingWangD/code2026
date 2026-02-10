package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.controller.dto.HomeworkSubmitDTO;
import com.example.entity.Homework;
import com.example.exception.CustomException;
import com.example.mapper.HomeworkMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class HomeworkService {

    @Resource
    private HomeworkMapper homeworkMapper;

    @Resource
    private StudentBehaviorService studentBehaviorService;

    @Resource
    private LearningFeaturesService learningFeaturesService;

    /**
     * 新增作业
     */
    public void add(Homework homework) {
        // 验证截止时间
        if (ObjectUtil.isEmpty(homework.getDeadline())) {
            throw new CustomException("截止时间不能为空");
        }

        // 设置默认值
        if (ObjectUtil.isEmpty(homework.getTotalScore())) {
            homework.setTotalScore(100.0);
        }
        if (ObjectUtil.isEmpty(homework.getSubmitCount())) {
            homework.setSubmitCount(0);
        }
        if (ObjectUtil.isEmpty(homework.getGradedCount())) {
            homework.setGradedCount(0);
        }
        if (ObjectUtil.isEmpty(homework.getAverageScore())) {
            homework.setAverageScore(0.0);
        }

        // 根据时间设置状态
        String nowStr = LocalDateTime.now().toString();
        // startTime 可能是 String / LocalDateTime，这里不强行比较类型，避免编译问题
        if (ObjectUtil.isNotEmpty(homework.getStartTime())) {
            LocalDateTime start = parseToDateTime(homework.getStartTime());
            if (start != null && start.isAfter(LocalDateTime.now())) {
                homework.setStatus("未开始");
            } else {
                homework.setStatus("进行中");
            }
        } else {
            homework.setStatus("进行中");
        }

        homework.setCreateTime(nowStr);
        homework.setUpdateTime(nowStr);

        homeworkMapper.insert(homework);
    }

    /**
     * 删除作业
     */
    public void deleteById(Integer id) {
        Homework homework = homeworkMapper.selectById(id);
        if (ObjectUtil.isNull(homework)) {
            throw new CustomException("作业不存在");
        }
        homeworkMapper.deleteById(id);
    }

    /**
     * 修改作业
     */
    public void updateById(Homework homework) {
        Homework dbHomework = homeworkMapper.selectById(homework.getId());
        if (ObjectUtil.isNull(dbHomework)) {
            throw new CustomException("作业不存在");
        }

        homework.setUpdateTime(LocalDateTime.now().toString());
        homeworkMapper.updateById(homework);
    }

    /**
     * 根据ID查询作业
     */
    public Homework selectById(Integer id) {
        Homework homework = homeworkMapper.selectById(id);
        if (ObjectUtil.isNull(homework)) {
            throw new CustomException("作业不存在");
        }
        return homework;
    }

    /**
     * 查询所有作业
     */
    public List<Homework> selectAll(Homework homework) {
        return homeworkMapper.selectAll(homework);
    }

    /**
     * 分页查询作业
     */
    public PageInfo<Homework> selectPage(Homework homework, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Homework> list = homeworkMapper.selectAll(homework);
        return PageInfo.of(list);
    }

    /**
     * 根据课程ID查询作业
     */
    public List<Homework> selectByCourseId(Integer courseId) {
        return homeworkMapper.selectByCourseId(courseId);
    }

    /**
     * 根据课程ID和状态查询作业
     */
    public List<Homework> selectByCourseIdAndStatus(Integer courseId, String status) {
        return homeworkMapper.selectByCourseIdAndStatus(courseId, status);
    }

    /**
     * 更新作业统计信息
     */
    public void updateStatistics(Integer id, Integer submitCount, Integer gradedCount,
                                 Double averageScore, String status) {
        Homework homework = homeworkMapper.selectById(id);
        if (ObjectUtil.isNull(homework)) {
            throw new CustomException("作业不存在");
        }

        homeworkMapper.updateStatistics(id, submitCount, gradedCount, averageScore, status);
    }

    /**
     * 增加提交人数
     */
    public void incrementSubmitCount(Integer id) {
        Homework homework = homeworkMapper.selectById(id);
        if (ObjectUtil.isNull(homework)) {
            throw new CustomException("作业不存在");
        }

        int newSubmitCount = homework.getSubmitCount() + 1;
        homeworkMapper.updateStatistics(id, newSubmitCount, homework.getGradedCount(),
                homework.getAverageScore(), homework.getStatus());
    }

    /**
     * 增加批改人数
     */
    public void incrementGradedCount(Integer id, Double score) {
        Homework homework = homeworkMapper.selectById(id);
        if (ObjectUtil.isNull(homework)) {
            throw new CustomException("作业不存在");
        }

        int newGradedCount = homework.getGradedCount() + 1;

        // 计算新的平均分
        double totalScore = homework.getAverageScore() * homework.getGradedCount() + score;
        double newAverageScore = totalScore / newGradedCount;

        // 如果所有提交的作业都已批改，更新状态为已批改
        String newStatus = homework.getStatus();
        if (newGradedCount >= homework.getSubmitCount()) {
            newStatus = "已批改";
        }

        homeworkMapper.updateStatistics(id, homework.getSubmitCount(), newGradedCount,
                newAverageScore, newStatus);
    }

    /**
     * 获取即将截止的作业
     */
    public List<Homework> getUpcomingDeadlines(Integer days) {
        if (ObjectUtil.isEmpty(days)) {
            days = 3; // 默认查询未来3天
        }
        return homeworkMapper.selectUpcomingDeadlines(days);
    }

    /**
     * 统计作业数量
     */
    public Integer count(Integer courseId, String status) {
        return homeworkMapper.count(courseId, status);
    }

    /**
     * 搜索作业
     */
    public List<Homework> search(String keyword) {
        Homework query = new Homework();
        query.setTitle(keyword);
        query.setCourseName(keyword);
        return homeworkMapper.selectAll(query);
    }

    /**
     * 更新作业状态（例如：未开始->进行中，进行中->已截止等）
     */
    public void updateStatus(Integer id, String status) {
        Homework homework = homeworkMapper.selectById(id);
        if (ObjectUtil.isNull(homework)) {
            throw new CustomException("作业不存在");
        }
        homework.setStatus(status);
        homework.setUpdateTime(LocalDateTime.now().toString());
        homeworkMapper.updateById(homework);
    }

    /**
     * 检查作业是否已截止
     */
    public boolean isDeadlinePassed(Integer id) {
        Homework homework = homeworkMapper.selectById(id);
        if (ObjectUtil.isNull(homework)) {
            throw new CustomException("作业不存在");
        }

        LocalDateTime deadline = parseToDateTime(homework.getDeadline());
        if (deadline == null) {
            // 如果 deadline 格式不对，保守处理：认为未截止
            return false;
        }
        return deadline.isBefore(LocalDateTime.now());
    }

    /**
     * 获取学生的未提交作业
     */
    public List<Homework> getUnsubmittedHomework(Integer studentId, Integer courseId) {
        // 这里后续你会接 submit_record 表来做精确判断
        return homeworkMapper.selectByCourseIdAndStatus(courseId, "进行中");
    }

    /**
     * 批量更新作业状态
     */
    public void batchUpdateStatus(List<Integer> ids, String status) {
        for (Integer id : ids) {
            updateStatus(id, status);
        }
    }

    /**
     * 更新作业平均分
     */
    public void updateAverageScore(Integer id) {
        Homework homework = homeworkMapper.selectById(id);
        if (ObjectUtil.isNull(homework)) {
            throw new CustomException("作业不存在");
        }
        homework.setUpdateTime(LocalDateTime.now().toString());
        homeworkMapper.updateById(homework);
    }

    /**
     * 学生提交作业（核心业务）
     */
    @Transactional(rollbackFor = Exception.class)
    public void submitHomework(HomeworkSubmitDTO dto) {

        // 1️⃣ 基础校验
        if (dto == null || dto.getStudentId() == null || dto.getCourseId() == null || dto.getHomeworkId() == null) {
            throw new CustomException("提交参数不完整");
        }

        Homework homework = homeworkMapper.selectById(dto.getHomeworkId());
        if (homework == null) {
            throw new CustomException("作业不存在");
        }

        // 2️⃣ 提交时间兜底
        LocalDateTime submitTime = dto.getSubmitTime() != null ? dto.getSubmitTime() : LocalDateTime.now();

        // 3️⃣ 判断是否迟交
        LocalDateTime deadline = parseToDateTime(homework.getDeadline());
        boolean isLate = (deadline != null) && submitTime.isAfter(deadline);

        // 4️⃣ 记录学习行为（⭐ 风险系统的核心数据源）
        // 如果你还没实现该方法，会编译报错：那就按我下面给你的 StudentBehaviorService 去实现
        studentBehaviorService.recordHomeworkSubmit(
                dto.getStudentId(),
                dto.getCourseId(),
                dto.getHomeworkId(),
                dto.getScore(),
                isLate,
                dto.getAttemptNo(),
                submitTime
        );

        // 5️⃣ 更新作业提交人数
        incrementSubmitCount(dto.getHomeworkId());

        // 6️⃣ 如果本次有成绩（老师已批改）
        if (dto.getScore() != null) {
            incrementGradedCount(dto.getHomeworkId(), dto.getScore());
        }

        // 7️⃣ 触发特征更新（当天）
        // 如果你还没实现 refreshTodayFeatures，会编译报错：可以先按我下面建议加一个空实现兜底
        //learningFeaturesService.refreshTodayFeatures(dto.getStudentId(), dto.getCourseId());
    }

    /**
     * 将 String / LocalDateTime 兼容解析为 LocalDateTime
     */
    private LocalDateTime parseToDateTime(Object time) {
        if (time == null) return null;
        if (time instanceof LocalDateTime) return (LocalDateTime) time;

        if (time instanceof String s) {
            if (s.isBlank()) return null;

            // 兼容常见格式：2026-02-07T11:29:00 / 2026-02-07 11:29:00
            try {
                return LocalDateTime.parse(s);
            } catch (Exception ignored) {
            }
            try {
                return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
