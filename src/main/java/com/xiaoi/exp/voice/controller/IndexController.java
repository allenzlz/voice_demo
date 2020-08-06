package com.xiaoi.exp.voice.controller;

import com.alibaba.fastjson.JSONObject;
import com.xiaoi.exp.voice.service.WxService;
import com.xiaoi.exp.voice.util.OkHttpUtils;
import com.xiaoi.exp.voice.util.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Controller
public class IndexController {

    @Autowired
    WxService wxService;
    @Value("${wx.appID}")
    private String appID;

    @GetMapping("index")
    public String index(ModelMap modelMap) {

        String accessToken = wxService.getAccessToken();

        String ticket = wxService.getJsapiTicket(accessToken);

        String noncestr = UUIDUtils.getUUID().substring(0, 16);
        long timestamp = System.currentTimeMillis() / 1000;
        String url = "http://t2c3399561.zicp.vip/dev/index";
        String jsapi_ticket = ticket;

        String signature = wxService.getSignature(noncestr, timestamp, url, jsapi_ticket);

        modelMap.addAttribute("appId", appID);
        modelMap.addAttribute("timestamp", timestamp);
        modelMap.addAttribute("nonceStr", noncestr);
        modelMap.addAttribute("signature", signature);

        return "index";
    }



    public static void main(String[] args) {
        System.out.println(new Date().getTime() / 1000);
    }
}
