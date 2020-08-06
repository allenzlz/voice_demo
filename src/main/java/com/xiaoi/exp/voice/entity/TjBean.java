package com.xiaoi.exp.voice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Accessors(chain = true)
public class TjBean {
    private String statKey;
    private String my1;
    private String my2;
    private String my3;
    private String my4;
    private String my5;
    private int count;//全部的总数
    private int fcount;//完成调查的总数
    private String dczb;//调查占比
    private String mydzb;//满意度占比
}
