package com.xiaoi.exp.voice.controller;

import com.alibaba.fastjson.JSONObject;
import com.xiaoi.exp.voice.service.AsrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
public class IndexController {
    @GetMapping("index")
    public String index() {
        return "index";
    }
}
