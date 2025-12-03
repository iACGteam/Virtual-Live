// src/main/java/com/virtuallive/backend/live/controller/SrsCallbackController.java
package com.virtuallive.backend.live.controller;

import com.virtuallive.backend.live.dto.SrsCallbackDTO;
import com.virtuallive.backend.live.service.LiveStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/live/callback")
public class SrsCallbackController {

    @Autowired
    private LiveStreamService liveStreamService;

    @PostMapping("/on_publish")
    public int onPublish(@RequestBody SrsCallbackDTO dto) {
        // SRS 发起推流请求
        System.out.println(">>> 收到 SRS 推流请求: " + dto);
        boolean success = liveStreamService.startLive(dto.getStreamKey());
        // 返回 0 表示允许推流，返回 1 表示拒绝（SRS 会断开连接）
        return success ? 0 : 1;
    }

    @PostMapping("/on_unpublish")
    public int onUnpublish(@RequestBody SrsCallbackDTO dto) {
        // SRS 通知推流结束
        System.out.println("<<< 收到 SRS 断流通知: " + dto.getStreamKey());
        liveStreamService.stopLive(dto.getStreamKey());
        return 0;
    }
}