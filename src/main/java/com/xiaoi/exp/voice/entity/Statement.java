package com.xiaoi.exp.voice.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class Statement {
    private int id;
    private String sessionId;
    private String callphone;
    private String satisResults;
    private Date createTime;
}
