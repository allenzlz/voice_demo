package com.xiaoi.exp.voice.service;

import com.alibaba.fastjson.JSONObject;
import com.xiaoi.exp.voice.util.HttpClientUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;


@Service
public class AsrService {
    @Value("${asr.url}")
    private String asrUrl;

    /**
     * @param file
     * @param fileType
     * @return
     */
    public JSONObject asr(MultipartFile file, String fileType) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("name", fileType);
            String res = HttpClientUtils.httpPostRequest2(asrUrl, file, file.getName(), params, 20000);
            JSONObject jsonObject = JSONObject.parseObject(res);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
