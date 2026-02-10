<template>
  <div class="card">
    <h3>个人信息</h3>
    <div v-if="!user" class="warn">未检测到登录信息，请先登录。</div>

    <div v-else class="grid">
      <div class="row"><span class="k">姓名</span><span class="v">{{ user.name }}</span></div>
      <div class="row"><span class="k">角色</span><span class="v">{{ user.role }}</span></div>
      <div class="row"><span class="k">ID</span><span class="v">{{ user.id }}</span></div>
      <div class="row"><span class="k">头像</span><span class="v">{{ user.avatar ?? "null" }}</span></div>
    </div>

    <p class="tip">这里只做展示，不做复杂资料编辑（最小改动）。</p>
  </div>
</template>

<script setup>
const USER_KEY = "system-user";
let user = null;
try {
  user = JSON.parse(localStorage.getItem(USER_KEY) || "null");
} catch {
  user = null;
}
</script>

<style scoped>
.card {
  background: #fff;
  border-radius: 14px;
  padding: 18px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.06);
}
.warn { color: #c00; font-weight: 700; }
.grid { margin-top: 10px; display: grid; gap: 10px; max-width: 420px; }
.row { display: flex; justify-content: space-between; }
.k { color: #666; }
.v { font-weight: 600; }
.tip { margin-top: 14px; color: #777; font-size: 13px; }
</style>
