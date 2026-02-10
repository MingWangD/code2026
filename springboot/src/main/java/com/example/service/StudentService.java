package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.entity.Student;
import com.example.exception.CustomException;
import com.example.mapper.StudentMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import com.example.entity.Account;

@Service
public class StudentService {

    @Resource
    private StudentMapper studentMapper;

    /**
     * 新增学生
     */
    public void add(Student student) {
        // 检查用户名是否存在
        Student dbStudent = studentMapper.selectByUsername(student.getUsername());
        if (ObjectUtil.isNotNull(dbStudent)) {
            throw new CustomException("用户名已存在");
        }

        // 检查学号是否存在
        if (ObjectUtil.isNotEmpty(student.getStudentNo())) {
            Student existStudent = studentMapper.selectByStudentNo(student.getStudentNo());
            if (ObjectUtil.isNotNull(existStudent)) {
                throw new CustomException("学号已存在");
            }
        }

        // 设置默认值
        if (ObjectUtil.isEmpty(student.getPassword())) {
            student.setPassword("123456"); // 默认密码
        }
        if (ObjectUtil.isEmpty(student.getName())) {
            student.setName(student.getUsername());
        }

        student.setRole("STUDENT");
        student.setStatus("正常");
        student.setAcademicStatus("正常");
        student.setWarningCount(0);
        student.setCreateTime(LocalDateTime.now().toString());
        student.setUpdateTime(LocalDateTime.now().toString());

        studentMapper.insert(student);
    }

    /**
     * 删除学生
     */
    public void deleteById(Integer id) {
        Student student = studentMapper.selectById(id);
        if (ObjectUtil.isNull(student)) {
            throw new CustomException("学生不存在");
        }
        studentMapper.deleteById(id);
    }

    /**
     * 修改学生
     */
    public void updateById(Student student) {
        Student dbStudent = studentMapper.selectById(student.getId());
        if (ObjectUtil.isNull(dbStudent)) {
            throw new CustomException("学生不存在");
        }

        // 检查用户名是否重复
        if (ObjectUtil.isNotEmpty(student.getUsername()) &&
                !dbStudent.getUsername().equals(student.getUsername())) {
            Student existStudent = studentMapper.selectByUsername(student.getUsername());
            if (ObjectUtil.isNotNull(existStudent) && !existStudent.getId().equals(student.getId())) {
                throw new CustomException("用户名已存在");
            }
        }

        // 检查学号是否重复
        if (ObjectUtil.isNotEmpty(student.getStudentNo()) &&
                !dbStudent.getStudentNo().equals(student.getStudentNo())) {
            Student existStudent = studentMapper.selectByStudentNo(student.getStudentNo());
            if (ObjectUtil.isNotNull(existStudent) && !existStudent.getId().equals(student.getId())) {
                throw new CustomException("学号已存在");
            }
        }

        student.setRole("STUDENT");
        student.setUpdateTime(LocalDateTime.now().toString());
        studentMapper.updateById(student);
    }

    /**
     * 根据ID查询学生
     */
    public Student selectById(Integer id) {
        Student student = studentMapper.selectById(id);
        if (ObjectUtil.isNull(student)) {
            throw new CustomException("学生不存在");
        }
        return student;
    }

    /**
     * 根据学号查询学生
     */
    public Student selectByStudentNo(String studentNo) {
        Student student = studentMapper.selectByStudentNo(studentNo);
        if (ObjectUtil.isNull(student)) {
            throw new CustomException("学生不存在");
        }
        return student;
    }

    /**
     * 查询所有学生
     */
    public List<Student> selectAll(Student student) {
        return studentMapper.selectAll(student);
    }

    /**
     * 分页查询学生
     */
    public PageInfo<Student> selectPage(Student student, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Student> list = studentMapper.selectAll(student);
        return PageInfo.of(list);
    }

    /**
     * 学生登录
     */
    public Student login(Student student) {
        if (ObjectUtil.isEmpty(student.getUsername()) ||
                ObjectUtil.isEmpty(student.getPassword())) {
            throw new CustomException("用户名或密码不能为空");
        }

        Student dbStudent = studentMapper.selectByCondition(
                student.getUsername(),
                student.getPassword(),
                "正常"
        );

        if (ObjectUtil.isNull(dbStudent)) {
            throw new CustomException("用户名或密码错误");
        }

        // 清除密码再返回
        dbStudent.setPassword(null);
        return dbStudent;
    }

