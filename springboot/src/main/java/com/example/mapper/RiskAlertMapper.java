package com.example.mapper;

import com.example.entity.RiskAlert;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 风险预警数据访问接口
 */
public interface RiskAlertMapper {

    /**
     * 新增预警记录
     */
    int insert(RiskAlert alert);

    /**
     * 删除预警记录
     */
    int deleteById(Integer id);

    /**
     * 修改预警记录
     */
    int updateById(RiskAlert alert);

    /**
     * 根据ID查询预警记录
     */
    RiskAlert selectById(Integer id);

    /**
     * 查询所有预警记录
     */
    List<RiskAlert> selectAll(RiskAlert alert);

    /**
     * 根据预警编号查询
     */
    RiskAlert selectByAlertNo(String alertNo);

    /**
     * 根据学生ID查询预警记录
     */
    List<RiskAlert> selectByStudentId(Integer studentId);

    /**
     * 根据课程ID查询预警记录
     */
    List<RiskAlert> selectByCourseId(Integer courseId);

    /**
     * 根据预警等级查询
     */
    List<RiskAlert> selectByAlertLevel(String alertLevel);

    /**
     * 根据状态查询预警记录
     */
    List<RiskAlert> selectByStatus(String status);

    /**
     * 获取未读预警数量
     */
    int countUnread(@Param("handlerId") Integer handlerId,
                    @Param("handlerRole") String handlerRole);

    /**
     * 统计预警数量
     */
    int count(@Param("courseId") Integer courseId,
              @Param("alertLevel") String alertLevel,
              @Param("status") String status,
              @Param("startDate") String startDate,
              @Param("endDate") String endDate);

    /**
     * 更新预警状态
     */
    int updateStatus(@Param("id") Integer id,
                     @Param("status") String status,
                     @Param("handlerId") Integer handlerId,
                     @Param("handlerName") String handlerName,
                     @Param("handlerRole") String handlerRole);

    /**
     * 批量更新预警状态
     */
    int batchUpdateStatus(@Param("ids") List<Integer> ids,
                          @Param("status") String status);

    /**
     * 获取最新的预警记录
     */
    List<RiskAlert> selectLatestAlerts(@Param("limit") Integer limit);

    /**
     * 搜索预警记录
     */
    List<RiskAlert> search(@Param("keyword") String keyword);

    /**
     * 获取每日预警统计
     */
    List<Map<String, Object>> selectDailyStatistics(@Param("days") Integer days);
    /**
     * 根据处理人查询预警记录
     */
    List<RiskAlert> selectByHandler(@Param("handlerId") Integer handlerId,
                                    @Param("handlerRole") String handlerRole);
}