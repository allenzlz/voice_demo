package com.xiaoi.exp.voice.controller;


import com.alibaba.fastjson.JSONObject;
import com.xiaoi.exp.voice.service.WxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class WxController {

    @Autowired
    WxService wxService;

    @GetMapping("getMediaUrl")
    public JSONObject getMediaUrl(@RequestParam("mediaId") String mediaId) {
        JSONObject resJson=new JSONObject();

        String mediaUrl=wxService.getMediaUrl(mediaId);

        resJson.put("mediaUrl",mediaUrl);

        return resJson;
    }



}