    /**
     * 学生登录（Account参数版本）
     */
    public Student login(com.example.entity.Account account) {
        Student student = new Student();
        student.setUsername(account.getUsername());
        student.setPassword(account.getPassword());
        return login(student);
    }

    /**
     * 修改密码
     */
    public void updatePassword(Student student) {
        if (ObjectUtil.isEmpty(student.getUsername()) ||
                ObjectUtil.isEmpty(student.getPassword()) ||
                ObjectUtil.isEmpty(student.getNewPassword())) {
            throw new CustomException("参数不完整");
        }

        Student dbStudent = studentMapper.selectByCondition(
                student.getUsername(),
                student.getPassword(),
                "正常"
        );

        if (ObjectUtil.isNull(dbStudent)) {
            throw new CustomException("原密码错误");
        }

        dbStudent.setPassword(student.getNewPassword());
        dbStudent.setUpdateTime(LocalDateTime.now().toString());
        studentMapper.updateById(dbStudent);
    }

    /**
     * 修改密码（Account参数版本）
     */
    public void updatePassword(com.example.entity.Account account) {
        Student student = new Student();
        student.setUsername(account.getUsername());
        student.setPassword(account.getPassword());
        student.setNewPassword(account.getNewPassword());
        updatePassword(student);
    }

    /**
     * 批量导入学生
     */
    public void batchImport(List<Student> students) {
        if (ObjectUtil.isEmpty(students)) {
            throw new CustomException("导入数据为空");
        }

        // 检查学号是否重复
        for (Student student : students) {
            if (ObjectUtil.isNotEmpty(student.getStudentNo())) {
                Student existStudent = studentMapper.selectByStudentNo(student.getStudentNo());
                if (ObjectUtil.isNotNull(existStudent)) {
                    throw new CustomException("学号 " + student.getStudentNo() + " 已存在");
                }
            }

            // 设置默认值
            if (ObjectUtil.isEmpty(student.getPassword())) {
                student.setPassword("123456");
            }
            if (ObjectUtil.isEmpty(student.getName())) {
                student.setName(student.getStudentNo());
            }
            student.setUsername(student.getStudentNo());
            student.setRole("STUDENT");
            student.setAcademicStatus("正常");
            student.setWarningCount(0);
            student.setCreateTime(LocalDateTime.now().toString());
            student.setUpdateTime(LocalDateTime.now().toString());
            student.setStatus("正常");
        }

        studentMapper.batchInsert(students);
    }

    /**
     * 根据班级查询学生
     */
    public List<Student> selectByClassId(Integer classId) {
        return studentMapper.selectByClassId(classId);
    }

    /**
     * 更新学业状态
     */
    public void updateAcademicStatus(Integer id, String academicStatus) {
        Student student = studentMapper.selectById(id);
        if (ObjectUtil.isNull(student)) {
            throw new CustomException("学生不存在");
        }

        // 如果是预警状态，增加预警次数
        Integer warningCount = student.getWarningCount();
        if ("预警".equals(academicStatus)) {
            warningCount = warningCount + 1;
        }

        studentMapper.updateAcademicStatus(id, academicStatus, warningCount);
    }

    /**
     * 统计学生数量
     */
    public Integer count(String grade, String major, String academicStatus) {
        return studentMapper.count(grade, major, academicStatus);
    }

    /**
     * 搜索学生
     */
    public List<Student> search(String keyword) {
        Student query = new Student();
        query.setName(keyword);
        query.setStudentNo(keyword);
        query.setMajor(keyword);
        return studentMapper.selectAll(query);
    }

    /**
     * 启用/禁用学生
     */
    public void toggleStatus(Integer id) {
        Student student = studentMapper.selectById(id);
        if (ObjectUtil.isNull(student)) {
            throw new CustomException("学生不存在");
        }

        String newStatus = "正常".equals(student.getStatus()) ? "禁用" : "正常";
        student.setStatus(newStatus);
        student.setUpdateTime(LocalDateTime.now().toString());
        studentMapper.updateById(student);
    }

    /**
     * 重置密码
     */
    public void resetPassword(Integer id) {
        Student student = studentMapper.selectById(id);
        if (ObjectUtil.isNull(student)) {
            throw new CustomException("学生不存在");
        }

        student.setPassword("123456");
        student.setUpdateTime(LocalDateTime.now().toString());
        studentMapper.updateById(student);
    }
}