package com.xiaoi.exp.voice.controller;

import com.alibaba.fastjson.JSONObject;
import com.xiaoi.exp.voice.util.OkHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AskServiceController {

    @Value("${ask.url}")
    private String askUrl;

    @RequestMapping("ask")
    public JSONObject ask(@RequestParam("userId") String userId, @RequestParam("platform") String platform,
                          @RequestParam("question") String question) {

        JSONObject jsonObject=new JSONObject();
        String result =
                OkHttpUtils.get(askUrl + "?userId=" + userId + "&platform=" + platform + "&question=" + question +
                        "&format" +
                "=json");
        JSONObject resultJson=JSONObject.parseObject(result);
        jsonObject.put("content",resultJson.getString("content"));
        return jsonObject;
    }
}
