package com.xiaoi.exp.voice.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class AiResult<T> implements Serializable {
    private Integer code;
    private String message;
    private T data;
    private Integer count;

    public AiResult(Integer code, String message, T data) {
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public AiResult(Integer code, String message, T data, Integer count) {
        this.data = data;
        this.code = code;
        this.message = message;
        this.count = count;
    }
}
