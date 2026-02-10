<template>
  <div>
    <!-- 顶部：选择区 -->
    <div class="card" style="margin-bottom: 12px;">
      <div style="font-size: 18px; font-weight: 600; margin-bottom: 10px;">
        教师视角：学生行为时间线（下拉选择 + 行为判读）
      </div>

      <div style="display:flex; gap:10px; flex-wrap: wrap; align-items: center;">
        <!-- 学生下拉 -->
        <el-select
            v-model="data.studentId"
            filterable
            clearable
            style="width: 260px"
            placeholder="选择学生"
            @change="onSelectionChange"
            :loading="data.loadingStudents"
        >
          <el-option
              v-for="s in data.students"
              :key="s.id"
              :label="studentLabel(s)"
              :value="s.id"
          />
        </el-select>

        <!-- 课程下拉 -->
        <el-select
            v-model="data.courseId"
            filterable
            clearable
            style="width: 260px"
            placeholder="选择课程"
            @change="onSelectionChange"
            :loading="data.loadingCourses"
        >
          <el-option
              v-for="c in data.courses"
              :key="c.id"
              :label="courseLabel(c)"
              :value="c.id"
          />
        </el-select>

        <el-button type="primary" @click="loadAll" :loading="data.loading || data.loadingInsights">查询</el-button>
        <el-button type="info" @click="reset">重置</el-button>

        <el-switch
            v-model="data.autoRefresh"
            inline-prompt
            active-text="自动刷新"
            inactive-text="手动"
        />
        <span style="color:#666; font-size: 13px;">
          刷新间隔：{{ data.refreshSec }} 秒
        </span>

        <el-button @click="reloadBaseData" :loading="data.loadingStudents || data.loadingCourses">
          重新加载学生/课程
        </el-button>
      </div>

      <div style="margin-top: 10px; color:#666; font-size: 13px; line-height: 20px;">
        最近更新时间：{{ data.lastUpdate || '-' }}；
        当前记录数：{{ data.list.length }}
      </div>
    </div>

    <!-- ✅ 行为判读层（A） -->
    <div class="card" style="margin-bottom: 12px;">
      <div style="display:flex; align-items:center; justify-content: space-between; gap:12px;">
        <div style="font-size: 16px; font-weight: 600;">
          教师行为判读（今日）
        </div>
        <el-button size="small" @click="loadInsights" :loading="data.loadingInsights" :disabled="!data.studentId || !data.courseId">
          刷新判读
        </el-button>
      </div>

      <div v-if="!data.studentId || !data.courseId" style="margin-top: 10px; color:#999; font-size: 13px;">
        请选择学生与课程后显示判读结果。
      </div>

      <div v-else style="margin-top: 10px;">
        <div style="color:#333; font-size: 14px; line-height: 22px;">
          <div style="margin-bottom: 6px;">
            <span style="color:#666;">日期：</span>{{ data.insights?.date || '-' }}
          </div>

          <div style="margin-bottom: 6px;">
            <span style="color:#666;">摘要：</span>{{ data.insights?.summary || '-' }}
          </div>

          <div style="margin-bottom: 10px;">
            <span style="color:#666;">标签：</span>
            <span v-if="(data.insights?.tags || []).length === 0" style="color:#999;">无</span>
            <el-tag
                v-else
                v-for="(t, idx) in data.insights.tags"
                :key="idx"
                style="margin-right: 6px; margin-bottom: 6px;"
                type="warning"
            >
              {{ t }}
            </el-tag>
          </div>

          <div style="display:flex; flex-wrap: wrap; gap:10px;">
            <el-tag type="info">登录：{{ data.insights?.metrics?.loginCount ?? 0 }}</el-tag>
            <el-tag type="info">视频：{{ data.insights?.metrics?.videoCount ?? 0 }}</el-tag>
            <el-tag type="success">作业提交：{{ data.insights?.metrics?.homeworkSubmitCount ?? 0 }}</el-tag>
            <el-tag type="danger">迟交：{{ data.insights?.metrics?.homeworkLateCount ?? 0 }}</el-tag>
            <el-tag type="success">作业均分：{{ data.insights?.metrics?.homeworkAvgScore ?? 0 }}</el-tag>
            <el-tag type="danger">考试：{{ data.insights?.metrics?.examCount ?? 0 }}</el-tag>
            <el-tag type="info">今日事件数：{{ data.insights?.eventCount ?? 0 }}</el-tag>
          </div>
        </div>

        <div style="margin-top: 12px; color:#999; font-size: 12px;">
          数据来源：<code>/teacher/behavior/insights</code>
        </div>
      </div>
    </div>

    <!-- 时间线表 -->
    <div class="card">
      <div style="display:flex; align-items:center; justify-content: space-between; gap:12px; margin-bottom: 8px;">
        <div style="font-size: 16px; font-weight: 600;">事件时间线</div>
        <el-button size="small" @click="loadEvents" :loading="data.loading" :disabled="!data.studentId || !data.courseId">
          刷新时间线
        </el-button>
      </div>

      <el-table :data="data.list" stripe v-loading="data.loading" style="width: 100%">
        <el-table-column label="时间" prop="behaviorTime" width="180" />
        <el-table-column label="类型" width="150">
          <template #default="scope">
            <el-tag v-if="scope.row.behaviorType === 'HOMEWORK_SUBMIT'" type="success">作业提交</el-tag>
            <el-tag v-else-if="scope.row.behaviorType === 'VIDEO_WATCH'" type="info">观看视频</el-tag>
            <el-tag v-else-if="scope.row.behaviorType === 'LOGIN'" type="warning">登录</el-tag>
            <el-tag v-else-if="scope.row.behaviorType === 'EXAM'" type="danger">考试</el-tag>
            <el-tag v-else>{{ scope.row.behaviorType }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="关联ID" prop="relatedId" width="140" />
        <el-table-column label="得分/完成率" prop="score" width="140" />

        <el-table-column label="迟交" width="90">
          <template #default="scope">
            <span v-if="scope.row.behaviorType === 'HOMEWORK_SUBMIT'">
              <el-tag v-if="scope.row.isLate === 1" type="danger">是</el-tag>
              <el-tag v-else type="success">否</el-tag>
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column label="次数/分钟" width="110">
          <template #default="scope">
            <span v-if="scope.row.behaviorType === 'HOMEWORK_SUBMIT'">
              第 {{ scope.row.attemptNo || 1 }} 次
            </span>
            <span v-else-if="scope.row.behaviorType === 'VIDEO_WATCH'">
              {{ scope.row.attemptNo || 0 }} 分钟
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column label="学生ID" prop="studentId" width="100" />
        <el-table-column label="课程ID" prop="courseId" width="100" />
      </el-table>

      <div style="margin-top: 12px; color:#999; font-size: 12px;">
        说明：下拉数据来自 <code>/student/selectAll</code> 与 <code>/course/selectAll</code>；
        时间线来自 <code>/behavior/event/selectByStudentAndCourse</code>。
      </div>
    </div>
  </div>
</template>

<script setup>
import request from "../../utils/request";
import { reactive, onBeforeUnmount, watch, onMounted } from "vue";
import { ElMessage } from "element-plus";

let timer = null;

const data = reactive({
  studentId: null,
  courseId: null,

  students: [],
  courses: [],
  loadingStudents: false,
  loadingCourses: false,

  list: [],
  loading: false,

  insights: null,
  loadingInsights: false,

  lastUpdate: "",

  autoRefresh: true,
  refreshSec: 3,
});

const studentLabel = (s) => {
  const name = s.name || s.studentName || "";
  const no = s.studentNo ? `（${s.studentNo}）` : "";
  return `${name || "学生"}${no} - ID:${s.id}`;
};

const courseLabel = (c) => {
  const name = c.name || c.courseName || c.title || "课程";
  return `${name} - ID:${c.id}`;
};

const reloadBaseData = async () => {
  await Promise.all([loadStudents(), loadCourses()]);
};

const loadStudents = async () => {
  data.loadingStudents = true;
  try {
    const res = await request.get("/student/selectAll");
    if (res.code === "200" || res.code === 200) data.students = res.data || [];
    else ElMessage.error(res.msg || "加载学生列表失败");
  } catch (e) {
    ElMessage.error("加载学生列表失败：请检查后端是否启动/跨域/接口路径");
  } finally {
    data.loadingStudents = false;
  }
};

const loadCourses = async () => {
  data.loadingCourses = true;
  try {
    const res = await request.get("/course/selectAll");
    if (res.code === "200" || res.code === 200) data.courses = res.data || [];
    else ElMessage.error(res.msg || "加载课程列表失败");
  } catch (e) {
    ElMessage.error("加载课程列表失败：请检查后端是否启动/跨域/接口路径");
  } finally {
    data.loadingCourses = false;
  }
};

const canQuery = () => {
  if (!data.studentId || !data.courseId) {
    ElMessage.warning("请先选择学生和课程");
    return false;
  }
  return true;
};

// ========== 事件时间线 ==========
const loadEvents = async () => {
  if (!canQuery()) return;

  data.loading = true;
  try {
    const res = await request.get("/behavior/event/selectByStudentAndCourse", {
      params: { studentId: data.studentId, courseId: data.courseId },
    });

    if (res.code === "200" || res.code === 200) {
      data.list = res.data || [];
      data.lastUpdate = new Date().toLocaleString();
    } else {
      ElMessage.error(res.msg || "查询失败");
    }
  } catch (e) {
    ElMessage.error("请求失败，请检查后端是否启动/跨域配置");
  } finally {
    data.loading = false;
  }
};

// ========== 行为判读（A） ==========
const loadInsights = async () => {
  if (!canQuery()) return;

  data.loadingInsights = true;
  try {
    const res = await request.get("/teacher/behavior/insights", {
      params: { studentId: data.studentId, courseId: data.courseId },
    });

    if (res.code === "200" || res.code === 200) {
      data.insights = res.data || null;
    } else {
      ElMessage.error(res.msg || "加载判读失败");
    }
  } catch (e) {
    ElMessage.error("加载判读失败：请检查 /teacher/behavior/insights 是否可用");
  } finally {
    data.loadingInsights = false;
  }
};

// 一键：同时拉判读 + 时间线
const loadAll = async () => {
  if (!canQuery()) return;
  await Promise.all([loadInsights(), loadEvents()]);
};

const onSelectionChange = () => {
  if (data.studentId && data.courseId) loadAll();
};

const reset = () => {
  data.studentId = null;
  data.courseId = null;
  data.list = [];
  data.insights = null;
  data.lastUpdate = "";
};

// 自动刷新：刷新判读 + 时间线
const startTimer = () => {
  stopTimer();
  timer = setInterval(() => {
    if (data.studentId && data.courseId) loadAll();
  }, data.refreshSec * 1000);
};

const stopTimer = () => {
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
};

watch(
    () => data.autoRefresh,
    (v) => {
      if (v) startTimer();
      else stopTimer();
    },
    { immediate: true }
);

onMounted(async () => {
  await reloadBaseData();
});

onBeforeUnmount(() => stopTimer());
</script>
