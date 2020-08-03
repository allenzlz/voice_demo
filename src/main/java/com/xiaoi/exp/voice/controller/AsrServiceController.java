package com.xiaoi.exp.voice.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoi.exp.voice.util.AsrUtils;
import com.xiaoi.exp.voice.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
public class AsrServiceController {

    @Value("${voice.ip}")
    private String ip;
    @Value("${voice.port}")
    private String port;
    @Value("${voice.appkey}")
    private String appkey;
    @Value("${voice.token}")
    private String token;
    @Value("${ask.url}")
    private String askUrl;
    @Value("${voice.filePath}")
    private String filePath;
    @Value("${voice.httpPath}")
    private String httpPath;
    @Value("${tts.url}")
    private String ttsUrl;

    @RequestMapping("getAsr")
    public JSONObject getAsr(@RequestParam("file") MultipartFile file, @RequestParam("userid") String userid) {
        //返回结果
        JSONObject jsonObject = new JSONObject();
        //语音识别地址
        String asrUrl = "http://" + ip + ":" + port + "/stream/v1/filetrans";
        //语音文件名称
        String fileName = getUUID() + ".wav";
        //语音文件存放路径
        File dest = new File(filePath);
        //判断文件是否已经存在
        if (!dest.exists()) {
            dest.mkdirs();
        }
        //设置语音文件的服务器存放全地址
        File destReal = new File(filePath + fileName);
        //生成语音文件放到指定的目录下
        try {
            file.transferTo(destReal);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         * 语音文件http访问地址
         * 示例：String fileLink = "https://aliyun-nls.oss-cn-hangzhou.aliyuncs.com/asr/fileASR/examples/nls-sample-16k
         * .wav";
         */
        //String fileLink = httpPath + fileName;
        // 第一步：提交录音文件识别请求，获取任务ID用于后续的识别结果轮询
        String taskId = getTaskId(asrUrl, fileName);
        if (taskId == null) {
            jsonObject.put("question", "录音文件识别请求失败！");
        } else {
            // 第二步：根据任务ID轮询识别结果
            String asrResult = getAsrResult(asrUrl, taskId);
            //对语音识别结果进行语义识别
            String askParamsUrl = askUrl + asrResult + "&format=json&userid=" + userid;
            String askResult = HttpUtils.get(askParamsUrl);
            JSONObject askJson = JSONObject.parseObject(askResult);
            jsonObject.put("question", asrResult);
            jsonObject.put("answer", askJson.getString("content"));
            jsonObject.put("ttsUrl", ttsUrl);
        }
        //使用完语音文件，清除本地的文件，释放磁盘
        boolean destRealDelFlag = destReal.delete();
        if (destRealDelFlag) {
            log.info("*****************fileName:" + fileName + "delete success...");
        } else {
            log.info("*****************fileName:" + fileName + "delete failure...");
        }
        //返回结果
        jsonObject.put("result", "success");
        return jsonObject;
    }

    @RequestMapping("getAsrByName")
    public JSONObject getAsrByName(@RequestParam("fileName") String fileName) {
        //返回结果
        JSONObject jsonObject = new JSONObject();
        //语音识别地址
        String asrUrl = "http://" + ip + ":" + port + "/stream/v1/filetrans";
        // 第一步：提交录音文件识别请求，获取任务ID用于后续的识别结果轮询
        String taskId = getTaskId(asrUrl, fileName);
        if (taskId == null) {
            jsonObject.put("result", "录音文件识别请求失败！");
        } else {
            // 第二步：根据任务ID轮询识别结果
            String asrResult = getAsrResult(asrUrl, taskId);
            jsonObject.put("result", asrResult);
        }
        return jsonObject;
    }

    public String getAsrResult(String asrUrl, String taskId) {
        String asrResult = AsrUtils.getFileTransResult(appkey, token, asrUrl, taskId);
        if (asrResult != null) {
            log.info("录音文件识别结果查询成功：" + asrResult);
            JSONObject asrObj = JSONObject.parseObject(asrResult);
            if (asrObj != null) {
                JSONArray sentencesJsonArr = asrObj.getJSONArray("sentences");
                if (sentencesJsonArr != null && sentencesJsonArr.size() > 0) {
                    JSONObject jsonObject0 = sentencesJsonArr.getJSONObject(0);
                    if (jsonObject0 != null) {
                        Object textObj = jsonObject0.get("text");
                        if (textObj != null) {
                            return String.valueOf(textObj);
                        }
                    }
                }
            }
            return "录音文件识别结果为空";
        } else {
            log.info("录音文件识别结果查询失败！");
            return "录音文件识别结果查询失败！";
        }
    }

    public String getTaskId(String asrUrl, String fileName) {
        // 第一步：提交录音文件识别请求，获取任务ID用于后续的识别结果轮询
        String taskId = AsrUtils.submitFileTransRequest(appkey, token, asrUrl, httpPath + fileName);
        if (taskId != null) {
            log.info("录音文件识别请求成功，task_id: " + taskId);
            return taskId;
        } else {
            log.info("录音文件识别请求失败！");
            return null;
        }
    }

    /**
     * 生成32位UUID随机数
     *
     * @return
     */
    public String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
