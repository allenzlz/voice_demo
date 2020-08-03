package com.xiaoi.exp.voice.controller;

import com.alibaba.fastjson.JSONObject;
import com.xiaoi.exp.voice.service.AsrService;
import com.xiaoi.exp.voice.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class AsrController {
    @Autowired
    private AsrService asrService;

    @Value("${ask.url}")
    private String askUrl;

    @Value("${tts.url}")
    private String ttsUrl;

    @PostMapping("asr")
    public JSONObject asr(@RequestParam("file") MultipartFile file, @RequestParam("userid") String userid) {
        log.info("录音文件大小：" + file.getSize());
        JSONObject jsonObject = asrService.asr(file, "16k-cn");
//        String question = jsonObject.getString("info");
//        if (StringUtils.isNotBlank(question)) {
//            String url = askUrl + question + "&format=json&userid=" + userid;
//            String resJSON = HttpUtils.get(url);
//            JSONObject jsonb = JSONObject.parseObject(resJSON);
//            jsonObject.put("answer", jsonb.getString("content"));
//            jsonObject.put("ttsUrl", ttsUrl);
//        }
//        log.info("问题：" + jsonObject.getString("info") + "\n" + jsonObject.getString("answer"));
        return jsonObject;
    }
}
