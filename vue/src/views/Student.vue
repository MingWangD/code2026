<template>
  <div class="layout">
    <!-- 左侧菜单 -->
    <aside class="sider">
      <div class="brand">
        <div class="t1">学生学习中心</div>
        <div class="t2">高效行为 → 可解释预警</div>
      </div>

      <nav class="menu">
        <div class="item" :class="{ active: tab==='profile' }" @click="tab='profile'">个人信息</div>
        <div class="item" :class="{ active: tab==='video' }" @click="tab='video'">观看视频</div>
        <div class="item" :class="{ active: tab==='homework' }" @click="tab='homework'">提交作业</div>
        <div class="item" :class="{ active: tab==='exam' }" @click="tab='exam'">考试</div>
      </nav>
    </aside>

    <!-- 右侧内容 -->
    <main class="content">
      <!-- 个人信息 -->
      <section v-if="tab==='profile'" class="card">
        <h3>个人信息</h3>
        <div v-if="!studentId" class="warn">未检测到登录学生信息，请重新登录。</div>
        <div v-else class="kv">
          <div>学生：<strong>{{ studentName }}</strong></div>
          <div>ID：{{ studentId }}</div>
        </div>
      </section>

      <!-- 观看视频：B 方案（timeupdate 按 10 秒桶上报） -->
      <section v-if="tab==='video'" class="card">
        <h3>观看视频</h3>

        <div v-if="!studentId" class="warn">未检测到登录学生信息，请重新登录。</div>

        <div v-else>
          <div class="bar">
            <div>学生：<strong>{{ studentName }}</strong>（ID: {{ studentId }}）</div>
            <div class="meta">课程ID：{{ courseId }}</div>
          </div>

          <video
              ref="videoRef"
              class="video"
              :src="videoUrl"
              controls
              preload="metadata"
              @loadedmetadata="onLoadedMeta"
              @timeupdate="onTimeUpdate"
              @ended="onEnded"
          />

          <div class="tip">
            播放过程中每累计 10 秒上报一次 <code>VIDEO_PROGRESS</code>（delta=10s）。
            （Network → Fetch/XHR 搜索 <code>videoProgress</code>）
          </div>
        </div>
      </section>

      <!-- 提交作业（你后续把原作业页内容搬进来） -->
      <section v-if="tab==='homework'" class="card">
        <h3>提交作业</h3>
        <div class="tip">这里放你的作业列表与提交逻辑（下一步我帮你合并）。</div>
      </section>

      <!-- 考试（你后续把原考试页内容搬进来） -->
      <section v-if="tab==='exam'" class="card">
        <h3>考试</h3>
        <div class="tip">这里放你的考试列表与答题提交逻辑（下一步我帮你合并）。</div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref } from "vue";

const BASE_URL = "http://localhost:9090";
const USER_KEY = "system-user";

// 默认打开视频页
const tab = ref("video");

// 课程与视频（演示值；后续可从 course.video_url 动态赋值）
const courseId = ref(1);
const videoUrl = ref("http://localhost:9090/videos/demo.mp4");

// 登录学生信息
let studentId = null;
let studentName = "";
try {
  const u = JSON.parse(localStorage.getItem(USER_KEY) || "{}");
  studentId = u?.id ?? null;
  studentName = u?.name ?? "";
} catch {
  studentId = null;
}

// ============ B 方案：timeupdate 按 10 秒桶上报 ============
const videoRef = ref(null);
let lastBucket = -1;   // 0,1,2... 表示 0-9s,10-19s...
let durationCache = 0;

function onLoadedMeta() {
  const v = videoRef.value;
  if (!v) return;
  durationCache = Number.isFinite(v.duration) ? Number(v.duration.toFixed(1)) : 0;
  console.log("[Student] loadedmetadata", { duration: durationCache, src: v.currentSrc });
}

async function sendProgress(deltaSeconds) {
  const v = videoRef.value;
  if (!v || !studentId) return;

  const dur = Number((durationCache || v.duration || 0).toFixed(1));
  const payload = {
    studentId,
    courseId: courseId.value,
    deltaSeconds,
    currentTime: Number(v.currentTime.toFixed(1)),
    duration: dur,
    playbackRate: v.playbackRate || 1.0,
  };

  try {
    console.log("[Student] POST /behavior/event/videoProgress", payload);
    await fetch(`${BASE_URL}/behavior/event/videoProgress`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
  } catch (e) {
    console.error("[Student] 上报失败", e);
  }
  //console.log("[CHECK] studentId=", studentId, "studentName=", studentName, "localStorage=", localStorage.getItem(USER_KEY));

}

function onTimeUpdate() {
  const v = videoRef.value;
  //console.log("[CHECK] timeupdate fired", v?.currentTime, "studentId=", studentId); // ✅加这一行
  if (!v) return;
  if (v.paused || v.ended) return;

  const t = Math.floor(v.currentTime);
  const bucket = Math.floor(t / 10);

  if (bucket > lastBucket) {
    lastBucket = bucket;
    //console.log("[CHECK] bucket hit -> sendProgress", bucket); // ✅再加这一行
    sendProgress(10);
  }
}


function onEnded() {
  //console.log("[Student] ended");
}
</script>

<style scoped>
.layout { display: flex; min-height: 100vh; background: #f5f7fb; }
.sider { width: 260px; background: #111827; color: #fff; padding: 18px 14px; }
.brand { padding: 10px 10px 16px; border-bottom: 1px solid rgba(255,255,255,0.08); }
.t1 { font-weight: 800; font-size: 18px; }
.t2 { margin-top: 6px; font-size: 12px; opacity: .8; }

.menu { margin-top: 14px; display: flex; flex-direction: column; gap: 10px; }
.item {
  padding: 10px 12px; border-radius: 10px;
  cursor: pointer; user-select: none;
  background: rgba(255,255,255,0.06);
}
.item.active { background: rgba(59,130,246,0.55); }

.content { flex: 1; padding: 18px; }
.card {
  background: #fff;
  border-radius: 14px;
  padding: 18px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.06);
}
.warn { color: #c00; font-weight: 700; }
.bar { display: flex; justify-content: space-between; align-items: center; margin: 10px 0 12px; }
.meta { color: #666; font-size: 13px; }
.video { width: 100%; max-width: 860px; background: #000; border-radius: 10px; }
.tip { margin-top: 10px; color: #555; font-size: 13px; }
.kv { color: #333; display: grid; gap: 6px; }
code { background: #f3f4f6; padding: 2px 6px; border-radius: 6px; }
</style>
