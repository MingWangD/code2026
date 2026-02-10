package com.example.controller.dto;

public class VideoProgressDTO {
    private Integer studentId;
    private Integer courseId;

    // 本次心跳确认的播放增量（秒），比如 10
    private Integer deltaSeconds;

    // 播放器当前时间（秒）
    private Double currentTime;

    // 视频总时长（秒）
    private Double duration;

    // 可选：前端可传播放倍速
    private Double playbackRate;

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public Integer getDeltaSeconds() { return deltaSeconds; }
    public void setDeltaSeconds(Integer deltaSeconds) { this.deltaSeconds = deltaSeconds; }

    public Double getCurrentTime() { return currentTime; }
    public void setCurrentTime(Double currentTime) { this.currentTime = currentTime; }

    public Double getDuration() { return duration; }
    public void setDuration(Double duration) { this.duration = duration; }

    public Double getPlaybackRate() { return playbackRate; }
    public void setPlaybackRate(Double playbackRate) { this.playbackRate = playbackRate; }
}
