package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.entity.Teacher;
import com.example.exception.CustomException;
import com.example.mapper.TeacherMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import com.example.entity.Account;

@Service
public class TeacherService {

    @Resource
    private TeacherMapper teacherMapper;

    /**
     * 新增教师
     */
    public void add(Teacher teacher) {
        // 检查用户名是否存在
        Teacher dbTeacher = teacherMapper.selectByUsername(teacher.getUsername());
        if (ObjectUtil.isNotNull(dbTeacher)) {
            throw new CustomException("用户名已存在");
        }

        // 检查工号是否存在
        if (ObjectUtil.isNotEmpty(teacher.getTeacherNo())) {
            Teacher existTeacher = teacherMapper.selectByTeacherNo(teacher.getTeacherNo());
            if (ObjectUtil.isNotNull(existTeacher)) {
                throw new CustomException("工号已存在");
            }
        }

        // 设置默认值
        if (ObjectUtil.isEmpty(teacher.getPassword())) {
            teacher.setPassword("123456"); // 默认密码
        }
        if (ObjectUtil.isEmpty(teacher.getName())) {
            teacher.setName(teacher.getUsername());
        }

        teacher.setRole("TEACHER");
        teacher.setStatus("正常");
        teacher.setCourseCount(0);
        teacher.setCreateTime(LocalDateTime.now().toString());
        teacher.setUpdateTime(LocalDateTime.now().toString());

        teacherMapper.insert(teacher);
    }

    /**
     * 删除教师
     */
    public void deleteById(Integer id) {
        Teacher teacher = teacherMapper.selectById(id);
        if (ObjectUtil.isNull(teacher)) {
            throw new CustomException("教师不存在");
        }
        teacherMapper.deleteById(id);
    }

    /**
     * 修改教师
     */
    public void updateById(Teacher teacher) {
        Teacher dbTeacher = teacherMapper.selectById(teacher.getId());
        if (ObjectUtil.isNull(dbTeacher)) {
            throw new CustomException("教师不存在");
        }

        // 检查用户名是否重复
        if (ObjectUtil.isNotEmpty(teacher.getUsername()) &&
                !dbTeacher.getUsername().equals(teacher.getUsername())) {
            Teacher existTeacher = teacherMapper.selectByUsername(teacher.getUsername());
            if (ObjectUtil.isNotNull(existTeacher) && !existTeacher.getId().equals(teacher.getId())) {
                throw new CustomException("用户名已存在");
            }
        }

        // 检查工号是否重复
        if (ObjectUtil.isNotEmpty(teacher.getTeacherNo()) &&
                !dbTeacher.getTeacherNo().equals(teacher.getTeacherNo())) {
            Teacher existTeacher = teacherMapper.selectByTeacherNo(teacher.getTeacherNo());
            if (ObjectUtil.isNotNull(existTeacher) && !existTeacher.getId().equals(teacher.getId())) {
                throw new CustomException("工号已存在");
            }
        }

        teacher.setRole("TEACHER");
        teacher.setUpdateTime(LocalDateTime.now().toString());
        teacherMapper.updateById(teacher);
    }

    /**
     * 根据ID查询教师
     */
    public Teacher selectById(Integer id) {
        Teacher teacher = teacherMapper.selectById(id);
        if (ObjectUtil.isNull(teacher)) {
            throw new CustomException("教师不存在");
        }
        return teacher;
    }

    /**
     * 根据工号查询教师
     */
    public Teacher selectByTeacherNo(String teacherNo) {
        Teacher teacher = teacherMapper.selectByTeacherNo(teacherNo);
        if (ObjectUtil.isNull(teacher)) {
            throw new CustomException("教师不存在");
        }
        return teacher;
    }

    /**
     * 查询所有教师
     */
    public List<Teacher> selectAll(Teacher teacher) {
        return teacherMapper.selectAll(teacher);
    }

    /**
     * 分页查询教师
     */
    public PageInfo<Teacher> selectPage(Teacher teacher, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Teacher> list = teacherMapper.selectAll(teacher);
        return PageInfo.of(list);
    }

    /**
     * 教师登录
     */
    public Teacher login(Teacher teacher) {
        if (ObjectUtil.isEmpty(teacher.getUsername()) ||
                ObjectUtil.isEmpty(teacher.getPassword())) {
            throw new CustomException("用户名或密码不能为空");
        }

        Teacher dbTeacher = teacherMapper.selectByCondition(
                teacher.getUsername(),
                teacher.getPassword(),
                "正常"
        );

        if (ObjectUtil.isNull(dbTeacher)) {
            throw new CustomException("用户名或密码错误");
        }

        // 清除密码再返回
        dbTeacher.setPassword(null);
        return dbTeacher;
    }

