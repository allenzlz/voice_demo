package com.xiaoi.exp.voice.service;

import com.eastrobot.robotdev.cache.Cache;
import com.xiaoi.exp.voice.entity.Statement;
import com.xiaoi.exp.voice.mapper.StatementMapper;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatementService {

    @Autowired
    private StatementMapper statementMapper;

    public String initRequest(String callphone, String sessionId) {
        try {
            Object o = Cache.get(callphone, sessionId);
            if (null == o) {
                Cache.put(callphone, sessionId, "-");
                List<Statement> list = statementMapper.getList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return null;
        }
    }

    public String getList() {
        try {
            return getResp(1, "succ", statementMapper.getList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getResp(int code, String message, Object data) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("message", message);
        json.put("data", data);
        return json.toString();
    }
}
