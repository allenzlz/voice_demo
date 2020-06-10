package com.xiaoi.exp.voice.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: alan.peng
 * @Date: 2020/4/2 16:16
 * @Description:
 */
@Slf4j
public class HttpClientUtils {

    private static PoolingHttpClientConnectionManager gcm = null;

    private static CloseableHttpClient httpClient = null;

    private static IdleConnectionMonitorThread idleThread = null;

    /**
     * 连接池的最大连接数
     */
    private final static int maxTotal = 200;
    /**
     * 连接池按route配置的最大连接数
     */
    private final static int maxPerRoute = 200;

    /**
     * tcp connect的超时时间
     */
    private final static int connectTimeout = 500;
    /**
     * 从连接池获取连接的超时时间
     */
    private final static int connectRequestTimeout = 500;
    /**
     * tcp io的读写超时时间
     */
    private final static int socketTimeout = 2000;


    static {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();

        gcm = new PoolingHttpClientConnectionManager(registry);
        gcm.setMaxTotal(maxTotal);
        gcm.setDefaultMaxPerRoute(maxPerRoute);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeout)                     // 设置连接超时
                .setSocketTimeout(socketTimeout)                       // 设置读取超时
                .setConnectionRequestTimeout(connectRequestTimeout)    // 设置从连接池获取连接实例的超时
                .build();

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClient = httpClientBuilder
                .setConnectionManager(gcm)
                .setDefaultRequestConfig(requestConfig)
                .build();

        idleThread = new IdleConnectionMonitorThread(gcm);
        idleThread.start();
    }

    public static String doGet(String url) {
        return doGet(url, Collections.EMPTY_MAP, Collections.EMPTY_MAP);
    }

    public static String doGet(String url, Map<String, Object> params) {
        return doGet(url, Collections.EMPTY_MAP, params);
    }

    public static String doGet(String url,
                               Map<String, String> headers,
                               Map<String, Object> params
    ) {

        // *) 构建GET请求头
        String apiUrl = getUrlWithParams(url, params);
        HttpGet httpGet = new HttpGet(apiUrl);

        // *) 设置header信息
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response == null || response.getStatusLine() == null) {
                return null;
            }

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entityRes = response.getEntity();
                if (entityRes != null) {
                    return EntityUtils.toString(entityRes, "UTF-8");
                }
            }
            return null;
        } catch (IOException e) {
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public static String doPost(String apiUrl, Map<String, Object> params) {
        return doPost(apiUrl, Collections.EMPTY_MAP, params);
    }

    public static String doPost(String apiUrl,
                                Map<String, String> headers,
                                Map<String, Object> params
    ) {

        HttpPost httpPost = new HttpPost(apiUrl);

        // *) 配置请求headers
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // *) 配置请求参数
        if (params != null && params.size() > 0) {
            HttpEntity entityReq = getUrlEncodedFormEntity(params);
            httpPost.setEntity(entityReq);
        }
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            if (response == null || response.getStatusLine() == null) {
                return null;
            }
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entityRes = response.getEntity();
                if (entityRes != null) {
                    String result = EntityUtils.toString(entityRes, "UTF-8");
                    log.debug("请求URL=>{}，返回结果=>{}", apiUrl, result);
                    return result;
                }
            }
            return null;
        } catch (IOException e) {
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public static String doPostBody(String apiUrl, String postBody) {
        return doPostBody(apiUrl, Collections.EMPTY_MAP, postBody);
    }

    public static String doPostBody(String apiUrl, Map<String, String> headers, String postBody) {
        HttpPost httpPost = new HttpPost(apiUrl);
        // *) 配置请求headers
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // *) 配置请求参数
        if (StringUtils.isNotBlank(postBody)) {
            HttpEntity entityReq = getPostBody(postBody);
            httpPost.setEntity(entityReq);
        }
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            if (response == null || response.getStatusLine() == null) {
                return null;
            }
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entityRes = response.getEntity();
                if (entityRes != null) {
                    String result = EntityUtils.toString(entityRes, "UTF-8");
                    log.debug("请求URL=>{}，返回结果=>{}", apiUrl, result);
                    return result;
                }
            }
            return null;
        } catch (IOException e) {
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }


    /**
     * 发送文件对象到服务端
     *
     * @param file
     * @param url
     * @param params
     * @param charset
     * @return
     */
    public static String postFile(File file, String url, Map<String, String> params, String charset) {

        if (StringUtils.isBlank(url)) {
            return null;
        }
        CloseableHttpResponse response = null;
        String result = null;
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }
            HttpPost httpPost = new HttpPost(url);
            //将java.io.File对象添加到HttpEntity（org.apache.http.HttpEntity）对象中
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            //解决上传文件，文件名中文乱码问题
            builder.setCharset(Charset.forName("utf-8"));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//设置浏览器兼容模式
