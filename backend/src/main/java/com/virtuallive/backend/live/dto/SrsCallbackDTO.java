package com.virtuallive.backend.live.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SrsCallbackDTO {
    // SRS 回调时的固定字段
    private String action; // on_publish 或 on_unpublish
    private String ip;
    private String vhost;
    private String app;

    // SRS 传过来的 key 叫 "stream"，我们要映射成 streamKey
    @JsonProperty("stream")
    private String streamKey;

    @JsonProperty("client_id")
    private String clientId;
}