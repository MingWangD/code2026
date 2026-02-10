package com.example.mapper;

import com.example.entity.ModelVersion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模型版本数据访问接口
 */
public interface ModelVersionMapper {

    /**
     * 新增模型版本
     */
    int insert(ModelVersion modelVersion);

    /**
     * 删除模型版本
     */
    int deleteById(Integer id);

    /**
     * 修改模型版本
     */
    int updateById(ModelVersion modelVersion);

    /**
     * 根据ID查询模型版本
     */
    ModelVersion selectById(Integer id);

    /**
     * 查询所有模型版本
     */
    List<ModelVersion> selectAll(ModelVersion modelVersion);

    /**
     * 根据模型编号查询
     */
    ModelVersion selectByModelNo(String modelNo);

    /**
     * 获取当前激活的模型
     */
    ModelVersion selectActiveModel();

    /**
     * 获取最新的模型版本
     */
    ModelVersion selectLatestVersion(@Param("modelNo") String modelNo);

    /**
     * 统计模型版本数量
     */
    int count(@Param("status") String status,
              @Param("isActive") Boolean isActive);

    /**
     * 更新模型激活状态
     */
    int updateActiveStatus(@Param("id") Integer id,
                           @Param("isActive") Boolean isActive);

    /**
     * 取消所有模型的激活状态
     */
    int deactivateAllModels();

    /**
     * 更新模型性能指标
     */
    int updatePerformanceMetrics(@Param("id") Integer id,
                                 @Param("accuracy") Double accuracy,
                                 @Param("precision") Double precision,
                                 @Param("recall") Double recall,
                                 @Param("f1Score") Double f1Score,
                                 @Param("auc") Double auc);

    /**
     * 更新模型文件路径
     */
    int updateModelFilePath(@Param("id") Integer id,
                            @Param("modelFilePath") String modelFilePath,
                            @Param("featureScalerPath") String featureScalerPath);

    /**
     * 获取模型训练历史
     */
    List<ModelVersion> selectTrainingHistory(@Param("limit") Integer limit);
}