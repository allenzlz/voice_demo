package com.xiaoi.exp.voice.service;


import com.alibaba.fastjson.JSONObject;
import com.eastrobot.robotdev.cache.Cache;
import com.xiaoi.exp.voice.util.OkHttpUtils;
import com.xiaoi.exp.voice.util.UUIDUtils;
import com.xiaoi.exp.voice.util.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Slf4j
@Service
public class WxService {

    @Value("${wx.appID}")
    private String appID;
    @Value("${wx.appsecret}")
    private String appsecret;


    public String getAccessToken() {
        String accessToken = Cache.get("wx", "accessToken") == null ? "" : String.valueOf(Cache.get("wx",
                "accessToken"));
        if (ValidationUtils.isEmpty(accessToken)) {
            String accessTokenStr = OkHttpUtils.get("https://api.weixin.qq" +
                    ".com/cgi-bin/token?grant_type=client_credential" +
                    "&appid" +
                    "=" + appID + "&secret=" + appsecret);
            log.info("accessTokenStr:" + accessTokenStr);
            JSONObject accessTokenJson = JSONObject.parseObject(accessTokenStr);
            accessToken = accessTokenJson.getString("access_token");
            log.info("get accessToken from wx:" + accessToken);
            Cache.put("wx", "accessToken", accessToken);
        }
        return accessToken;
    }

    public String getJsapiTicket(String accessToken) {
        String ticket = Cache.get("wx", "jsapiTicket") == null ? "" : String.valueOf(Cache.get("wx", "jsapiTicket"));
        if (ValidationUtils.isEmpty(ticket)) {
            String jsapiTicketStr = OkHttpUtils.get("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="
                    + accessToken + "&type=jsapi");
            JSONObject jsapiTicketJson = JSONObject.parseObject(jsapiTicketStr);
            ticket = jsapiTicketJson.getString("ticket");
            log.info("ticket:" + ticket);
            Cache.put("wx", "jsapiTicket", ticket);
        }
        return ticket;

    }

    public String getSignature(String noncestr, long timestamp, String url, String jsapi_ticket) {

        String[] params = new String[]{"noncestr=" + noncestr, "jsapi_ticket=" + jsapi_ticket, "timestamp=" + timestamp,
                "url=" + url};
        Arrays.sort(params);

        String signatureStr = params[0] + "&" + params[1] + "&" + params[2] + "&" + params[3];

        log.info(signatureStr);

        String signature = DigestUtils.shaHex(signatureStr);

        log.info(signature);

        return signature;
    }

    public String getMediaUrl(String mediaId) {
        String accessToken = getAccessToken();
        InputStream mediaInputStream =
                OkHttpUtils.getInputStream("https://api.weixin.qq.com/cgi-bin/media/get?access_token=" + accessToken +
                        "&media_id=" + mediaId);

        String mediaName = UUIDUtils.getUUID();
        writeToLocal("D:\\work\\apache-tomcat-8.5.45-windows-x64\\apache-tomcat-8.5.45\\webapps\\ROOT\\voice_files\\" + mediaName+ ".amr",
                mediaInputStream);
        try {
            Runtime rt = Runtime.getRuntime();
            Process p = null;
            p = rt.exec("cmd.exe /c D:\\ffmpeg-20200802-b48397e-win64-shared\\bin\\ffmpeg.exe -i " +
                    "D:\\work\\apache-tomcat-8.5.45-windows-x64\\apache-tomcat-8.5" +
                    ".45\\webapps\\ROOT\\voice_files\\"+mediaName+".amr " +
                    "D:\\work\\apache-tomcat-8.5.45-windows-x64\\apache-tomcat-8.5" +
                    ".45\\webapps\\ROOT\\voice_files\\"+mediaName+".mp3");
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("*****************"+mediaName);
        return "http://xiaojk123.51vip.biz/voice_files/"+mediaName+".mp3";
    }


    /**
     * 将InputStream写入本地文件
     *
     * @param destination 写入本地目录
     * @param input       输入流
     * @throws IOException IOException
     */
    public static void writeToLocal(String destination, InputStream input) {
        int index = -1;
        FileOutputStream downloadFile = null;
        try {
            byte[] bytes = new byte[1024];
            downloadFile = new FileOutputStream(destination);
            while ((index = input.read(bytes)) != -1) {
                downloadFile.write(bytes, 0, index);
                downloadFile.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                downloadFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process p = null;
            p = rt.exec("cmd.exe /c D:\\ffmpeg-20200802-b48397e-win64-shared\\bin\\ffmpeg.exe -i " +
                    "D:\\work\\apache-tomcat-8.5.45-windows-x64\\apache-tomcat-8.5" +
                    ".45\\webapps\\ROOT\\voice_files\\9086abce260946859755a7791bdce9da.amr " +
                    "D:\\work\\apache-tomcat-8.5.45-windows-x64\\apache-tomcat-8.5" +
                    ".45\\webapps\\ROOT\\voice_files\\9086abce260946859755a7791bdce9da.mp3");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
