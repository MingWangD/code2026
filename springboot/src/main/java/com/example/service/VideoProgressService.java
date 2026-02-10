package com.example.service;

import com.example.controller.dto.VideoProgressDTO;
import com.example.exception.CustomException;
import com.example.mapper.StudentBehaviorEventMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class VideoProgressService {

    @Resource
    private StudentBehaviorEventMapper studentBehaviorEventMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void recordVideoProgress(VideoProgressDTO dto) {
        if (dto == null) throw new CustomException("参数不能为空");
        if (dto.getStudentId() == null || dto.getCourseId() == null) throw new CustomException("studentId/courseId 不能为空");
        if (dto.getDeltaSeconds() == null || dto.getDeltaSeconds() <= 0) throw new CustomException("deltaSeconds 必须 > 0");

        // 额外信息装进 JSON（你未来可以扩展：设备、清晰度、网络等）
        Map<String, Object> extra = new HashMap<>();
        extra.put("currentTime", dto.getCurrentTime());
        extra.put("duration", dto.getDuration());
        extra.put("playbackRate", dto.getPlaybackRate());

        String extraJson;
        try {
            extraJson = objectMapper.writeValueAsString(extra);
        } catch (Exception e) {
            throw new CustomException("behavior_extra 序列化失败：" + e.getMessage());
        }

        studentBehaviorEventMapper.insertEvent(
                dto.getStudentId(),
                dto.getCourseId(),
                "VIDEO_PROGRESS",
                null,
                null,
                0,
                1,
                dto.getDeltaSeconds().doubleValue(),
                extraJson,
                LocalDateTime.now()
        );
    }
}
