package com.cqx.myjob.jobclient;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * http工具类
 */
public class HttpClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
    private static final String GBK_CODE = "GBK";
    private static final String UTF8_CODE = "UTF-8";

    /**
     * get方式
     *
     * @param url
     * @return
     */
    public String doGet(String url) {
        Map<String, String> headerMap = new HashMap<>();
        final String[] result = {""};
        doSend(url, null, UTF8_CODE, HttpGet.class, headerMap, entity -> {
            try {
                // 通过EntityUtils中的toString方法将结果转换为字符串
                if (entity == null) return;
                result[0] = EntityUtils.toString(entity);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        });
        return result[0];
    }

    /**
     * post方式
     *
     * @param url
     * @param data
     * @return
     */
    public String doPost(String url, String data) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("BDOC-User", "admin");
        final String[] result = {""};
        doSend(url, data, UTF8_CODE, HttpPost.class, headerMap, entity -> {
            try {
                // 通过EntityUtils中的toString方法将结果转换为字符串
                if (entity == null) return;
                result[0] = EntityUtils.toString(entity);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        });
        return result[0];
    }

    /**
     * put方式
     *
     * @param url
     * @param data
     * @return
     */
    public String doPut(String url, String data) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("BDOC-User", "admin");
        final String[] result = {""};
        doSend(url, data, UTF8_CODE, HttpPut.class, headerMap, entity -> {
            try {
                // 通过EntityUtils中的toString方法将结果转换为字符串
                if (entity == null) return;
                result[0] = EntityUtils.toString(entity);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        });
        return result[0];
    }

    /**
     * 发送公共方法
     *
     * @param url
     * @param data
     * @param data_code
     * @param httpRequest
     * @param headerMap
     * @param iHttpEntityDeal
     */
    private void doSend(String url, String data, String data_code, Class<?> httpRequest,
                        Map<String, String> headerMap, IHttpEntityDeal iHttpEntityDeal) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpRequestBase httpRequestBase = null;
        try {
            // 通过址默认配置创建一个httpClient实例
            httpClient = HttpClients.createDefault();
            // 创建远程连接实例
            if (httpRequest.getName().equals(HttpGet.class.getName())) {
                httpRequestBase = new HttpGet(url);
            } else if (httpRequest.getName().equals(HttpPost.class.getName())) {
                httpRequestBase = new HttpPost(url);
                // 封装请求参数
                StringEntity httpEntity = new StringEntity(data, Charset.forName(data_code));
                ((HttpPost) httpRequestBase).setEntity(httpEntity);
            } else if (httpRequest.getName().equals(HttpPut.class.getName())) {
                httpRequestBase = new HttpPut(url);
                // 封装请求参数
                StringEntity httpEntity = new StringEntity(data, Charset.forName(data_code));
                ((HttpPut) httpRequestBase).setEntity(httpEntity);
            }
            // 取出所有的头信息
            Header[] allHeaderArr = httpRequestBase.getAllHeaders();
            Map<String, String> allHeaderMap = new HashMap<>();
            for (Header header : allHeaderArr) {
                allHeaderMap.put(header.getName(), header.getValue());
            }
            // 设置请求头信息，鉴权
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                if (allHeaderMap.get(entry.getKey()) != null) {
                    httpRequestBase.setHeader(entry.getKey(), entry.getValue());
                } else {
                    httpRequestBase.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 设置配置请求参数
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                    .setConnectionRequestTimeout(35000)// 请求超时时间
                    .setSocketTimeout(60000)// 数据读取超时时间
                    .build();
            // 为实例设置配置
            httpRequestBase.setConfig(requestConfig);
            // 执行请求得到返回对象
            response = httpClient.execute(httpRequestBase);
            // 通过返回对象获取返回数据
            HttpEntity entity = response.getEntity();
            // 通过公共接口对entity进行处理
            iHttpEntityDeal.deal(entity);
        } catch (ClientProtocolException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            // 关闭资源
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}