package com.example.controller;

import com.example.common.Result;
import com.example.entity.Homework;
import com.example.service.HomeworkService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.controller.dto.HomeworkSubmitDTO;

/**
 * 作业前端操作接口
 */
@RestController
@RequestMapping("/homework")
public class HomeworkController {

    @PostMapping("/submit")
    public Result submit(@RequestBody HomeworkSubmitDTO dto) {
        homeworkService.submitHomework(dto);
        return Result.success();
    }

    @Resource
    private HomeworkService homeworkService;

    /**
     * 新增作业
     */
    @PostMapping("/add")
    public Result add(@RequestBody Homework homework) {
        homeworkService.add(homework);
        return Result.success();
    }

    /**
     * 删除作业
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        homeworkService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改作业
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Homework homework) {
        homeworkService.updateById(homework);
        return Result.success();
    }

    /**
     * 根据ID查询作业
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Homework homework = homeworkService.selectById(id);
        return Result.success(homework);
    }

    /**
     * 查询所有作业
     */
    @GetMapping("/selectAll")
    public Result selectAll(Homework homework) {
        List<Homework> list = homeworkService.selectAll(homework);
        return Result.success(list);
    }

    /**
     * 分页查询作业
     */
    @GetMapping("/selectPage")
    public Result selectPage(Homework homework,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Homework> page = homeworkService.selectPage(homework, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 根据课程ID查询作业
     */
    @GetMapping("/selectByCourse/{courseId}")
    public Result selectByCourseId(@PathVariable Integer courseId) {
        List<Homework> list = homeworkService.selectByCourseId(courseId);
        return Result.success(list);
    }

    /**
     * 根据课程ID和状态查询作业
     */
    @GetMapping("/selectByCourseAndStatus")
    public Result selectByCourseIdAndStatus(@RequestParam Integer courseId,
                                            @RequestParam String status) {
        List<Homework> list = homeworkService.selectByCourseIdAndStatus(courseId, status);
        return Result.success(list);
    }

    /**
     * 获取即将截止的作业
     */
    @GetMapping("/upcomingDeadlines")
    public Result getUpcomingDeadlines(@RequestParam(defaultValue = "3") Integer days) {
        List<Homework> list = homeworkService.getUpcomingDeadlines(days);
        return Result.success(list);
    }

    /**
     * 更新作业状态
     */
    @PutMapping("/updateStatus")
    public Result updateStatus(@RequestParam Integer id, @RequestParam String status) {
        homeworkService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 增加提交人数
     */
    @PutMapping("/incrementSubmitCount/{id}")
    public Result incrementSubmitCount(@PathVariable Integer id) {
        homeworkService.incrementSubmitCount(id);
        return Result.success();
    }

    /**
     * 增加批改人数
     */
    @PutMapping("/incrementGradedCount")
    public Result incrementGradedCount(@RequestParam Integer id, @RequestParam Double score) {
        homeworkService.incrementGradedCount(id, score);
        return Result.success();
    }

    /**
     * 统计作业数量
     */
    @GetMapping("/count")
    public Result count(@RequestParam(required = false) Integer courseId,
                        @RequestParam(required = false) String status) {
        Integer count = homeworkService.count(courseId, status);
        return Result.success(count);
    }

    /**
     * 搜索作业
     */
    @GetMapping("/search")
    public Result search(@RequestParam String keyword) {
        List<Homework> list = homeworkService.search(keyword);
        return Result.success(list);
    }

    /**
     * 检查作业是否已截止
     */
    @GetMapping("/isDeadlinePassed/{id}")
    public Result isDeadlinePassed(@PathVariable Integer id) {
        boolean isPassed = homeworkService.isDeadlinePassed(id);
        return Result.success(isPassed);
    }

    /**
     * 获取学生的未提交作业
     */
    @GetMapping("/unsubmitted")
    public Result getUnsubmittedHomework(@RequestParam Integer studentId,
                                         @RequestParam Integer courseId) {
        List<Homework> list = homeworkService.getUnsubmittedHomework(studentId, courseId);
        return Result.success(list);
    }

    /**
     * 批量更新作业状态
     */
    @PutMapping("/batchUpdateStatus")
    public Result batchUpdateStatus(@RequestParam List<Integer> ids,
                                    @RequestParam String status) {
        homeworkService.batchUpdateStatus(ids, status);
        return Result.success();
    }

    /**
     * 获取作业详情（包含统计信息）
     */
    @GetMapping("/detail/{id}")
    public Result getHomeworkDetail(@PathVariable Integer id) {
        Homework homework = homeworkService.selectById(id);
        // 可以在这里添加更多统计信息
        return Result.success(homework);
    }

    /**
     * 更新作业平均分
     */
    @PutMapping("/updateAverageScore/{id}")
    public Result updateAverageScore(@PathVariable Integer id) {
        homeworkService.updateAverageScore(id);
        return Result.success();
    }
}