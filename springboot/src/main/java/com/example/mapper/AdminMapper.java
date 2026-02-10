package com.example.mapper;

import com.example.entity.Admin;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;



/**
 * 操作admin相关数据接口
 */
public interface AdminMapper {



    /**
     * 新增
     */
    int insert(Admin admin);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Admin admin);

    /**
     * 根据ID查询
     */
    Admin selectById(Integer id);

    /**
     * 查询所有
     */
    List<Admin> selectAll(Admin admin);

    /**
     * 根据用户名查询
     */
    //@Select("select * from `admin` where username = #{username}")
    Admin selectByUsername(String username);

    /**
     * 统计数量
     */
    int count(@Param("status") String status, @Param("department") String department);

    /**
     * 更新登录信息
     */
    int updateLoginInfo(@Param("id") Integer id,
                        @Param("lastLoginTime") String lastLoginTime,
                        @Param("updateTime") String updateTime);

    /**
     * 根据条件查询（用于登录验证）
     */
    Admin selectByCondition(@Param("username") String username,
                            @Param("password") String password,
                            @Param("status") String status,
                            @Param("role") String role);


}