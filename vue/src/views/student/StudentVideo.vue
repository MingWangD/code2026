<template>
  <div class="card">
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
          @play="onPlay"
          @pause="onPauseOrEnded"
          @ended="onPauseOrEnded"
      />

      <div class="tip">
        播放后系统每 10 秒写入一次 <code>VIDEO_PROGRESS</code>（delta=10s）。
        你可以边播放边查数据库确认事件增长。
      </div>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, ref } from "vue";

const API_BASE = (import.meta.env.VITE_BASE_URL || "").replace(/\/$/, "");
const USER_KEY = "system-user";
const HEARTBEAT_INTERVAL = 10_000;

// 这里先用演示值：后续我们再接课程列表/课程详情动态加载
const courseId = ref(1);
const videoUrl = ref(`${API_BASE}/videos/demo.mp4`);

let studentId = null;
let studentName = "";
try {
  const u = JSON.parse(localStorage.getItem(USER_KEY) || "{}");
  studentId = u?.id ?? null;
  studentName = u?.name ?? "";
} catch {
  studentId = null;
}

const videoRef = ref(null);
let timer = null;

async function sendProgress() {
  const video = videoRef.value;
  if (!video || !studentId) return;
  if (video.paused || video.ended) return;

  const payload = {
    studentId,
    courseId: courseId.value,
    deltaSeconds: 10,
    currentTime: Number(video.currentTime.toFixed(1)),
    duration: Number((video.duration || 0).toFixed(1)),
    playbackRate: video.playbackRate || 1.0,
  };

  try {
    await fetch(`${API_BASE}/behavior/event/videoProgress`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
  } catch (e) {
    console.error("视频心跳上报失败", e);
  }
}

function startHeartbeat() {
  stopHeartbeat();
  timer = setInterval(sendProgress, HEARTBEAT_INTERVAL);
}
function stopHeartbeat() {
  if (timer) clearInterval(timer);
  timer = null;
}
function onPlay() {
  startHeartbeat();
}
function onPauseOrEnded() {
  stopHeartbeat();
}

onBeforeUnmount(() => stopHeartbeat());
</script>

<style scoped>
.card {
  background: #fff;
  border-radius: 14px;
  padding: 18px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.06);
}
.warn { color: #c00; font-weight: 700; }
.bar { display: flex; justify-content: space-between; align-items: center; margin: 10px 0 12px; }
.meta { color: #666; font-size: 13px; }
.video {
  width: 100%;
  max-width: 860px;
  background: #000;
  border-radius: 10px;
}
.tip {
  margin-top: 10px;
  color: #555;
  font-size: 13px;
}
code { background: #f3f4f6; padding: 2px 6px; border-radius: 6px; }
</style>
