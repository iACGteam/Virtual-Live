package com.virtuallive.backend.live.controller;

import com.virtuallive.backend.live.dto.LeaderboardItemDTO;
import com.virtuallive.backend.live.dto.RoomInfoDTO;
import com.virtuallive.backend.live.dto.RoomSettingsDTO;
import com.virtuallive.backend.live.dto.UserInfoDTO;
import com.virtuallive.backend.live.entity.LiveRoom;
import com.virtuallive.backend.live.repository.LiveRoomRepository;
import com.virtuallive.backend.live.repository.LiveSessionRepository;
import com.virtuallive.backend.live.service.IUserService;
import com.virtuallive.backend.live.service.LiveRoomService;
import com.virtuallive.backend.live.service.impl.InteractionServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/live/rooms")
public class LiveRoomController {

    @Autowired private LiveRoomRepository liveRoomRepository;
    @Autowired private LiveSessionRepository liveSessionRepository;
    @Autowired private InteractionServiceImpl interactionService;

    @Autowired private LiveRoomService liveRoomService;
    @Autowired private IUserService userService;

    // 修正：默认端口改为 8088，与 Docker 外部端口保持一致
    @Value("${srs.play.host:http://localhost:8088}") private String srsPlayHost;
    @Value("${srs.rtmp.host:rtmp://localhost/live}") private String srsRtmpHost;

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomInfoDTO> getRoomInfo(@PathVariable Integer roomId) {
        Optional<LiveRoom> roomOpt = liveRoomRepository.findById(roomId);

        if (roomOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        LiveRoom room = roomOpt.get();
        RoomInfoDTO dto = new RoomInfoDTO();
        dto.setRoomId(room.getRoomId());
        dto.setTitle(room.getRoomTitle());
        dto.setDescription(room.getDescription());
        dto.setCoverUrl(room.getThumbnailUrl());
        dto.setLive(room.getIsLive() != null && room.getIsLive());

        if (dto.isLive()) {
            String app = "live";
            dto.setPlayUrlFlv(srsPlayHost + "/" + app + "/" + room.getStreamKey() + ".flv");
            dto.setPlayUrlHls(srsPlayHost + "/" + app + "/" + room.getStreamKey() + ".m3u8");

            liveSessionRepository.findFirstByRoomIdAndEndTimeIsNullOrderByStartTimeDesc(roomId)
                    .ifPresent(session -> dto.setViewerCount(session.getViewerCount()));
        } else {
            dto.setViewerCount(0);
        }

        return ResponseEntity.ok(dto);
    }

    // ================== 新增主播管理接口 ==================

    @GetMapping("/{roomId}/manager/info")
    public ResponseEntity<?> getManagerInfo(@PathVariable Integer roomId) {
        Optional<LiveRoom> roomOpt = liveRoomRepository.findById(roomId);
        if (roomOpt.isEmpty()) return ResponseEntity.notFound().build();

        LiveRoom room = roomOpt.get();

        if (room.getStreamKey() == null || room.getStreamKey().isEmpty()) {
            room.setStreamKey("room_" + roomId + "_" + UUID.randomUUID().toString().substring(0, 8));
            liveRoomRepository.save(room);
        }

        Map<String, Object> info = new HashMap<>();
        info.put("serverAddress", srsRtmpHost);
        info.put("streamKey", room.getStreamKey());
        info.put("title", room.getRoomTitle());
        info.put("coverUrl", room.getThumbnailUrl());
        info.put("category", room.getCategory()); // 返回分类信息
        info.put("isLive", room.getIsLive());

        return ResponseEntity.ok(info);
    }

    @PostMapping("/{roomId}/manager/update")
    public ResponseEntity<?> updateRoomSettings(@PathVariable Integer roomId, @RequestBody RoomSettingsDTO settings) {
        // TODO: 鉴权
        return liveRoomRepository.findById(roomId).map(room -> {
            room.setRoomTitle(settings.getTitle());
            room.setDescription(settings.getDescription());
            room.setThumbnailUrl(settings.getCoverUrl());
            room.setCategory(settings.getCategory());
            liveRoomRepository.save(room);
            return ResponseEntity.ok("更新成功");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{roomId}/manager/stream-key")
    public ResponseEntity<?> resetStreamKey(@PathVariable Integer roomId) {
        // TODO: 鉴权
        return liveRoomRepository.findById(roomId).map(room -> {
            room.setStreamKey("room_" + roomId + "_" + UUID.randomUUID().toString().substring(0, 8));
            liveRoomRepository.save(room);
            return ResponseEntity.ok(Map.of("streamKey", room.getStreamKey()));
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * 禁言用户
     */
    @PostMapping("/{roomId}/manager/mute")
    public ResponseEntity<?> muteUser(@PathVariable Integer roomId, @RequestBody Map<String, Object> payload) {
        // TODO: 鉴权
        Integer userId = (Integer) payload.get("userId");
        Integer duration = (Integer) payload.get("durationSeconds"); // 秒
        interactionService.muteUser(roomId, userId, duration);
        return ResponseEntity.ok("用户已禁言");
    }

    /**
     * 解除禁言
     * 确保此接口存在并映射正确
     */
    @PostMapping("/{roomId}/manager/unmute")
    public ResponseEntity<?> unmuteUser(@PathVariable Integer roomId, @RequestBody Map<String, Object> payload) {
        // TODO: 鉴权
        Integer userId = (Integer) payload.get("userId");
        interactionService.unmuteUser(roomId, userId);
        return ResponseEntity.ok("用户禁言已解除");
    }

    /**
     * 删除弹幕
     */
    @DeleteMapping("/{roomId}/manager/danmaku/{danmakuId}")
    public ResponseEntity<?> deleteDanmaku(@PathVariable Integer roomId, @PathVariable Integer danmakuId) {
        // TODO: 鉴权
        interactionService.deleteDanmaku(danmakuId);
        return ResponseEntity.ok("弹幕已删除");
    }

    // ================== 排行榜接口 ==================

    @GetMapping("/{roomId}/stats/leaderboard")
    public ResponseEntity<List<LeaderboardItemDTO>> getLeaderboard(@PathVariable Integer roomId,
                                                                   @RequestParam(defaultValue = "SESSION") String type) {
        return ResponseEntity.ok(interactionService.getLeaderboard(roomId, type));
    }


    /**
     * ★ 新增：根据当前登录主播获取/创建直播间
     * 前端只传 token，不传 roomId
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyRoom(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(401).body("缺少 Authorization 头");
        }
        UserInfoDTO user = userService.getUserByToken(authHeader);
        if (user == null) {
            return ResponseEntity.status(401).body("无效的 token");
        }

        Integer vtuberId = user.getUserId().intValue();

        // 这里可以加一层：只允许 vtuber 类型的用户创建房间（如果你区分了 userType）
        // 当前先简单允许所有用户
        LiveRoom room = liveRoomService.getOrCreateRoomForVtuber(vtuberId);

        // 复用 RoomInfoDTO 的格式
        RoomInfoDTO dto = new RoomInfoDTO();
        dto.setRoomId(room.getRoomId());
        dto.setTitle(room.getRoomTitle());
        dto.setDescription(room.getDescription());
        dto.setCoverUrl(room.getThumbnailUrl());
        dto.setLive(room.getIsLive() != null && room.getIsLive());

        if (dto.isLive()) {
            String app = "live";
            dto.setPlayUrlFlv(srsPlayHost + "/" + app + "/" + room.getStreamKey() + ".flv");
            dto.setPlayUrlHls(srsPlayHost + "/" + app + "/" + room.getStreamKey() + ".m3u8");

            liveSessionRepository.findFirstByRoomIdAndEndTimeIsNullOrderByStartTimeDesc(room.getRoomId())
                    .ifPresent(session -> dto.setViewerCount(session.getViewerCount()));
        } else {
            dto.setViewerCount(0);
        }

        return ResponseEntity.ok(dto);
    }
}