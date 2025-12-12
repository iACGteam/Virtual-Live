package com.virtuallive.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class R<T> {
    private Integer code;
    private String message;
    private T data;
    
    public static <T> R<T> ok(T data) {
        return R.<T>builder()
                .code(0)
                .message("success")
                .data(data)
                .build();
    }
    
    public static <T> R<T> ok() {
        return ok(null);
    }
    
    public static <T> R<T> ok(String message, T data) {
        return R.<T>builder()
                .code(0)
                .message(message)
                .data(data)
                .build();
    }
    
    public static <T> R<T> error(String message) {
        return R.<T>builder()
                .code(500)
                .message(message)
                .data(null)
                .build();
    }
    
    public static <T> R<T> error(Integer code, String message) {
        return R.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}
