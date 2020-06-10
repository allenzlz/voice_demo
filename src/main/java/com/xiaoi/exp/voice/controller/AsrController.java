package com.xiaoi.exp.voice.controller;

import com.alibaba.fastjson.JSONObject;
import com.xiaoi.exp.voice.service.AsrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class AsrController {
    @Autowired
    private AsrService asrService;

    @PostMapping("asr")
    public JSONObject asr(@RequestParam("file") MultipartFile file) {
        JSONObject jsonObject = asrService.asr(file, "16k-cn");
        return jsonObject;
    }
}
