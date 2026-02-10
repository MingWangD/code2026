package com.example.mapper;

import com.example.entity.LearningFeatures;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学习行为特征数据访问接口
 */
public interface LearningFeaturesMapper {

    /**
     * 新增特征记录
     */
    int insert(LearningFeatures features);

    /**
     * 删除特征记录
     */
    int deleteById(Integer id);

    /**
     * 修改特征记录
     */
    int updateById(LearningFeatures features);

    int insertOrUpdateBatch(@org.apache.ibatis.annotations.Param("list") java.util.List<com.example.entity.LearningFeatures> list);

    /**
     * 仅更新“作业相关”特征（用于作业提交事件后快速刷新当天特征）
     */
    int updateHomeworkPart(@Param("id") Integer id,
                           @Param("homeworkSubmitRate") Double homeworkSubmitRate,
                           @Param("homeworkAvgScore") Double homeworkAvgScore,
                           @Param("homeworkDelayCount") Integer homeworkDelayCount,
                           @Param("homeworkQualityScore") Double homeworkQualityScore,
                           @Param("updatedTime") String updatedTime);



    List<LearningFeatures> selectRecentFeatures(@Param("days") Integer days);
    /**
     * 根据ID查询特征记录
     */
    LearningFeatures selectById(Integer id);

    /**
     * 查询所有特征记录
     */
    List<LearningFeatures> selectAll(LearningFeatures features);

    /**
     * 根据学生ID查询特征记录
     */
    List<LearningFeatures> selectByStudentId(Integer studentId);

    /**
     * 根据课程ID查询特征记录
     */
    List<LearningFeatures> selectByCourseId(Integer courseId);

    /**
     * 根据学生和课程查询特征记录
     */
    List<LearningFeatures> selectByStudentAndCourse(@Param("studentId") Integer studentId,
                                                    @Param("courseId") Integer courseId);

    /**
     * 根据日期范围查询特征记录
     */
    List<LearningFeatures> selectByDateRange(@Param("studentId") Integer studentId,
                                             @Param("startDate") String startDate,
                                             @Param("endDate") String endDate);

    /**
     * 查询指定日期的特征记录
     */
    LearningFeatures selectByStudentCourseDate(@Param("studentId") Integer studentId,
                                               @Param("courseId") Integer courseId,
                                               @Param("featureDate") String featureDate);

    /**
     * 获取学生的学习特征汇总
     */
    LearningFeatures selectSummaryByStudent(@Param("studentId") Integer studentId,
                                            @Param("courseId") Integer courseId,
                                            @Param("startDate") String startDate,
                                            @Param("endDate") String endDate);

    /**
     * 统计特征记录数量
     */
    int count(@Param("studentId") Integer studentId,
              @Param("courseId") Integer courseId,
              @Param("riskLevel") String riskLevel);

    /**
     * 获取高风险学生特征
     */
    List<LearningFeatures> selectHighRiskFeatures(@Param("threshold") Double threshold,
                                                  @Param("limit") Integer limit);

    /**
     * 批量插入特征记录
     */
    int batchInsert(List<LearningFeatures> featuresList);

    /**
     * 更新风险信息
     */
    int updateRiskInfo(@Param("id") Integer id,
                       @Param("riskScore") Double riskScore,
                       @Param("riskLevel") String riskLevel,
                       @Param("riskProbability") Double riskProbability);

    /**
     * 获取模型训练数据
     */
    List<LearningFeatures> selectTrainingData(@Param("limit") Integer limit);

    /**
     * 删除指定日期之前的特征记录
     */
    int deleteBeforeDate(@Param("date") String date);

    /**
     * 从student_behavior数据计算特征
     * @param days 计算最近多少天的数据
     * @return 计算后的特征列表
     */
    List<LearningFeatures> calculateFeaturesFromBehavior(@Param("days") Integer days);

    /**
     * 删除指定学生、课程、日期的重复特征记录
     */
    int deleteByStudentCourseDate(@Param("studentId") Integer studentId,
                                  @Param("courseId") Integer courseId,
                                  @Param("featureDate") String featureDate);
}