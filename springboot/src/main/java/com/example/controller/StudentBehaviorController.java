package com.example.controller;

import com.example.common.Result;
import com.example.controller.dto.HomeworkSubmitDTO;
import com.example.entity.StudentBehavior;
import com.example.exception.CustomException;
import com.example.service.StudentBehaviorService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 学习行为事件接口（事件模型版本）
 * student_behavior 作为事件表：只插入，不更新
 */
@RestController
@RequestMapping("/behavior")
public class StudentBehaviorController {

    @Resource
    private StudentBehaviorService studentBehaviorService;

    // =========================
    // 1) 写入事件（核心）
    // =========================

    /**
     * 记录视频观看事件
     */
    @PostMapping("/event/videoWatch")
    public Result recordVideoWatch(@RequestParam Integer studentId,
                                   @RequestParam Integer courseId,
                                   @RequestParam Integer watchTime,       // 分钟
                                   @RequestParam Double completionRate) { // 0-100
        studentBehaviorService.recordVideoWatch(studentId, courseId, watchTime, completionRate);
        return Result.success();
    }

    /**
     * 记录登录事件
     */
    @PostMapping("/event/login")
    public Result recordLogin(@RequestParam Integer studentId,
                              @RequestParam Integer courseId) {
        studentBehaviorService.recordLogin(studentId, courseId);
        return Result.success();
    }

    /**
     * 记录作业提交事件（事件表）
     * 建议使用 DTO，和 HomeworkService.submitHomework 同一套入参
     */
    @PostMapping("/event/homeworkSubmit")
    public Result recordHomeworkSubmit(@RequestBody HomeworkSubmitDTO dto) {
        if (dto == null || dto.getStudentId() == null || dto.getCourseId() == null || dto.getHomeworkId() == null) {
            throw new CustomException("提交参数不完整");
        }
        studentBehaviorService.recordHomeworkSubmit(
                dto.getStudentId(),
                dto.getCourseId(),
                dto.getHomeworkId(),
                dto.getScore(),
                false, // isLate 建议在 HomeworkService 里算；这里只是事件入口，就先给 false（或你也可以加参数传进来）
                dto.getAttemptNo(),
                dto.getSubmitTime()
        );
        return Result.success();
    }

    // （可选）考试事件：你后面接考试模块时再启用
    // @PostMapping("/event/exam")
    // public Result recordExam(...) { ... }

    // =========================
    // 2) 查询事件（只读）
    // =========================

    /**
     * 按学生查询事件（最近的在前）
     */
    @GetMapping("/event/selectByStudent/{studentId}")
    public Result selectByStudentId(@PathVariable Integer studentId) {
        List<StudentBehavior> list = studentBehaviorService.selectByStudentId(studentId);
        return Result.success(list);
    }

    /**
     * 按课程查询事件（最近的在前）
     */
    @GetMapping("/event/selectByCourse/{courseId}")
    public Result selectByCourseId(@PathVariable Integer courseId) {
        List<StudentBehavior> list = studentBehaviorService.selectByCourseId(courseId);
        return Result.success(list);
    }

    @PostMapping("/event/videoProgress")
    public Result recordVideoProgress(@RequestBody com.example.controller.dto.VideoProgressDTO dto) {
        if (dto == null || dto.getStudentId() == null || dto.getCourseId() == null) {
            throw new com.example.exception.CustomException("studentId/courseId 不能为空");
        }
        if (dto.getDeltaSeconds() == null || dto.getDeltaSeconds() <= 0) {
            throw new com.example.exception.CustomException("deltaSeconds 必须 > 0");
        }

        // 直接复用你已有的 service（你也可以新建 VideoProgressService，但为了最小改动，这里先直接调）
        // 你需要在 StudentBehaviorService 里新增一个方法 recordVideoProgress(dto)
        studentBehaviorService.recordVideoProgress(dto);

        return Result.success();
    }


    /**
     * 按学生+课程查询事件（最近的在前）
     */
    @GetMapping("/event/selectByStudentAndCourse")
    public Result selectByStudentAndCourse(@RequestParam Integer studentId,
                                           @RequestParam Integer courseId) {
        List<StudentBehavior> list = studentBehaviorService.selectByStudentAndCourse(studentId, courseId);
        return Result.success(list);
    }

    /**
     * 按学生+课程+日期查询（某天的事件）
     */
    @GetMapping("/event/selectByDate")
    public Result selectByStudentCourseDate(@RequestParam Integer studentId,
                                            @RequestParam Integer courseId,
                                            @RequestParam(required = false) String date) {
        if (date == null || date.isBlank()) {
            date = LocalDate.now().toString();
        }
        List<StudentBehavior> list = studentBehaviorService.selectByStudentAndCourseAndDate(studentId, courseId, date);
        return Result.success(list);
    }

    /**
     * 统计某学生对某作业已提交次数（从事件表统计）
     */
    @GetMapping("/event/homeworkSubmitCount")
    public Result homeworkSubmitCount(@RequestParam Integer studentId,
                                      @RequestParam Integer courseId,
                                      @RequestParam Integer homeworkId) {
        Integer cnt = studentBehaviorService.countHomeworkSubmit(studentId, courseId, homeworkId);
        return Result.success(cnt == null ? 0 : cnt);
    }


    /**
     * 分页查询事件（简单版）
     * 你原来用 PageHelper 的 selectAll 也能保留
     */
    @GetMapping("/event/selectPage")
    public Result selectPage(StudentBehavior behavior,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<StudentBehavior> page = studentBehaviorService.selectPage(behavior, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 删除事件（一般不建议开放；保留给管理员排错用）
     */
    @DeleteMapping("/event/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        studentBehaviorService.deleteById(id);
        return Result.success();
    }
}
