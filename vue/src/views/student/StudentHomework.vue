<template>
  <div class="card">
    <h3>提交作业</h3>

    <div v-if="!resolvedStudentId" class="warn">未检测到登录学生，请先登录。</div>

    <div v-else>
      <div class="bar">
        <div>学生：<strong>{{ resolvedStudentName || '未知' }}</strong>（ID: {{ resolvedStudentId }}）</div>
        <div class="meta">课程ID：{{ courseId }}</div>
      </div>

      <button class="btn" @click="loadHomework" :disabled="loading || !courseId">
        {{ loading ? '加载中...' : '加载作业' }}
      </button>

      <div v-if="message" class="tip success">{{ message }}</div>

      <div v-if="!courseId" class="tip">请先在上方选择课程。</div>
      <div v-else-if="homeworks.length === 0 && !loading" class="tip">该课程暂无作业。</div>

      <div class="homework" v-for="hw in homeworks" :key="hw.id">
        <div class="left">
          <div class="title">{{ hw.title || hw.homeworkTitle || `作业 #${hw.id}` }}</div>
          <div class="desc">{{ hw.description || hw.content || '暂无说明' }}</div>
          <div class="meta">已提交 {{ submitCountMap[hw.id] || 0 }} 次</div>
        </div>
        <div class="right">
          <button
              class="btn submit"
              :disabled="!resolvedStudentId || !courseId || submitLoadingMap[hw.id]"
              @click="submitHomework(hw.id)"
          >
            {{ submitLoadingMap[hw.id] ? '提交中...' : '提交作业' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue";

const props = defineProps({
  courseId: { type: Number, default: null },
  studentId: { type: Number, default: null },
  studentName: { type: String, default: "" },
  apiBase: { type: String, default: "" }
});

const USER_KEY = "system-user";
const localUser = JSON.parse(localStorage.getItem(USER_KEY) || "{}");

const resolvedStudentId = computed(() => props.studentId ?? localUser?.id ?? null);
const resolvedStudentName = computed(() => props.studentName || localUser?.name || "");
const loading = ref(false);
const homeworks = ref([]);
const message = ref("");
const submitCountMap = ref({});
const submitLoadingMap = ref({});

const getBase = () => (props.apiBase || (import.meta.env.VITE_BASE_URL || "")).replace(/\/$/, "");

// 加载作业列表
async function loadHomework() {
  if (!props.courseId) {
    homeworks.value = [];
    submitCountMap.value = {};
    message.value = "";
    return;
  }
  loading.value = true;
  try {
    const res = await fetch(`${getBase()}/homework/selectByCourse/${props.courseId}`);
    const json = await res.json();
    homeworks.value = json?.data || [];
    await loadSubmitCounts();
  } catch (e) {
    console.error("加载作业失败", e);
  } finally {
    loading.value = false;
  }
}

async function loadSubmitCounts() {
  if (!resolvedStudentId.value || !props.courseId || homeworks.value.length === 0) {
    submitCountMap.value = {};
    return;
  }

  const entries = await Promise.all(
    homeworks.value.map(async (hw) => {
      try {
        const countRes = await fetch(
          `${getBase()}/behavior/event/homeworkSubmitCount?studentId=${resolvedStudentId.value}&courseId=${props.courseId}&homeworkId=${hw.id}`
        );
        const countJson = await countRes.json();
        return [hw.id, Number(countJson?.data || 0)];
      } catch {
        return [hw.id, 0];
      }
    })
  );

  submitCountMap.value = Object.fromEntries(entries);
}

// 提交作业
async function submitHomework(homeworkId) {
  if (!resolvedStudentId.value || !props.courseId) return;

  submitLoadingMap.value = { ...submitLoadingMap.value, [homeworkId]: true };
  message.value = "";

  try {
    const submittedCount = Number(submitCountMap.value?.[homeworkId] || 0);

    // 2) 本次 attemptNo = 已提交次数 + 1
    const payload = {
      studentId: resolvedStudentId.value,
      courseId: props.courseId,
      homeworkId: homeworkId,
      score: null,
      submitTime: null,
      attemptNo: submittedCount + 1
    };

    // 3) 提交作业
    const res = await fetch(`${getBase()}/homework/submit`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    const json = await res.json();

    if (json.code === "200") {
      const nextCount = submittedCount + 1;
      submitCountMap.value = { ...submitCountMap.value, [homeworkId]: nextCount };
      message.value = `作业提交成功！作业 #${homeworkId} 已提交 ${nextCount} 次。`;
    } else {
      alert("提交失败：" + json.msg);
    }
  } catch (e) {
    console.error("提交作业失败", e);
    alert("提交失败，请查看控制台");
  } finally {
    submitLoadingMap.value = { ...submitLoadingMap.value, [homeworkId]: false };
  }
}

watch(() => props.courseId, () => {
  loadHomework();
}, { immediate: true });

watch(() => props.studentId, () => {
  loadSubmitCounts();
});
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

.tip.success {
  color: #065f46;
  font-weight: 600;
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
