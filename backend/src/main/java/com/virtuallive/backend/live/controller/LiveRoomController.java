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
import com.virtuallive.backend.model.dto.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Comparator;
import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/api/v1/live/rooms")
public class LiveRoomController {

    @Autowired private LiveRoomRepository liveRoomRepository;
    @Autowired private LiveSessionRepository liveSessionRepository;
    @Autowired private InteractionServiceImpl interactionService;

    @Autowired private LiveRoomService liveRoomService;
    @Autowired private IUserService userService;
    @Autowired private com.virtuallive.backend.live.repository.DanmakuRepository danmakuRepository;
    @Autowired private com.virtuallive.backend.live.service.FanBadgeService fanBadgeService;

    // 修正：默认端口改为 8088，与 Docker 外部端口保持一致
    @Value("${srs.play.host:http://localhost:8088}") private String srsPlayHost;
    @Value("${srs.rtmp.host:rtmp://localhost/live}") private String srsRtmpHost;

    @GetMapping("/{roomId}/danmaku/history")
    public R<List<com.virtuallive.backend.live.dto.DanmakuMessage>> getDanmakuHistory(@PathVariable Integer roomId) {
        // 1. 获取当前 Session
        Integer sessionId = liveSessionRepository.findFirstByRoomIdAndEndTimeIsNullOrderByStartTimeDesc(roomId)
                .map(com.virtuallive.backend.live.entity.LiveSession::getSessionId)
                .orElse(null);

        if (sessionId == null) {
            return R.ok(new ArrayList<>());
        }

        // 2. 查询弹幕
        List<com.virtuallive.backend.live.entity.Danmaku> list = danmakuRepository.findTop50BySessionIdAndIsDeletedFalseOrderByCreatedAtDesc(sessionId);
        
        // 3. 转换为 DTO 并反转顺序（按时间正序返回）
        List<com.virtuallive.backend.live.dto.DanmakuMessage> result = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            com.virtuallive.backend.live.entity.Danmaku d = list.get(i);
            com.virtuallive.backend.live.dto.DanmakuMessage msg = new com.virtuallive.backend.live.dto.DanmakuMessage();
            msg.setDanmakuId(d.getDanmakuId());
            msg.setContent(d.getContent());
            msg.setColor(d.getColor());
            msg.setRoomId(roomId);
            // 填充用户信息
            try {
                UserInfoDTO u = userService.getUserById(d.getUserId());
                if (u != null) {
                    msg.setSenderId(u.getUserId());
                    msg.setSenderName(u.getUsername());
                    msg.setSenderAvatar(u.getAvatarUrl());
                    // 填充粉丝等级
                    try {
                        Optional<LiveRoom> roomOpt = liveRoomRepository.findById(roomId);
                        if (roomOpt.isPresent()) {
                            Integer vtuberId = roomOpt.get().getVtuberId();
                            Integer level = fanBadgeService.getFanBadgeLevel(vtuberId, u.getUserId().intValue());
                            msg.setFanLevel(level);
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                }
            } catch (Exception e) {
                msg.setSenderName("未知用户");
            }
            
            // 简单判断类型
            if (d.getContent() != null && d.getContent().startsWith("[SC")) {
                msg.setType("SC");
            } else {
                msg.setType("CHAT");
            }
            
            result.add(msg);
        }
        
        return R.ok(result);
    }

    @GetMapping("/{roomId}")
    public R<RoomInfoDTO> getRoomInfo(@PathVariable Integer roomId) {
        Optional<LiveRoom> roomOpt = liveRoomRepository.findById(roomId);

        if (roomOpt.isEmpty()) {
            return R.error(404, "直播间不存在");
        }

        LiveRoom room = roomOpt.get();
        RoomInfoDTO dto = new RoomInfoDTO();
        dto.setRoomId(room.getRoomId());
        dto.setTitle(room.getRoomTitle());
        dto.setDescription(room.getDescription());
        dto.setCoverUrl(room.getThumbnailUrl());
        dto.setLive(room.getIsLive() != null && room.getIsLive());
        dto.setCategory(room.getCategory());
        dto.setCreatorId(room.getVtuberId());

        // Get creator info
        UserInfoDTO user = userService.getUserById(room.getVtuberId());
        if (user != null) {
            dto.setCreatorName(user.getUsername());
            dto.setCreatorAvatar(user.getAvatarUrl());
        }

        if (dto.isLive()) {
            String app = "live";
            dto.setPlayUrlFlv(srsPlayHost + "/" + app + "/" + room.getStreamKey() + ".flv");
            dto.setPlayUrlHls(srsPlayHost + "/" + app + "/" + room.getStreamKey() + ".m3u8");

            liveSessionRepository.findFirstByRoomIdAndEndTimeIsNullOrderByStartTimeDesc(roomId)
                    .ifPresent(session -> dto.setViewerCount(session.getViewerCount()));
        } else {
            dto.setViewerCount(0);
        }

        return R.ok(dto);
    }

    @GetMapping("/list")
    public R<List<RoomInfoDTO>> listActiveRooms(HttpServletRequest request) {
        // 修改为查询所有房间，不再过滤 isLive=true
        List<LiveRoom> rooms = liveRoomRepository.findAll();
        
        List<RoomInfoDTO> dtos = new ArrayList<>(rooms.stream().map(room -> {
            RoomInfoDTO dto = new RoomInfoDTO();
            dto.setRoomId(room.getRoomId());
            dto.setTitle(room.getRoomTitle());
            dto.setDescription(room.getDescription());
            dto.setCoverUrl(room.getThumbnailUrl());
            // 明确设置直播状态，前端据此显示“未开播”遮罩
            dto.setLive(room.getIsLive() != null && room.getIsLive());
            dto.setCategory(room.getCategory());
            dto.setCreatorId(room.getVtuberId());

            // Get creator info
            UserInfoDTO user = userService.getUserById(room.getVtuberId());
            if (user != null) {
                dto.setCreatorName(user.getUsername());
                dto.setCreatorAvatar(user.getAvatarUrl());
            }

            // Viewer count
            if (dto.isLive()) {
                liveSessionRepository.findFirstByRoomIdAndEndTimeIsNullOrderByStartTimeDesc(room.getRoomId())
                        .ifPresent(session -> dto.setViewerCount(session.getViewerCount()));
            } else {
                dto.setViewerCount(0);
            }
            
            return dto;
        }).toList());

        // 如果用户已登录，将其创建的直播间排在第一位
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && !authHeader.isBlank()) {
            try {
                UserInfoDTO currentUser = userService.getUserByToken(authHeader);
                if (currentUser != null) {
                    Integer currentUserId = currentUser.getUserId().intValue();
                    dtos.sort((a, b) -> {
                        boolean aIsMine = a.getCreatorId() != null && a.getCreatorId().equals(currentUserId);
                        boolean bIsMine = b.getCreatorId() != null && b.getCreatorId().equals(currentUserId);
                        
                        if (aIsMine && !bIsMine) return -1;
                        if (!aIsMine && bIsMine) return 1;
                        return 0;
                    });
                }
            } catch (Exception e) {
                log.error("Error sorting rooms for user", e);
            }
        }

        return R.ok(dtos);
    }

