package com.xiaoi.exp.voice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.eastrobot.robotdev.cache.Cache;
import com.xiaoi.exp.voice.entity.AiResult;
import com.xiaoi.exp.voice.entity.Statement;
import com.xiaoi.exp.voice.entity.TjBean;
import com.xiaoi.exp.voice.mapper.StatementMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StatementService {

    @Autowired
    private StatementMapper statementMapper;

    public List<Statement> getAll() {
        return statementMapper.selectList(null);
    }

    public AiResult<String> initRequest(String callphone, String sessionId) {
        int code = 1;
        String message = "succ";
        String data = "";
        try {
            Cache.put(sessionId, callphone, "-");
            Statement st = new Statement();
            st.setSessionId(sessionId);
            st.setCallPhone(callphone);
            st.setCreateTime(new Date());
            st.setStatKey(new Date());
            st.setSatisResults("-");//请求进来暂时记录"-"，如正常结束调查，调换"-"为真实的结果
            int insert = statementMapper.insert(st);
            log.info("初始化请求->插入数据：result=" + insert);
        } catch (Exception e) {
            e.printStackTrace();
            code = -1;
            message = e.getMessage();
        } finally {
            return new AiResult<>(code, message, data);
        }
    }

    public AiResult<String> updateInitRequest(String callphone, String sessionId, String satisResults) {
        int code = 1;
        String message = "succ";
        String data = "";
        try {
            Object o = Cache.get(sessionId, callphone);
            if (o != null) {
                Statement st = new Statement();
                st.setCreateTime(new Date());
                st.setStatKey(new Date());
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
            return new AiResult<>(code, message, data);
        }
    }

    public AiResult<List<Statement>> getList(int page, int limit) {
        try {
            int start = 0;
            page = page - 1;
            if (page < 0) {
                page = 0;
            }
            start = page * limit;
            List<Statement> statements = statementMapper.selectList(null);
            log.info(statements.get(0).getCreateTime().toString());
            return new AiResult<>(0, "succ", statementMapper.getList(start, limit), statementMapper.selectCount(null));
        } catch (Exception e) {
            e.printStackTrace();
            return new AiResult<>(-1, "failed", null, 0);
        }
    }

    public AiResult<List<TjBean>> getTjResult() {
        List<TjBean> tjList = new ArrayList<>();
        try {
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(2);
            List<Map> stCount = statementMapper.getStCount();
            TjBean tj = new TjBean();
            for (Map<Object, Object> map : stCount) {
                tj.setStatKey(map.get("stat_key").toString());
                String satis_results = map.get("satis_results").toString();
                String stcount = map.get("count").toString();
                if ("1".equals(satis_results)) {
                    tj.setMy1(stcount);
                }
                if ("2".equals(satis_results)) {
                    tj.setMy2(stcount);
                }
                if ("3".equals(satis_results)) {
                    tj.setMy3(stcount);
                }
                if ("4".equals(satis_results)) {
                    tj.setMy4(stcount);
                }
                if ("-".equals(satis_results)) {
                    tj.setMy5(stcount);
                }
                int count = statementMapper.selectCount(null);
                tj.setCount(count);//总数

                QueryWrapper<Statement> qw = new QueryWrapper<>();
                qw.ne("satis_results", "-");
                int fcount = statementMapper.selectCount(qw);
                tj.setFcount(fcount);//调查总数
                tj.setDczb(numberFormat.format((float) fcount / (float) count * 100) + "%");//调查完整的占比

                QueryWrapper<Statement> qw2 = new QueryWrapper<>();
                qw2.ne("satis_results", "-");
                qw2.ne("satis_results", "4");
                int mycount = statementMapper.selectCount(qw2);//满意度占比
                tj.setMydzb(numberFormat.format((float) mycount / (float) fcount * 100) + "%");

            }
            tjList.add(tj);
        } catch (Exception e) {
            e.printStackTrace();
            tjList = null;
        } finally {
            return new AiResult<>(0, "succc", tjList, 0);
        }
    }

    public static void main(String[] args) {
        int num1 = 7;

        int num2 = 9;

// 创建一个bai数值格式化du对象

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);

        String result = numberFormat.format((float) num1 / (float) num2 * 100);

        System.out.println("num1和num2的百zhi分比为:" + result + "%");
    }

}
