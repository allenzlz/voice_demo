package com.xiaoi.exp.voice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.eastrobot.robotdev.cache.Cache;
import com.xiaoi.exp.voice.entity.Statement;
import com.xiaoi.exp.voice.mapper.StatementMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class StatementService {

    @Autowired
    private StatementMapper statementMapper;

    public JSONObject initRequest(String callphone, String sessionId) {
        int code = 1;
        String message = "succ";
        String data = "";
        try {
            Cache.put(sessionId, callphone, "-");
            Statement st = new Statement();
            st.setSessionId(sessionId);
            st.setCallPhone(callphone);
            st.setCreateTime(new Date());
            st.setSatisResults("-");//请求进来暂时记录"-"，如正常结束调查，调换"-"为真实的结果
            int insert = statementMapper.insert(st);
            log.info("初始化请求->插入数据：result=" + insert);
        } catch (Exception e) {
            e.printStackTrace();
            code = -1;
            message = e.getMessage();
        } finally {
            return getResp(code, message, data);
        }
    }

    public JSONObject updateInitRequest(String callphone, String sessionId, String satisResults) {
        int code = 1;
        String message = "succ";
        String data = "";
        try {
            Object o = Cache.get(sessionId, callphone);
            if (o != null) {
                Statement st = new Statement();
                st.setCreateTime(new Date());
                st.setSatisResults(satisResults);
                UpdateWrapper<Statement> uw = new UpdateWrapper<Statement>();
                uw.eq("session_id", sessionId);
                int update = statementMapper.update(st, uw);
                log.info("二次请求->更新数据：result=" + update);
                Cache.remove(sessionId, callphone);
            } else {
                code = -1;
                message = "二次请求再第一次请求中没有找到对应的缓存";
            }
        } catch (Exception e) {
            e.printStackTrace();
            code = -1;
            message = e.getMessage();
        } finally {
            return getResp(code, message, data);
        }
    }

    public JSONObject getList(int page, int limit) {
        try {
            int start = 0;
            page = page - 1;
            if (page < 0) {
                page = 0;
            }
            start = page * limit;
            return getResp(0, "succ", statementMapper.getList(start, limit), statementMapper.selectCount(null));
        } catch (Exception e) {
            e.printStackTrace();
            return getResp(0, "failed", null);
        }
    }

    private JSONObject getResp(int code, String message, Object data) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("message", message);
        json.put("data", data);
        return json;
    }

    private JSONObject getResp(int code, String message, Object data, int count) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("message", message);
        json.put("data", data);
        json.put("count", count);
        return json;
    }
}
