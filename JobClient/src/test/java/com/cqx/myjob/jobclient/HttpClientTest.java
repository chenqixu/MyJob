package com.cqx.myjob.jobclient;

import com.cqx.myjob.jobcomponent.bean.JobBean;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class HttpClientTest {

    private HttpClient httpClient;
    private String url;

    @Before
    public void setUp() throws Exception {
        httpClient = new HttpClient();
        url = "http://10.1.8.203:10091/jobservice/job/submit/";
    }

    @Test
    public void doGet() {
        JobBean jobBean = new JobBean();
        jobBean.setJob_name("ShareFileJob");
        jobBean.setJob_class("com.cqx.myjob.jobcomponent.impl.ShareFileJob");
        Map<String, String> param = new HashMap<>();
        jobBean.setJob_param(param);
        String data = jobBean.toString();
        data = data.replaceAll("\\{", "\\\\{");
        data = data.replaceAll("\\}", "\\\\}");
        data = data.replaceAll("\"", "\\\\\"");
        System.out.println(data);
    }

    @Test
    public void doPost() {
        JobBean jobBean = new JobBean();
        jobBean.setJob_id("10050");
        jobBean.setJob_name("ShareFileJob");
        jobBean.setJob_class("com.cqx.myjob.jobcomponent.impl.ShareFileJob");
        Map<String, String> param = new HashMap<>();
        jobBean.setJob_param(param);
        String data = jobBean.toString();
        System.out.println(data);
//        String result = httpClient.doPost(url, data);
//        System.out.println(result);
    }

    @Test
    public void doPut() {
    }
}