package com.xiaoi.exp.voice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class TtsController {

    @Value("${tts.param.url}")
    private String ttsParamUrl;

    @GetMapping("tts")
    public String tts(ModelMap modelMap) {
        modelMap.addAttribute("ttsUrl",ttsParamUrl);
        return "tts";
    }
}
