-- Day2 学生端联调数据库准备脚本（可重复执行）
-- 目标：保证学生端 homework / exam 在 course_id=1 下可见、可提交，并便于验证事件入库

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

START TRANSACTION;

-- 1) 固定联调对象：学生=1（张三），课程=1（Java程序设计）
SET @student_id := 1;
SET @course_id := 1;

-- 2) 确保课程存在且进行中
UPDATE course
SET status = '进行中',
    update_time = NOW()
WHERE id = @course_id;

-- 3) 确保作业可见可提交（至少 2 份进行中的作业，截止时间在未来）
INSERT INTO homework (
  id, course_id, course_name, title, description, requirements,
  attachment_url, start_time, deadline,
  total_score, submit_count, graded_count, average_score,
  status, create_time, update_time
)
SELECT 9001, c.id, c.course_name, 'Day2联调作业A', '用于学生端作业提交流程测试', '点击提交即可，不要求上传附件',
       NULL, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY),
       100, 0, 0, 0,
       '进行中', NOW(), NOW()
FROM course c
WHERE c.id = @course_id
  AND NOT EXISTS (SELECT 1 FROM homework h WHERE h.id = 9001);

INSERT INTO homework (
  id, course_id, course_name, title, description, requirements,
  attachment_url, start_time, deadline,
  total_score, submit_count, graded_count, average_score,
  status, create_time, update_time
)
SELECT 9002, c.id, c.course_name, 'Day2联调作业B', '用于多次提交 attemptNo 递增测试', '连续提交两次观察事件 attempt_no',
       NULL, NOW(), DATE_ADD(NOW(), INTERVAL 10 DAY),
       100, 0, 0, 0,
       '进行中', NOW(), NOW()
FROM course c
WHERE c.id = @course_id
  AND NOT EXISTS (SELECT 1 FROM homework h WHERE h.id = 9002);

UPDATE homework
SET status = '进行中',
    deadline = DATE_ADD(NOW(), INTERVAL 7 DAY),
    update_time = NOW()
WHERE course_id = @course_id
  AND id IN (9001, 9002);

-- 4) 准备一份固定考试（发布状态），并挂 3 道题
INSERT INTO exam (
  id, course_id, exam_name, total_score, pass_score, duration_min, status, create_time, update_time
)
SELECT 9001, c.id, 'Day2联调考试（固定）', 100, 60, 30, '发布', NOW(), NOW()
FROM course c
WHERE c.id = @course_id
  AND NOT EXISTS (SELECT 1 FROM exam e WHERE e.id = 9001);

UPDATE exam
SET status = '发布',
    update_time = NOW()
WHERE id = 9001;

-- exam_question 按当前 course 的题库自动选取 3 题
DELETE FROM exam_question WHERE exam_id = 9001;
SET @rn := 0;
INSERT INTO exam_question (exam_id, question_id, score, sort_no)
SELECT 9001, q.id, 33, (@rn := @rn + 1)
FROM question q
WHERE q.course_id = @course_id
ORDER BY q.id
LIMIT 3;

-- 5) 清理本次联调对象的旧测试事件（仅清理 Day2 范围，避免污染 attemptNo）
DELETE FROM student_behavior_event
WHERE student_id = @student_id
  AND course_id = @course_id
  AND (
    (behavior_type = 'HOMEWORK_SUBMIT' AND related_id IN ('9001', '9002'))
    OR (behavior_type = 'EXAM_SUBMIT' AND related_id = '9001')
  );

COMMIT;
SET FOREIGN_KEY_CHECKS = 1;

-- 6) 联调前自检（执行脚本后可直接看结果）
SELECT id, title, status, deadline, submit_count
FROM homework
WHERE id IN (9001, 9002)
ORDER BY id;

SELECT e.id, e.exam_name, e.status, COUNT(eq.id) AS question_count
FROM exam e
LEFT JOIN exam_question eq ON eq.exam_id = e.id
WHERE e.id = 9001
GROUP BY e.id, e.exam_name, e.status;

-- 7) 联调后验收 SQL（学生端提交后执行）
-- 7.1 作业提交事件
SELECT student_id, course_id, behavior_type, related_id, attempt_no, behavior_time
FROM student_behavior_event
WHERE student_id = @student_id
  AND course_id = @course_id
  AND behavior_type = 'HOMEWORK_SUBMIT'
  AND related_id IN ('9001', '9002')
ORDER BY id DESC;

-- 7.2 考试提交事件
SELECT student_id, course_id, behavior_type, related_id, score, attempt_no, behavior_time
FROM student_behavior_event
WHERE student_id = @student_id
  AND course_id = @course_id
  AND behavior_type = 'EXAM_SUBMIT'
  AND related_id = '9001'
ORDER BY id DESC;
