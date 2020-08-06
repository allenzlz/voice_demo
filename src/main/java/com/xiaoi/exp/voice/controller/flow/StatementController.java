package com.xiaoi.exp.voice.controller.flow;

import com.xiaoi.exp.voice.entity.AiResult;
import com.xiaoi.exp.voice.entity.Statement;
import com.xiaoi.exp.voice.entity.TjBean;
import com.xiaoi.exp.voice.service.StatementService;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

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
    public AiResult<String> initRequest(@RequestParam("callphone") String callphone, @RequestParam("sessionId") String sessionId) {
        return statementService.initRequest(callphone, sessionId);
    }

    @PostMapping("/second")
    public AiResult<String> secondRequest(@RequestParam("callphone") String callphone, @RequestParam("sessionId") String sessionId, @RequestParam("satisResults") String satisResults) {
        return statementService.updateInitRequest(callphone, sessionId, satisResults);
    }

    /**
     * 报表初始化数据
     *
     * @return
     */
    @GetMapping("/list")
    public AiResult<List<Statement>> getList(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return statementService.getList(page, limit);
    }

    /**
     * 报表跳转
     */
    @GetMapping("/st")
    public ModelAndView st() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("st");
        return mv;
    }

    /**
     * 统计跳转
     */
    @GetMapping("/tj")
    public ModelAndView tj() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("tj");
        return mv;
    }

    @GetMapping("/tjList")
    public AiResult<List<TjBean>> tjList() {
        return statementService.getTjResult();
    }

    @GetMapping("/all")
    public AiResult<List<Statement>> getAll() {
        return new AiResult<>(0, "123", statementService.getAll(), 13);
    }
}
