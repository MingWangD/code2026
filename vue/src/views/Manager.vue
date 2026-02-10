<template>
  <div class="manager-container">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="logo-section">
        <div class="logo-text">学情智能预警系统</div>
      </div>
      <div class="user-info">
        <img :src="user.avatar || defaultAvatar" class="avatar" alt="用户头像">
        <span class="username">{{ user.name || '管理员' }}</span>
      </div>
    </div>

    <!-- 主体区域 -->
    <div class="main-content">
      <!-- 侧边栏 -->
      <div class="sidebar">
        <el-menu
            router
            :default-active="activeMenu"
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#409EFF"
        >
          <el-menu-item index="/manager/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>预警看板</span>
          </el-menu-item>

          <el-menu-item index="/student">
            <el-icon><User /></el-icon>
            <span>学生端演示</span>
          </el-menu-item>

          <el-menu-item index="/manager/home">
            <el-icon><DataAnalysis /></el-icon>
            <span>统计分析</span>
          </el-menu-item>
          <el-sub-menu index="system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/manager/admin">
              <el-icon><User /></el-icon>
              <span>管理员信息</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item index="/logout" @click="logout">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 内容区域 -->
      <div class="content-area">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  HomeFilled,
  DataAnalysis,
  Setting,
  User,
  SwitchButton
} from '@element-plus/icons-vue'

export default {
  name: 'ManagerLayout',

  setup() {
    const router = useRouter()
    const route = useRoute()

    const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

    // 获取用户信息
    const user = computed(() => {
      try {
        const userData = localStorage.getItem('system-user')
        return userData ? JSON.parse(userData) : {}
      } catch (error) {
        console.error('解析用户信息失败:', error)
        return {}
      }
    })

    // 当前激活的菜单
    const activeMenu = computed(() => {
      return route.path
    })

    // 检查登录状态
    if (!user.value.id) {
      ElMessage.error('请先登录')
      router.push('/login')
    }

    // 退出登录
    const logout = () => {
      localStorage.removeItem('system-user')
      ElMessage.success('退出成功')
      router.push('/login')
    }

    return {
      user,
      defaultAvatar,
      activeMenu,
      logout
    }
  }
}
</script>

<style scoped>
.manager-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: 60px;
  background-color: #2e3143;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  border-bottom: 1px solid #ddd;
}

.logo-section {
  display: flex;
  align-items: center;
}

.logo-text {
  font-weight: bold;
  font-size: 24px;
  color: #fff;
  margin-left: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.username {
  color: #fff;
  font-size: 14px;
}

.main-content {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.sidebar {
  width: 200px;
  background-color: #304156;
  overflow-y: auto;
}

.content-area {
  flex: 1;
  padding: 20px;
  background-color: #f8f8ff;
  overflow-y: auto;
}

/* 菜单样式优化 */
.el-menu {
  border-right: none !important;
}

.el-menu-item {
  height: 56px;
  line-height: 56px;
}

.el-menu-item.is-active {
  background-color: #263445 !important;
}

.el-menu-item:hover {
  background-color: #263445 !important;
}
</style>