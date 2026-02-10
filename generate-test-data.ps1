# 保存为 generate-simple-data.ps1
$baseUrl = "http://localhost:9090"

Write-Host "🚀 开始生成测试数据..." -ForegroundColor Cyan

# 测试连接
try {
    $health = Invoke-RestMethod -Uri "$baseUrl/actuator/health" -Method Get
    Write-Host "✅ 后端连接正常" -ForegroundColor Green
} catch {
    Write-Host "❌ 后端连接失败" -ForegroundColor Red
    exit
}

# 生成测试数据
$testData = @{
    studentId = 1
    studentName = "张三"
    studentNo = "202301001"
    courseId = 1
    courseName = "Java程序设计"
    behaviorDate = "2024-03-01"
    videoWatchTime = 120
    videoCompletionRate = 85.5
    homeworkSubmitCount = 2
    homeworkAvgScore = 88.0
    loginCount = 5
    lastLoginTime = "2024-03-01 18:30:00"
    activeDays = 1
    totalOnlineTime = 180
    focusScore = 8
    interactionCount = 3
    learningProgress = 75.0
    isAtRisk = $false
    riskProbability = 0.2
}

# 转换为JSON
$jsonBody = $testData | ConvertTo-Json

Write-Host "📤 发送测试数据到服务器..." -ForegroundColor Yellow

try {
    # 先尝试batchAdd，如果不存在再尝试add
    $response = Invoke-RestMethod -Uri "$baseUrl/behavior/add" `
        -Method Post `
        -ContentType "application/json" `
        -Body $jsonBody

    Write-Host "✅ 数据添加成功" -ForegroundColor Green
    Write-Host "📊 响应: $($response | ConvertTo-Json -Compress)" -ForegroundColor Cyan

} catch {
    Write-Host "❌ 添加失败: $_" -ForegroundColor Red
}

# 验证数据
Write-Host "🔍 验证数据..." -ForegroundColor Yellow
try {
    $count = Invoke-RestMethod -Uri "$baseUrl/behavior/count" -Method Get
    Write-Host "📈 当前行为记录总数: $($count.data)" -ForegroundColor Cyan
} catch {
    Write-Host "⚠️  无法获取统计" -ForegroundColor Yellow
}