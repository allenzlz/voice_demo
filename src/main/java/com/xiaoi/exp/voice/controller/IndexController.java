package com.xiaoi.exp.voice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class IndexController {
    @Value("${tts.param.url}")
    private String ttsParamUrl;

    @GetMapping("index")
    public String index() {
        return "index";
    }
    @GetMapping("tts")
    public String tts(ModelMap modelMap) {
        modelMap.addAttribute("ttsUrl",ttsParamUrl);
        return "tts";
    }
}
