package com.example.entity;

/**
 * 管理员
 */
public class Admin extends Account {

    /** 管理员编号 */
    private String adminNo;
    /** 部门 */
    private String department;
    /** 联系电话 */
    private String phone;
    /** 权限级别：超级管理员/普通管理员 */
    private String permissionLevel;
    /** 最后登录时间 */
    private String lastLoginTime;
    /** 登录次数 */
    private Integer loginCount;

    // 构造方法（关键：不要给 permissionLevel 默认值，否则查询会被默认过滤）
    public Admin() {
        super();
        // 可以保留 role 默认 ADMIN（不影响你当前 selectAll，因为 XML 里没用 role 做 where）
        this.setRole("ADMIN");

        // 关键修复：不要默认“普通管理员”
        this.permissionLevel = null;

        // loginCount 默认也别强塞，避免将来你 XML 加条件时又被默认值误伤
        this.loginCount = null;
    }

    public String getAdminNo() {
        return adminNo;
    }

    public void setAdminNo(String adminNo) {
        this.adminNo = adminNo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", name='" + getName() + '\'' +
                ", role='" + getRole() + '\'' +
                ", adminNo='" + adminNo + '\'' +
                ", department='" + department + '\'' +
                ", permissionLevel='" + permissionLevel + '\'' +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
