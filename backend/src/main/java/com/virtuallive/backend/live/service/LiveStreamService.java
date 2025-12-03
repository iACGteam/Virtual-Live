package com.virtuallive.backend.live.service;

/**
 * 直播流服务接口
 */
public interface LiveStreamService {

    /**
     * 处理开播逻辑
     * @param streamKey 推流密钥
     * @return 是否允许推流
     */
    boolean startLive(String streamKey);

    /**
     * 处理关播逻辑
     * @param streamKey 推流密钥
     */
    void stopLive(String streamKey);
}