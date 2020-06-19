package com.xiaoi.exp.voice.util;

import com.alibaba.fastjson.JSONObject;
import com.xiaoi.exp.voice.asr.FileTransRESTfulDemo;
import okhttp3.*;

import java.io.IOException;

public class AsrUtils {

    /** 常量字段，请勿修改 */
    private static final String KEY_APPKEY = "appkey";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_FILE_LINK = "file_link";
    private static final String KEY_HEADER = "header";
    private static final String KEY_PAYLOAD = "payload";
    private static final String KEY_STATUS_MESSAGE = "status_message";
    private static final String KEY_TASK_ID = "task_id";

    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_SUCCESS_WITH_NO_VALID_FRAGMENT = "SUCCE SS_WITH_NO_VALID_FRAGMENT";
    private static final String STATUS_RUNNING = "RUNNING";
    private static final String STATUS_QUEUEING = "QUEUEING";


    public static String submitFileTransRequest(String appkey,String token,String url, String fileLink) {
        /**
         * 设置HTTP POST请求
         * 1.使用HTTP协议
         * 2.录音文件识别服务域名：您的gateway的域名: ip:port
         * 3.录音文件识别请求路径：/stream/v1/filetrans
         * 4.设置必须请求参数：appkey、token、file_link
         * 5.可选请求参数：auto_split、enable_callback、callback_url等
         * */
        JSONObject taskObject = new JSONObject();
        taskObject.put(KEY_APPKEY, appkey);
        taskObject.put(KEY_FILE_LINK, fileLink);
        taskObject.put(KEY_TOKEN, token);
        String task = taskObject.toJSONString();
        System.out.println(task);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), task);
        Request request =
                new Request.Builder().url(url).header("Content-Type", "application/json").post(requestBody).build();

        String taskId = null;
        try {
            OkHttpClient client = new OkHttpClient();
            /**
             * 发送HTTP POST请求，处理服务端返回的响应
             * */
            Response response = client.newCall(request).execute();
            int responseCode = response.code();
            String responseBody = response.body().string();
            System.out.println("请求结果：" + responseBody);
            response.close();

            if (200 == responseCode) {
                JSONObject result = JSONObject.parseObject(responseBody);
                result = result.getJSONObject(KEY_HEADER);
                String statusMessage = result.getString(KEY_STATUS_MESSAGE);
                if (STATUS_SUCCESS.equals(statusMessage)) {
                    taskId = result.getString(KEY_TASK_ID);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return taskId;
    }

    public static String getFileTransResult(String appkey,String token,String url, String taskId) {
        /**
         *设置HTTP GET请求
         * 1 .使用HTTP协议
         * 2.录音文件识别服务域名：您的gateway的域名: ip:port
         * 3.录音文件识别请求路径：/stream/v1/filetrans
         * 4.设置必须请求参数：appkey、token、task_id
         * */
        url = url + "?appkey=" + appkey;
        url = url + "&token=" + token;
        url = url + "&task_id=" + taskId;

        System.out.println("url: " + url);

        Request request = new Request.Builder().url(url).get().build();

        String statusMessage = null;
        String result = null;
        OkHttpClient client = new OkHttpClient();
        while (true) {
            try {
                /**
                 * 发送HTTP GET请求，处理服务端返回的响应
                 */
                Response response = client.newCall(request).execute();
                int responseCode = response.code();
                String responseBody = response.body().string();
                System.out.println("识别查询结果：" + responseBody);
                response.close();

                if (200 != responseCode) {
                    break;
                }

                JSONObject rootObj = JSONObject.parseObject(responseBody);
                // 从header中获取状态信息
                JSONObject headerObj = rootObj.getJSONObject(KEY_HEADER);
                statusMessage = headerObj.getString(KEY_STATUS_MESSAGE);
                if (STATUS_RUNNING.equals(statusMessage) || STATUS_QUEUEING.equals(statusMessage)) {                     // 继续轮询，注意设置轮询时间间隔
                    Thread.sleep(3000);
                } else {
                    // 状态信息为成功，返回识别结果；状态信息为异常，返回空
                    if (STATUS_SUCCESS.equals(statusMessage) || STATUS_SUCCESS_WITH_NO_VALID_FRAGMENT.equals(statusMessage)) {
                        result = rootObj.getJSONObject
                                (KEY_PAYLOAD).toString();
                    }
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static void main(String args[]) {
        String ip="172.16.7.57";
        String port = "8101";

        String url = "http://" + ip + ":" + port + "/stream/v1/filetrans";
        String fileLink = "https://aliyun-nls.oss-cn-hangzhou.aliyuncs.com/asr/fileASR/examples/nls-sample-16k.wav";

        // 第一步：提交录音文件识别请求，获取任务ID用于后续的识别结果轮询
        String taskId = submitFileTransRequest("default","default",url, fileLink);
        if (taskId != null) {
            System.out.println("录音文件识别请求成功，task_id: " + taskId);
        } else {
            System.out.println("录音文件识别请求失败！");
            return;
        }

        // 第二步：根据任务ID轮询识别结果
        String result = getFileTransResult("default","default",url, taskId);
        if (result != null) {
            System.out.println("录音文件识别结果查询成功：" + result);
        } else {
            System.out.println("录音文件识别结果查询失败！");
        }
    }
}
