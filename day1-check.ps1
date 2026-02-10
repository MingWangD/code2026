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

Write-Step "Day1 回归检查"
$repoRoot = (Get-Location).Path
$vueDir = Resolve-VueDir -StartDir $repoRoot

if (-not $vueDir) {
    throw "未找到前端目录。请在仓库根目录（包含 vue/）或 vue 目录中执行脚本。"
}

Write-Host "[INFO] 当前目录: $repoRoot"
Write-Host "[INFO] 识别到前端目录: $vueDir"

$viteConfigPath = Join-Path $vueDir "vite.config.js"
Write-Host "[INFO] vite.config.js 路径: $viteConfigPath"

Write-Step "前端可达性"
$okViteClient = Check-Url "http://localhost:5175/@vite/client" "Vite 客户端探针"
$okRoot = Check-Url "http://localhost:5175/" "前端根路径 /"
$okLogin = Check-Url "http://localhost:5175/login" "前端登录路径 /login"

Write-Step "前端源码配置检查"
$healthPath = Join-Path $vueDir "src/api/health.ts"
if (Test-Path $healthPath) {
    $healthContent = Get-Content $healthPath -Raw
    if ($healthContent -match "localhost:9090|axios\.create") {
        Write-Host "[WARN] health.ts 仍包含硬编码或自建 axios 实例" -ForegroundColor Yellow
    } else {
        Write-Host "[PASS] health.ts 已复用统一 request" -ForegroundColor Green
    }
} else {
    Write-Host "[WARN] 未找到 $healthPath" -ForegroundColor Yellow
}

Write-Step "后端接口检查"
$okPing = Check-Url "http://localhost:9090/api/health/ping" "后端健康检查"
$okDash = Check-Url "http://localhost:9090/api/dashboard/health" "Dashboard 健康检查"

Write-Step "总结"
Write-Host "Vite probe: $okViteClient"
Write-Host "FE /: $okRoot"
Write-Host "FE /login: $okLogin"
Write-Host "BE /api/health/ping: $okPing"
Write-Host "BE /api/dashboard/health: $okDash"

if (-not $okViteClient) {
    Write-Host "[TIP] 若 @vite/client 不通，说明当前端口不是 Vite 开发服务。" -ForegroundColor Yellow
}
if ($okViteClient -and -not $okRoot) {
    Write-Host "[TIP] @vite/client 正常但 / 异常时，优先检查代理/中间件或访问的端口是否与终端输出一致。" -ForegroundColor Yellow
}
