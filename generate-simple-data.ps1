# 简化版数据生成脚本
$baseUrl = "http://localhost:9090"

Write-Host "?? 测试数据生成开始..." -ForegroundColor Cyan

# 测试连接
try {
    $health = Invoke-RestMethod -Uri "$baseUrl/actuator/health" -Method Get
    Write-Host "? 后端连接正常: $($health.status)" -ForegroundColor Green
} catch {
    Write-Host "? 后端连接失败" -ForegroundColor Red
    exit
}

# 生成一条测试数据
$testData = @{
    studentId = 1
    studentName = "测试学生"
    courseId = 1
    courseName = "测试课程"
    behaviorDate = "2024-03-01"
    videoWatchTime = 120
    homeworkSubmitCount = 1
}

$jsonBody = $testData | ConvertTo-Json

Write-Host "?? 发送测试数据..." -ForegroundColor Yellow

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/behavior/add" `
        -Method Post `
        -ContentType "application/json" `
        -Body $jsonBody
    
    Write-Host "? 成功! 响应状态: $($response.code)" -ForegroundColor Green
    
} catch {
    Write-Host "? 失败: $_" -ForegroundColor Red
}

Write-Host "?? 测试完成" -ForegroundColor Green