//             builder.addBinaryBody("file", file, ContentType.create("multipart/form-data"), "file");
            builder.addPart("file", new FileBody(file));
            httpPost.setEntity(builder.build());
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = response.getEntity();

                // 消耗掉response
                EntityUtils.consume(resEntity);
                result = EntityUtils.toString(resEntity);
                log.debug("请求URL=>{}，返回结果=>{}", url, result);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 使用HttpClient4.5 post提交multipart/form-data数据实现多文件上传
     *
     * @param url         请求地址
     * @param fileParName fileKey
     * @param params      附带的文本参数
     * @param timeout     请求超时时间(毫秒)
     * @return
     * @author alexli
     * @date 2018年5月8日 上午10:26:15
     */
    public static Map<String, String> httpPostRequest2(String url, File file, String fileParName,
                                                       Map<String, Object> params, int timeout) {
        Map<String, String> resultMap = new HashMap<>();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Charset.forName("UTF-8"));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            String fileName = null;
            builder.addBinaryBody(fileParName, file, ContentType.MULTIPART_FORM_DATA, fileName);// 文件流
            //决中文乱码
            ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() == null)
                    continue;
                // 类似浏览器表单提交，对应input的name和value
                builder.addTextBody(entry.getKey(), entry.getValue().toString(), contentType);
            }
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);// 执行提交

            // 设置连接超时时间
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build();
            httpPost.setConfig(requestConfig);

            HttpEntity responseEntity = response.getEntity();
            resultMap.put("scode", String.valueOf(response.getStatusLine().getStatusCode()));
            resultMap.put("data", "");
            if (responseEntity != null) {
                // 将响应内容转换为字符串
                result = EntityUtils.toString(responseEntity);
                resultMap.put("data", result);
            }
        } catch (Exception e) {
            resultMap.put("scode", "error");
            resultMap.put("data", "HTTP请求出现异常: " + e.getMessage());

            Writer w = new StringWriter();
            e.printStackTrace(new PrintWriter(w));
            log.error("HTTP请求出现异常: " + w.toString());
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultMap;
    }


    /**
     * 使用HttpClient4.5 post提交multipart/form-data数据实现多文件上传
     *
     * @param url           请求地址
     * @param multipartFile post提交的文件
     * @param fileParName   fileKey
     * @param params        附带的文本参数
     * @param timeout       请求超时时间(毫秒)
     * @return
     */
    public static String httpPostRequest2(String url, MultipartFile multipartFile, String fileParName,
                                          Map<String, Object> params, int timeout) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Charset.forName("UTF-8"));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            String fileName = null;
            fileName = multipartFile.getOriginalFilename();
            builder.addBinaryBody(fileParName, multipartFile.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);// 文件流
            //决中文乱码
            ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() == null)
                    continue;
                // 类似浏览器表单提交，对应input的name和value
                builder.addTextBody(entry.getKey(), entry.getValue().toString(), contentType);
            }
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);// 执行提交

            // 设置连接超时时间
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build();
            httpPost.setConfig(requestConfig);

            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                // 将响应内容转换为字符串
                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
                log.debug("请求URL=>{}，返回结果=>{}", url, result);
                return result;

            }
        } catch (Exception e) {
            Writer w = new StringWriter();
            e.printStackTrace(new PrintWriter(w));
            log.error("HTTP请求出现异常: " + w.toString());
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static HttpEntity getPostBody(String postBody) {
        HttpEntity httpEntity = null;
        try {
            httpEntity = new StringEntity(postBody);
            return httpEntity;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }
        return httpEntity;
    }

    private static HttpEntity getUrlEncodedFormEntity(Map<String, Object> params) {
        List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry
                    .getValue().toString());
            pairList.add(pair);
        }
        return new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8"));
    }

    private static String getUrlWithParams(String url, Map<String, Object> params) {
        boolean first = true;
        StringBuilder sb = new StringBuilder(url);
        for (String key : params.keySet()) {
            char ch = '&';
            if (first == true) {
                ch = '?';
                first = false;
            }
            String value = params.get(key).toString();
            try {
                String sval = URLEncoder.encode(value, "UTF-8");
                sb.append(ch).append(key).append("=").append(sval);
            } catch (UnsupportedEncodingException e) {
            }
        }
        return sb.toString();
    }


    public void shutdown() {
        idleThread.shutdown();
    }

    // 监控有异常的链接
    private static class IdleConnectionMonitorThread extends Thread {

        private final HttpClientConnectionManager connMgr;
        private volatile boolean exitFlag = false;

        public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
            this.connMgr = connMgr;
            setDaemon(true);
        }

        @Override
        public void run() {
            while (!this.exitFlag) {
                synchronized (this) {
                    try {
                        this.wait(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 关闭失效的连接
                connMgr.closeExpiredConnections();
                // 可选的, 关闭30秒内不活动的连接
                connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
            }
        }

        public void shutdown() {
            this.exitFlag = true;
            synchronized (this) {
                notify();
            }
        }

    }
}
