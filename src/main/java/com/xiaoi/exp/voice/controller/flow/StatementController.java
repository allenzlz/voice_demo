package com.xiaoi.exp.voice.controller.flow;

import com.eastrobot.robotdev.cache.Cache;
import com.xiaoi.exp.voice.service.StatementService;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class StatementController {

    @Autowired
    private StatementService statementService;

    /**
     * 初始化请求
     *
     * @param callphone
     * @param sessionId
     * @return
     */
    @PostMapping("/init")
    public String initRequest(@RequestParam("callphone") String callphone, @RequestParam("sessionId") String sessionId) {
        return statementService.initRequest(callphone, sessionId);
    }

    /**
     * 报表初始化数据
     *
     * @return
     */
    @GetMapping("/list")
    public String getList() {
        return statementService.getList();
    }
}
