<template>
  <div class="card">
    <h3>考试</h3>

    <div v-if="!resolvedStudentId" class="tip">未检测到登录学生，请先登录。</div>

    <div class="row">
      <button class="btn" @click="load" :disabled="!courseId || loading">{{ loading ? '加载中...' : '加载可考试卷' }}</button>
      <span class="tip" v-if="!courseId">请先在上方选择课程。</span>
    </div>

    <div v-if="resultMessage" class="tip success">{{ resultMessage }}</div>

    <div v-if="courseId && list.length===0 && !loading" class="tip">暂无考试</div>

    <div v-for="e in list" :key="e.id" class="exam">
      <div class="left">
        <div class="title">{{ e.exam_name || e.examName || `试卷 #${e.id}` }}</div>
        <div class="meta">满分 {{ e.total_score || e.totalScore || '-' }}</div>
      </div>
      <button class="btn" @click="open(e.id)">进入</button>
    </div>

    <div v-if="paper" class="paper">
      <h4>{{ paper.exam.exam_name || paper.exam.examName }}</h4>
      <div v-for="q in paper.questions" :key="q.question_id" class="q">
        <div class="qt">{{ q.content }}</div>
        <label v-for="op in ['A','B','C','D']" :key="op">
          <input type="radio" :name="'q'+q.question_id" :value="op"
                 v-model="answers[q.question_id]" />
          {{ q['option_'+op] }}
        </label>
      </div>
      <button class="btn submit" @click="submit" :disabled="submitting">{{ submitting ? '提交中...' : '提交' }}</button>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue";

const props = defineProps({
  courseId: { type: Number, default: null },
  studentId: { type: Number, default: null },
  apiBase: { type: String, default: "" }
});

const USER_KEY="system-user";
const user = JSON.parse(localStorage.getItem(USER_KEY)||"{}");
const resolvedStudentId = computed(() => props.studentId ?? user.id ?? null);

const list=ref([]);
const paper=ref(null);
const answers=ref({});
const loading=ref(false);
const submitting=ref(false);
const resultMessage=ref("");

const getBase = () => (props.apiBase || (import.meta.env.VITE_BASE_URL || "")).replace(/\/$/, "");

async function load(){
  if (!props.courseId) {
    list.value = [];
    paper.value = null;
    resultMessage.value = "";
    return;
  }
  loading.value = true;
  try {
    const r=await fetch(`${getBase()}/exam/available?courseId=${props.courseId}`);
    const j=await r.json();
    list.value=j.data||[];
  } finally {
    loading.value = false;
  }
}
async function open(id){
  const r=await fetch(`${getBase()}/exam/paper/${id}`);
  const j=await r.json();
  paper.value=j.data;
  answers.value={};
}
async function submit(){
  if (!resolvedStudentId.value || !props.courseId || !paper.value?.exam?.id) {
    alert('参数不完整，请确认学生和课程已选择');
    return;
  }
  submitting.value = true;
  try {
    const r=await fetch(`${getBase()}/exam/submit`,{
      method:"POST",
      headers:{'Content-Type':'application/json'},
      body:JSON.stringify({studentId: resolvedStudentId.value,courseId:props.courseId,examId:paper.value.exam.id,answers:answers.value})
    });
    const j=await r.json();
    resultMessage.value = `考试提交成功：第 ${j?.data?.attemptNo ?? '-'} 次，得分 ${j?.data?.score ?? '-'}。`;
  } catch (e) {
    alert('提交失败，请稍后重试');
  } finally {
    submitting.value = false;
  }
}

watch(() => props.courseId, () => {
  load();
}, { immediate: true });

</script>

<style scoped>
.card{background:#fff;border-radius:14px;padding:18px}
.exam{display:flex;justify-content:space-between;margin:8px 0}
.q{margin:10px 0}
.btn{padding:6px 12px;border-radius:8px;background:#2563eb;color:#fff;border:0}
.submit{background:#059669}
.tip{color:#666}
.tip.success{color:#065f46;font-weight:600}
.row{display:flex;align-items:center;gap:10px;margin-bottom:10px}
</style>
