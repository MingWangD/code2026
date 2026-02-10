<template>
  <div class="card">
    <h3>考试</h3>

    <div class="row">
      <button class="btn" @click="load">加载可考试卷</button>
    </div>

    <div v-if="list.length===0" class="tip">暂无考试</div>

    <div v-for="e in list" :key="e.id" class="exam">
      <div class="left">
        <div class="title">{{ e.exam_name }}</div>
        <div class="meta">满分 {{ e.total_score }}</div>
      </div>
      <button class="btn" @click="open(e.id)">进入</button>
    </div>

    <div v-if="paper" class="paper">
      <h4>{{ paper.exam.exam_name }}</h4>
      <div v-for="q in paper.questions" :key="q.question_id" class="q">
        <div class="qt">{{ q.content }}</div>
        <label v-for="op in ['A','B','C','D']" :key="op">
          <input type="radio" :name="'q'+q.question_id" :value="op"
                 v-model="answers[q.question_id]" />
          {{ q['option_'+op] }}
        </label>
      </div>
      <button class="btn submit" @click="submit">提交</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";


const BASE="http://localhost:9090";
const USER_KEY="system-user";
const user = JSON.parse(localStorage.getItem(USER_KEY)||"{}");
const studentId=user.id;
const courseId=1;

const list=ref([]);
const paper=ref(null);
const answers=ref({});

async function load(){
  const r=await fetch(`${BASE}/exam/available?courseId=${courseId}`);
  const j=await r.json();
  list.value=j.data||[];
}
async function open(id){
  const r=await fetch(`${BASE}/exam/paper/${id}`);
  const j=await r.json();
  paper.value=j.data;
  answers.value={};
}
async function submit(){
  const r=await fetch(`${BASE}/exam/submit`,{
    method:"POST",
    headers:{'Content-Type':'application/json'},
    body:JSON.stringify({studentId,courseId,examId:paper.value.exam.id,answers:answers.value})
  });
  const j=await r.json();
  alert(`第 ${j.data.attemptNo} 次考试，得分 ${j.data.score}`);
}

onMounted(load);

</script>

<style scoped>
.card{background:#fff;border-radius:14px;padding:18px}
.exam{display:flex;justify-content:space-between;margin:8px 0}
.q{margin:10px 0}
.btn{padding:6px 12px;border-radius:8px;background:#2563eb;color:#fff;border:0}
.submit{background:#059669}
.tip{color:#666}
</style>
