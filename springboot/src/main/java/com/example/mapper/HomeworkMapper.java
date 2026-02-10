package com.example.mapper;

import com.example.entity.Homework;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业数据访问接口
 */
public interface HomeworkMapper {

    /**
     * 新增作业
     */
    int insert(Homework homework);

    /**
     * 删除作业
     */
    int deleteById(Integer id);

    /**
     * 修改作业
     */
    int updateById(Homework homework);

    /**
     * 根据ID查询作业
     */
    Homework selectById(Integer id);

    /**
     * 查询所有作业
     */
    List<Homework> selectAll(Homework homework);

    /**
     * 根据课程ID查询作业
     */
    List<Homework> selectByCourseId(Integer courseId);

    /**
     * 根据课程ID和状态查询作业
     */
    List<Homework> selectByCourseIdAndStatus(@Param("courseId") Integer courseId,
                                             @Param("status") String status);

    /**
     * 统计作业数量
     */
    int count(@Param("courseId") Integer courseId,
              @Param("status") String status);

    /**
     * 更新作业统计信息
     */
    int updateStatistics(@Param("id") Integer id,
                         @Param("submitCount") Integer submitCount,
                         @Param("gradedCount") Integer gradedCount,
                         @Param("averageScore") Double averageScore,
                         @Param("status") String status);

    /**
     * 根据截止时间查询即将截止的作业
     */
    List<Homework> selectUpcomingDeadlines(@Param("days") Integer days);

    /**
     * 分页查询作业
     */
    List<Homework> selectByPage(@Param("homework") Homework homework,
                                @Param("offset") Integer offset,
                                @Param("pageSize") Integer pageSize);

    /**
     * 根据状态批量更新作业
     */
    int batchUpdateStatus(@Param("ids") List<Integer> ids,
                          @Param("status") String status);
}