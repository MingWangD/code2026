$baseUrl = "http://localhost:9090"

Write-Host "🚀 开始生成真实测试数据..." -ForegroundColor Cyan

# 测试连接
try {
    $health = Invoke-RestMethod -Uri "$baseUrl/actuator/health" -Method Get
    Write-Host "✅ 后端连接正常" -ForegroundColor Green
} catch {
    Write-Host "❌ 后端连接失败" -ForegroundColor Red
    exit
}

# 定义学生和课程
$students = @(
    @{id=1; name="张三"; type="good"},
    @{id=2; name="李四"; type="risk"},
    @{id=3; name="王五"; type="improving"}
)

$courses = @(
    @{id=1; name="Java程序设计"},
    @{id=2; name="数据库原理"},
    @{id=3; name="Web前端开发"}
)

# 生成5天的数据
$totalRecords = 0
for ($day = 1; $day -le 5; $day++) {
    $date = "2024-03-0$day"
    
    Write-Host "📅 生成 $date 的数据..." -ForegroundColor Yellow
    
    foreach ($student in $students) {
        foreach ($course in $courses) {
            # 根据学生类型生成不同质量的数据
            if ($student.type -eq "good") {
                # 好学生：学习积极
                $videoTime = Get-Random -Minimum 120 -Maximum 180
                $completion = Get-Random -Minimum 85 -Maximum 98
                $score = Get-Random -Minimum 85 -Maximum 95
                $focus = Get-Random -Minimum 8 -Maximum 10
            } elseif ($student.type -eq "risk") {
                # 风险学生：学习懈怠
                $videoTime = Get-Random -Minimum 40 -Maximum 90
                $completion = Get-Random -Minimum 50 -Maximum 75
                $score = Get-Random -Minimum 60 -Maximum 75
                $focus = Get-Random -Minimum 4 -Maximum 7
            } else {
                # 进步学生：逐渐改善
                $videoTime = Get-Random -Minimum 80 -Maximum 140
                $completion = Get-Random -Minimum 70 -Maximum 90
                $score = Get-Random -Minimum 75 -Maximum 88
                $focus = Get-Random -Minimum 6 -Maximum 9
            }
            
            $behavior = @{
                studentId = $student.id
                studentName = $student.name
                studentNo = "20230100$($student.id)"
                courseId = $course.id
                courseName = $course.name
                behaviorDate = $date
                videoWatchTime = $videoTime
                videoCompletionRate = $completion
                homeworkSubmitCount = 1
                homeworkAvgScore = $score
                loginCount = Get-Random -Minimum 2 -Maximum 5
                lastLoginTime = "$date $(Get-Random -Minimum 9 -Maximum 21):$(Get-Random -Minimum 10 -Maximum 59):00"
                activeDays = 1
                totalOnlineTime = $videoTime + 30
                focusScore = $focus
                interactionCount = Get-Random -Minimum 1 -Maximum 4
                learningProgress = Get-Random -Minimum 60 -Maximum 90
                isAtRisk = $student.type -eq "risk"
                riskProbability = if ($student.type -eq "risk") {0.6} elseif ($student.type -eq "good") {0.2} else {0.4}
            }
            
            $jsonBody = $behavior | ConvertTo-Json
            
            try {
                $response = Invoke-RestMethod -Uri "$baseUrl/behavior/add" `
                    -Method Post `
                    -ContentType "application/json" `
                    -Body $jsonBody
                
                if ($response.code -eq 200) {
                    $totalRecords++
                    Write-Host "   + $($student.name) - $($course.name)" -ForegroundColor Gray
                }
            } catch {
                Write-Host "   ❌ 添加失败" -ForegroundColor Red
            }
        }
    }
}

Write-Host "🎉 数据生成完成！" -ForegroundColor Green
Write-Host "📊 总共生成: $totalRecords 条行为记录" -ForegroundColor Cyan
Write-Host "   📍 3个学生 × 3门课程 × 5天 = 45条记录" -ForegroundColor Cyan

# 验证结果
Write-Host "🔍 验证数据..." -ForegroundColor Yellow
try {
    $count = Invoke-RestMethod -Uri "$baseUrl/behavior/count" -Method Get
    Write-Host "📈 数据库总记录数: $($count.data)" -ForegroundColor Cyan
    
    # 查看最新数据
    $latest = Invoke-RestMethod -Uri "$baseUrl/behavior/selectPage?page=1&size=3" -Method Get
    Write-Host "📋 最新3条记录:" -ForegroundColor Cyan
    $latest.data.list | ForEach-Object {
        Write-Host "   - $($_.studentName) | $($_.courseName) | $($_.behaviorDate) | 风险: $($_.riskProbability)" -ForegroundColor Gray
    }
} catch {
    Write-Host "⚠️  无法验证数据" -ForegroundColor Yellow
}
