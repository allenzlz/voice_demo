package com.xiaoi.exp.voice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("satisfaction")
public class Statement implements Serializable {

    private static final long serialVersionUID = -1L;

    private int id;
    private String sessionId;
    private String callPhone;
    private String satisResults;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")//get
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")//set
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")//get
    @DateTimeFormat(pattern = "yyyy-MM-dd")//set
    private Date statKey;
}
