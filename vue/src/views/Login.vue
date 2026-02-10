<template>
  <div class="login-container">
    <div class="login-box">
      <div class="title">
        学情智能预警系统
      </div>

      <el-form
          ref="formRef"
          :model="data.form"
          :rules="data.rules"
      >
        <el-form-item prop="username">
          <el-input
              v-model="data.form.username"
              size="large"
              placeholder="请输入账号"
              :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              v-model="data.form.password"
              size="large"
              type="password"
              show-password
              placeholder="请输入密码"
              :prefix-icon="Lock"
          />
        </el-form-item>

        <el-form-item prop="role">
          <el-select
              v-model="data.form.role"
              size="large"
              style="width: 100%"
          >
            <el-option label="管理员" value="ADMIN" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="学生" value="STUDENT" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              size="large"
              style="width: 100%"
              :loading="loading"
              @click="login"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import request from "../utils/request";
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { User, Lock } from "@element-plus/icons-vue";

const router = useRouter();
const formRef = ref();
const loading = ref(false);

const data = reactive({
  form: {
    username: "",
    password: "",
    role: "ADMIN",
  },
  rules: {
    username: [{ required: true, message: "请输入账号", trigger: "blur" }],
    password: [{ required: true, message: "请输入密码", trigger: "blur" }],
  },
});

const login = async () => {
  try {
    await formRef.value.validate();
  } catch {
    ElMessage.warning("请填写完整信息");
    return;
  }

  loading.value = true;
  try {
    const res = await request.post("/auth/login", data.form);

    if (res.code === 200 || res.code === "200") {
      localStorage.setItem("system-user", JSON.stringify(res.data));
      ElMessage.success("登录成功");

      // 根据角色跳转
      if (res.data.role === "ADMIN") {
        router.push("/manager/dashboard");
      } else if (res.data.role === "TEACHER") {
        router.push("/manager/home");
      } else if (res.data.role === "STUDENT") {
        router.push("/student");
      }
    } else {
      ElMessage.error(res.msg || "登录失败");
    }
  } catch (e) {
    ElMessage.error("登录请求失败，请检查后端服务");
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  font-size: 28px;
  font-weight: bold;
  color: #1967e3;
  margin-bottom: 30px;
}
</style>
