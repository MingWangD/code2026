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
  }
  catch {
    Write-Host "[FAIL] $name -> $($_.Exception.Message)" -ForegroundColor Red
    return $false
  }
}

function Check-RestJson($url, $name) {
  try {
    $resp = Invoke-RestMethod -Uri $url -Method GET -TimeoutSec 8
    Write-Host "[PASS] $name" -ForegroundColor Green
    return @{ ok = $true; data = $resp }
  }
  catch {
    Write-Host "[FAIL] $name -> $($_.Exception.Message)" -ForegroundColor Red
    return @{ ok = $false; data = $null }
  }
}

function Contains-Pattern($path, $pattern) {
  if (-not (Test-Path $path)) { return $false }
  $raw = Get-Content $path -Raw
  return [bool]($raw -match $pattern)
}

Write-Step "Day2 auto check start"
$repoRoot = (Get-Location).Path
$vueDir = Resolve-VueDir -StartDir $repoRoot

if (-not $vueDir) {
  throw "Cannot find frontend dir. Run this script from repo root (with vue/) or from vue/."
}

Write-Host "[INFO] cwd: $repoRoot"
Write-Host "[INFO] vueDir: $vueDir"

$studentView = Join-Path $vueDir "src/views/Student.vue"
$hwView = Join-Path $vueDir "src/views/student/StudentHomework.vue"
$examView = Join-Path $vueDir "src/views/student/StudentExam.vue"
$healthApi = Join-Path $vueDir "src/api/health.ts"
$envDev = Join-Path $vueDir ".env.development"

Write-Step "Static checks (Day2 key points)"
$checks = @(
  @{ name = "Student.vue has course selector"; ok = (Contains-Pattern $studentView "course-select|loadCourses|当前课程") },
  @{ name = "Student.vue passes course-id to StudentHomework"; ok = (Contains-Pattern $studentView "StudentHomework\s+:course-id") },
  @{ name = "Student.vue passes course-id to StudentExam"; ok = (Contains-Pattern $studentView "StudentExam\s+:course-id") },
  @{ name = "StudentHomework uses defineProps + watch(courseId)"; ok = ((Contains-Pattern $hwView "defineProps") -and (Contains-Pattern $hwView "watch\s*\(\s*\(\)\s*=>\s*props\.courseId")) },
  @{ name = "StudentExam uses defineProps + watch(courseId)"; ok = ((Contains-Pattern $examView "defineProps") -and (Contains-Pattern $examView "watch\s*\(\s*\(\)\s*=>\s*props\.courseId")) },
  @{ name = "health.ts uses shared request"; ok = ((Contains-Pattern $healthApi "import\s+request\s+from\s+'../utils/request'") -and -not (Contains-Pattern $healthApi "axios\.create|localhost:9090")) },
  @{ name = ".env.development contains VITE_BASE_URL"; ok = (Contains-Pattern $envDev "VITE_BASE_URL") }
)

$staticPass = 0
foreach ($c in $checks) {
  if ($c.ok) {
    Write-Host "[PASS] $($c.name)" -ForegroundColor Green
    $staticPass++
  }
  else {
    Write-Host "[FAIL] $($c.name)" -ForegroundColor Red
  }
}

Write-Step "Backend API checks (optional)"
$beHealth = Check-RestJson "http://localhost:9090/api/health/ping" "GET /api/health/ping"
$courseAll = Check-RestJson "http://localhost:9090/course/selectAll" "GET /course/selectAll"

$courseCount = 0
if ($courseAll.ok -and $null -ne $courseAll.data -and $null -ne $courseAll.data.data) {
  try {
    $courseCount = @($courseAll.data.data).Count
  }
  catch {
    $courseCount = 0
  }
}

if ($courseAll.ok) {
  Write-Host "[INFO] /course/selectAll count: $courseCount"
  if ($courseCount -eq 0) {
    Write-Host "[WARN] Course list is empty. Frontend may fallback to default course id." -ForegroundColor Yellow
  }
}
else {
  Write-Host "[WARN] Cannot access /course/selectAll. Start springboot first." -ForegroundColor Yellow
}

Write-Step "Frontend probe (default port 5173)"
Write-Host "[TIP] If Vite is on 5174/5175, replace the port below."
$feVite = Check-Url "http://localhost:5173/@vite/client" "GET /@vite/client"
$feStudent = Check-Url "http://localhost:5173/student" "GET /student"
if (-not $feVite) {
  Write-Host "[WARN] 5173 unavailable. Check npm run dev output for the actual port." -ForegroundColor Yellow
}

Write-Step "Summary"
Write-Host "Static pass: $staticPass / $($checks.Count)"
if ($staticPass -eq $checks.Count) {
  Write-Host "[RESULT] Day2 static checks PASSED." -ForegroundColor Green
}
else {
  Write-Host "[RESULT] Day2 static checks NOT fully passed." -ForegroundColor Red
}

if ($beHealth.ok -and $courseAll.ok) {
  Write-Host "[RESULT] Backend checks PASSED." -ForegroundColor Green
}
else {
  Write-Host "[RESULT] Backend checks PARTIAL/FAILED." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "== Day2 auto check end ==" -ForegroundColor Cyan
