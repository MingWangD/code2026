<template>
  <div class="card">
    <h3>提交作业</h3>

    <div v-if="!studentId" class="warn">
      未检测到登录学生信息，请重新登录
    </div>

    <div v-else>
      <div class="bar">
        <div>
          学生：<strong>{{ studentName }}</strong>（ID: {{ studentId }}）
        </div>
        <div class="meta">课程ID：{{ courseId }}</div>
      </div>

      <button class="btn" @click="loadHomework">
        加载作业列表
      </button>

      <div v-if="loading" class="tip">正在加载作业...</div>

      <div v-if="homeworks.length === 0 && !loading" class="tip">
        当前课程暂无作业
      </div>

      <div
          v-for="hw in homeworks"
          :key="hw.id"
          class="homework"
      >
        <div class="left">
          <div class="title">{{ hw.title || "未命名作业" }}</div>
          <div class="desc">{{ hw.description || "暂无描述" }}</div>
        </div>

        <div class="right">
          <button
              class="btn submit"
              @click="submitHomework(hw.id)"
          >
            提交作业
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

const BASE_URL = "http://localhost:9090";
const USER_KEY = "system-user";

// 演示用：课程 ID
// 后续你可以从“课程列表 → 点击课程”带进来
const courseId = ref(1);

let studentId = null;
let studentName = "";

try {
  const u = JSON.parse(localStorage.getItem(USER_KEY) || "{}");
  studentId = u?.id ?? null;
  studentName = u?.name ?? "";
} catch {
  studentId = null;
}

const homeworks = ref([]);
const loading = ref(false);

// 加载作业列表
async function loadHomework() {
  loading.value = true;
  try {
    const res = await fetch(
        `${BASE_URL}/homework/selectByCourse/${courseId.value}`
    );
    const json = await res.json();
    homeworks.value = json?.data || [];
  } catch (e) {
    console.error("加载作业失败", e);
  } finally {
    loading.value = false;
  }
}

// 提交作业
async function submitHomework(homeworkId) {
  if (!studentId) return;

  try {
    // 1) 先查已提交次数
    const countRes = await fetch(
        `${BASE_URL}/behavior/event/homeworkSubmitCount?studentId=${studentId}&courseId=${courseId.value}&homeworkId=${homeworkId}`
    );
    const countJson = await countRes.json();
    const submittedCount = Number(countJson?.data || 0);

    // 2) 本次 attemptNo = 已提交次数 + 1
    const payload = {
      studentId: studentId,
      courseId: courseId.value,
      homeworkId: homeworkId,
      score: null,
      submitTime: null,
      attemptNo: submittedCount + 1
    };

    // 3) 提交作业
    const res = await fetch(`${BASE_URL}/homework/submit`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    const json = await res.json();

    if (json.code === "200") {
      alert(`作业提交成功！本次为第 ${submittedCount + 1} 次提交`);
    } else {
      alert("提交失败：" + json.msg);
    }
  } catch (e) {
    console.error("提交作业失败", e);
    alert("提交失败，请查看控制台");
  }
}
</script>

<style scoped>
.card {
  background: #fff;
  border-radius: 14px;
  padding: 18px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.06);
}

.warn {
  color: #c00;
  font-weight: 700;
}

.bar {
  display: flex;
  justify-content: space-between;
  margin: 10px 0 14px;
}

.meta {
  font-size: 13px;
  color: #666;
}

.btn {
  padding: 6px 12px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  background: #2563eb;
  color: #fff;
  font-size: 13px;
}

.btn.submit {
  background: #059669;
}

.tip {
  margin-top: 10px;
  font-size: 13px;
  color: #555;
}

.homework {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  margin-top: 10px;
  border-radius: 10px;
  background: #f9fafb;
}

.title {
  font-weight: 700;
}

.desc {
  font-size: 13px;
  color: #666;
  margin-top: 4px;
}
</style>