    /**
     * 教师登录（Account参数版本）
     */
    public Teacher login(com.example.entity.Account account) {
        Teacher teacher = new Teacher();
        teacher.setUsername(account.getUsername());
        teacher.setPassword(account.getPassword());
        return login(teacher);
    }

    /**
     * 修改密码
     */
    public void updatePassword(Teacher teacher) {
        if (ObjectUtil.isEmpty(teacher.getUsername()) ||
                ObjectUtil.isEmpty(teacher.getPassword()) ||
                ObjectUtil.isEmpty(teacher.getNewPassword())) {
            throw new CustomException("参数不完整");
        }

        Teacher dbTeacher = teacherMapper.selectByCondition(
                teacher.getUsername(),
                teacher.getPassword(),
                "正常"
        );

        if (ObjectUtil.isNull(dbTeacher)) {
            throw new CustomException("原密码错误");
        }

        dbTeacher.setPassword(teacher.getNewPassword());
        dbTeacher.setUpdateTime(LocalDateTime.now().toString());
        teacherMapper.updateById(dbTeacher);
    }

    /**
     * 修改密码（Account参数版本）
     */
    public void updatePassword(com.example.entity.Account account) {
        Teacher teacher = new Teacher();
        teacher.setUsername(account.getUsername());
        teacher.setPassword(account.getPassword());
        teacher.setNewPassword(account.getNewPassword());
        updatePassword(teacher);
    }

    /**
     * 根据院系查询教师
     */
    public List<Teacher> selectByDepartment(String department) {
        return teacherMapper.selectByDepartment(department);
    }

    /**
     * 更新教师课程数量
     */
    public void updateCourseCount(Integer id, Integer courseCount) {
        Teacher teacher = teacherMapper.selectById(id);
        if (ObjectUtil.isNull(teacher)) {
            throw new CustomException("教师不存在");
        }
        teacherMapper.updateCourseCount(id, courseCount);
    }

    /**
     * 批量导入教师
     */
    public void batchImport(List<Teacher> teachers) {
        if (ObjectUtil.isEmpty(teachers)) {
            throw new CustomException("导入数据为空");
        }

        // 检查工号是否重复
        for (Teacher teacher : teachers) {
            if (ObjectUtil.isNotEmpty(teacher.getTeacherNo())) {
                Teacher existTeacher = teacherMapper.selectByTeacherNo(teacher.getTeacherNo());
                if (ObjectUtil.isNotNull(existTeacher)) {
                    throw new CustomException("工号 " + teacher.getTeacherNo() + " 已存在");
                }
            }

            // 设置默认值
            if (ObjectUtil.isEmpty(teacher.getPassword())) {
                teacher.setPassword("123456");
            }
            if (ObjectUtil.isEmpty(teacher.getName())) {
                teacher.setName(teacher.getTeacherNo());
            }
            teacher.setUsername(teacher.getTeacherNo());
            teacher.setRole("TEACHER");
            teacher.setCourseCount(0);
            teacher.setCreateTime(LocalDateTime.now().toString());
            teacher.setUpdateTime(LocalDateTime.now().toString());
            teacher.setStatus("正常");
        }

        teacherMapper.batchInsert(teachers);
    }

    /**
     * 统计教师数量
     */
    public Integer count(String department, String title) {
        return teacherMapper.count(department, title);
    }

    /**
     * 搜索教师
     */
    public List<Teacher> search(String keyword) {
        Teacher query = new Teacher();
        query.setName(keyword);
        query.setTeacherNo(keyword);
        query.setDepartment(keyword);
        query.setTitle(keyword);
        query.setResearchArea(keyword);
        return teacherMapper.selectAll(query);
    }

    /**
     * 启用/禁用教师
     */
    public void toggleStatus(Integer id) {
        Teacher teacher = teacherMapper.selectById(id);
        if (ObjectUtil.isNull(teacher)) {
            throw new CustomException("教师不存在");
        }

        String newStatus = "正常".equals(teacher.getStatus()) ? "禁用" : "正常";
        teacher.setStatus(newStatus);
        teacher.setUpdateTime(LocalDateTime.now().toString());
        teacherMapper.updateById(teacher);
    }

    /**
     * 重置密码
     */
    public void resetPassword(Integer id) {
        Teacher teacher = teacherMapper.selectById(id);
        if (ObjectUtil.isNull(teacher)) {
            throw new CustomException("教师不存在");
        }

        teacher.setPassword("123456");
        teacher.setUpdateTime(LocalDateTime.now().toString());
        teacherMapper.updateById(teacher);
    }

    /**
     * 获取所有教师（简单列表）
     */
    public List<Teacher> getAllTeachers() {
        Teacher query = new Teacher();
        query.setStatus("正常");
        return teacherMapper.selectAll(query);
    }
}