    /**
     * 注销当前用户的直播间
     */
    @DeleteMapping("/my")
    public R<?> deregisterMyRoom(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isBlank()) {
            return R.error(401, "缺少 Authorization 头");
        }
        UserInfoDTO user = userService.getUserByToken(authHeader);
        if (user == null) {
            return R.error(401, "无效的 token");
        }

        Integer vtuberId = user.getUserId().intValue();
        
        // 查找该用户的直播间
        Optional<LiveRoom> roomOpt = liveRoomRepository.findFirstByVtuberId(vtuberId);
        if (roomOpt.isPresent()) {
            LiveRoom room = roomOpt.get();
            // 删除直播间
            liveRoomRepository.delete(room);
            return R.ok("直播间已注销");
        } else {
            return R.error(404, "您还没有创建直播间");
        }
    }

    // ================== 新增主播管理接口 ==================



    @PostMapping("/{roomId}/manager/update")
    public R<?> updateRoomSettings(@PathVariable Integer roomId, @RequestBody RoomSettingsDTO settings) {
        // TODO: 鉴权
        return liveRoomRepository.findById(roomId).map(room -> {
            room.setRoomTitle(settings.getTitle());
            room.setDescription(settings.getDescription());
            room.setThumbnailUrl(settings.getCoverUrl());
            room.setCategory(settings.getCategory());
            liveRoomRepository.save(room);
            return R.ok("更新成功");
        }).orElse(R.error(404, "直播间不存在"));
    }

    @PostMapping("/{roomId}/manager/stream-key")
    public R<?> resetStreamKey(@PathVariable Integer roomId) {
        // TODO: 鉴权
        return liveRoomRepository.findById(roomId).map(room -> {
            room.setStreamKey("room_" + roomId + "_" + UUID.randomUUID().toString().substring(0, 8));
            liveRoomRepository.save(room);
            return R.ok(Map.of("streamKey", room.getStreamKey()));
        }).orElse(R.error(404, "直播间不存在"));
    }

    @PostMapping("/{roomId}/manager/status")
    public R<?> updateLiveStatus(@PathVariable Integer roomId, @RequestBody Map<String, Boolean> payload) {
        // TODO: 鉴权
        Boolean isLive = payload.get("isLive");
        System.out.println("Updating live status for room " + roomId + " to " + isLive);
        if (isLive == null) return R.error(400, "Missing isLive");
        
        return liveRoomRepository.findById(roomId).map(room -> {
            room.setIsLive(isLive);
            liveRoomRepository.save(room);
            return R.ok("Status updated");
        }).orElse(R.error(404, "直播间不存在"));
    }

    /**
     * 禁言用户
     */
    @PostMapping("/{roomId}/manager/mute")
    public R<?> muteUser(@PathVariable Integer roomId, @RequestBody Map<String, Object> payload) {
        // TODO: 鉴权
        Integer userId = (Integer) payload.get("userId");
        Integer duration = (Integer) payload.get("durationSeconds"); // 秒
        interactionService.muteUser(roomId, userId, duration);
        return R.ok("用户已禁言");
    }

    /**
     * 解除禁言
     * 确保此接口存在并映射正确
     */
    @PostMapping("/{roomId}/manager/unmute")
    public R<?> unmuteUser(@PathVariable Integer roomId, @RequestBody Map<String, Object> payload) {
        // TODO: 鉴权
        Integer userId = (Integer) payload.get("userId");
        interactionService.unmuteUser(roomId, userId);
        return R.ok("用户禁言已解除");
    }

    /**
     * 删除弹幕
     */
    @DeleteMapping("/{roomId}/manager/danmaku/{danmakuId}")
    public R<?> deleteDanmaku(@PathVariable Integer roomId, @PathVariable Integer danmakuId) {
        // TODO: 鉴权
        interactionService.deleteDanmaku(danmakuId);
        return R.ok("弹幕已删除");
    }

    // ================== 排行榜接口 ==================

    @GetMapping("/{roomId}/stats/leaderboard")
    public R<List<LeaderboardItemDTO>> getLeaderboard(@PathVariable Integer roomId,
                                                                   @RequestParam(defaultValue = "SESSION") String type) {
        return R.ok(interactionService.getLeaderboard(roomId, type));
    }


    /**
     * ★ 新增：根据当前登录主播获取/创建直播间
     * 前端只传 token，不传 roomId
     */
    @GetMapping("/my")
    public R<?> getMyRoom(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isBlank()) {
            return R.error(401, "缺少 Authorization 头");
        }
        UserInfoDTO user = userService.getUserByToken(authHeader);
        if (user == null) {
            return R.error(401, "无效的 token");
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
        dto.setCategory(room.getCategory());
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

        return R.ok(dto);
    }

    /**
     * 获取直播间推流信息 (仅管理员/主播可见)
     */
    @GetMapping("/{roomId}/manager/info")
    public R<RoomSettingsDTO> getRoomSettings(@PathVariable Integer roomId) {
        // TODO: 鉴权 (检查当前用户是否为该房间主播)
        
        Optional<LiveRoom> roomOpt = liveRoomRepository.findById(roomId);
        if (roomOpt.isEmpty()) {
            return R.error(404, "直播间不存在");
        }
        LiveRoom room = roomOpt.get();

        // Ensure stream key exists
        if (room.getStreamKey() == null || room.getStreamKey().isEmpty()) {
            room.setStreamKey("room_" + roomId + "_" + UUID.randomUUID().toString().substring(0, 8));
            liveRoomRepository.save(room);
        }
        
        RoomSettingsDTO dto = new RoomSettingsDTO();
        dto.setRoomId(room.getRoomId());
        dto.setTitle(room.getRoomTitle());
        dto.setStreamKey(room.getStreamKey());
        dto.setRtmpServer(room.getRtmpServer() != null ? room.getRtmpServer() : srsRtmpHost);
        dto.setCoverUrl(room.getThumbnailUrl());
        dto.setCategory(room.getCategory());
        dto.setDescription(room.getDescription());
        
        return R.ok(dto);
    }
}