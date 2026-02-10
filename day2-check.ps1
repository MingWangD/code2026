$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

function Write-Step($msg) {
  Write-Host ""
  Write-Host "== $msg ==" -ForegroundColor Cyan
}

function Resolve-VueDir {
  param([string]$StartDir)

  $candidate1 = Join-Path $StartDir "vue"
  if (Test-Path (Join-Path $candidate1 "vite.config.js")) {
    return $candidate1
  }

  if (Test-Path (Join-Path $StartDir "vite.config.js")) {
    return $StartDir
  }

  return $null
}

function Check-Url($url, $name) {
  try {
    $resp = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 6
    Write-Host "[PASS] $name -> HTTP $($resp.StatusCode)" -ForegroundColor Green
    return $true
  } catch {
    Write-Host "[FAIL] $name -> $($_.Exception.Message)" -ForegroundColor Red
    return $false
  }
}

function Check-RestJson($url, $name) {
  try {
    $resp = Invoke-RestMethod -Uri $url -Method GET -TimeoutSec 8
    Write-Host "[PASS] $name" -ForegroundColor Green
    return @{ ok = $true; data = $resp }
  } catch {
    Write-Host "[FAIL] $name -> $($_.Exception.Message)" -ForegroundColor Red
    return @{ ok = $false; data = $null }
  }
}

function Contains-Pattern($path, $pattern) {
  if (-not (Test-Path $path)) { return $false }
  $raw = Get-Content $path -Raw
  return [bool]($raw -match $pattern)
}

Write-Step "Day2 自动判定开始"
$repoRoot = (Get-Location).Path
$vueDir = Resolve-VueDir -StartDir $repoRoot

if (-not $vueDir) {
  throw "未找到前端目录。请在仓库根目录（包含 vue/）或 vue 目录中执行脚本。"
}

Write-Host "[INFO] 当前目录: $repoRoot"
Write-Host "[INFO] 识别到前端目录: $vueDir"

$studentView = Join-Path $vueDir "src/views/Student.vue"
$hwView = Join-Path $vueDir "src/views/student/StudentHomework.vue"
$examView = Join-Path $vueDir "src/views/student/StudentExam.vue"
$healthApi = Join-Path $vueDir "src/api/health.ts"
$envDev = Join-Path $vueDir ".env.development"

Write-Step "静态代码判定（Day2 关键点）"
$checks = @(
  @{ name = "Student.vue 存在课程选择区"; ok = (Contains-Pattern $studentView "当前课程|course-select|loadCourses") },
  @{ name = "Student.vue 向作业模块传 course-id"; ok = (Contains-Pattern $studentView "StudentHomework\s+:course-id") },
  @{ name = "Student.vue 向考试模块传 course-id"; ok = (Contains-Pattern $studentView "StudentExam\s+:course-id") },
  @{ name = "Homework 使用 defineProps + watch(courseId)"; ok = ((Contains-Pattern $hwView "defineProps") -and (Contains-Pattern $hwView "watch\s*\(\s*\(\)\s*=>\s*props\.courseId")) },
  @{ name = "Exam 使用 defineProps + watch(courseId)"; ok = ((Contains-Pattern $examView "defineProps") -and (Contains-Pattern $examView "watch\s*\(\s*\(\)\s*=>\s*props\.courseId")) },
  @{ name = "health.ts 使用共享 request"; ok = ((Contains-Pattern $healthApi "import\s+request\s+from\s+'../utils/request'") -and -not (Contains-Pattern $healthApi "axios\.create|localhost:9090")) },
  @{ name = ".env.development 包含 VITE_BASE_URL"; ok = (Contains-Pattern $envDev "VITE_BASE_URL") }
)

$staticPass = 0
foreach ($c in $checks) {
  if ($c.ok) {
    Write-Host "[PASS] $($c.name)" -ForegroundColor Green
    $staticPass++
  } else {
    Write-Host "[FAIL] $($c.name)" -ForegroundColor Red
  }
}

Write-Step "后端接口判定（可选但推荐）"
$beHealth = Check-RestJson "http://localhost:9090/api/health/ping" "后端 /api/health/ping"
$courseAll = Check-RestJson "http://localhost:9090/course/selectAll" "后端 /course/selectAll"

$courseCount = 0
if ($courseAll.ok -and $null -ne $courseAll.data -and $null -ne $courseAll.data.data) {
  try {
    $courseCount = @($courseAll.data.data).Count
  } catch {
    $courseCount = 0
  }
}

if ($courseAll.ok) {
  Write-Host "[INFO] /course/selectAll 返回课程数: $courseCount"
  if ($courseCount -eq 0) {
    Write-Host "[WARN] 课程列表为空，前端会显示默认课程ID提示。" -ForegroundColor Yellow
  }
} else {
  Write-Host "[WARN] 未能访问 /course/selectAll，无法完成联调判定。" -ForegroundColor Yellow
}

Write-Step "前端服务判定（按实际端口）"
Write-Host "[TIP] 如果你本地 Vite 不是 5173，请手动替换下面地址测试。"
$feVite = Check-Url "http://localhost:5173/@vite/client" "前端 Vite 探针(5173)"
$feStudent = Check-Url "http://localhost:5173/student" "前端学生页(5173)"
if (-not $feVite) {
  Write-Host "[WARN] 5173 不通通常是 Vite 使用了 5174/5175，请看 npm run dev 输出。" -ForegroundColor Yellow
}

Write-Step "结论"
Write-Host "静态判定通过: $staticPass / $($checks.Count)"
if ($staticPass -eq $checks.Count) {
  Write-Host "[RESULT] Day2 代码结构判定通过。" -ForegroundColor Green
} else {
  Write-Host "[RESULT] Day2 代码结构判定未全部通过，请按 FAIL 项修复。" -ForegroundColor Red
}

if ($beHealth.ok -and $courseAll.ok) {
  Write-Host "[RESULT] 后端联调接口可用。" -ForegroundColor Green
} else {
  Write-Host "[RESULT] 后端联调接口未完全可用（请先启动 springboot）。" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "== Day2 自动判定结束 ==" -ForegroundColor Cyan
