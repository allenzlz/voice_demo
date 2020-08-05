package com.xiaoi.exp.voice.controller.flow;

import com.xiaoi.exp.voice.service.StatementService;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
    public JSONObject initRequest(@RequestParam("callphone") String callphone, @RequestParam("sessionId") String sessionId) {
        return statementService.initRequest(callphone, sessionId);
    }

    @PostMapping("second")
    public JSONObject secondRequest(@RequestParam("callphone") String callphone, @RequestParam("sessionId") String sessionId, @RequestParam("satisResults") String satisResults) {
        return statementService.updateInitRequest(callphone, sessionId, satisResults);
    }

    /**
     * 报表初始化数据
     *
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public JSONObject getList(@RequestParam("page") int page, @RequestParam("limit") int limit) {
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
}
