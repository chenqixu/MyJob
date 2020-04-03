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

        //10050
        jobBean.setJob_id("10050");
        jobBean.setJob_name("ShareFileJob");
        jobBean.setJob_class("com.cqx.myjob.jobcomponent.impl.ShareFileJob");
        Map<String, String> param = new HashMap<>();
        param.put("hadoop_conf", "/cmss/bch/bc/hadoop/etc/hadoop75/");
        param.put("scan_path", "/cqz/mccdr/sum_date=20200326");
        param.put("local_path", "/bi/user/cqx/data/mccdr/135.raf");
        jobBean.setJob_param(param);
        System.out.println(jobBean.toString());

        //10051
        jobBean.setJob_id("10051");
        jobBean.setJob_name("InitRAFJob");
        jobBean.setJob_class("com.cqx.myjob.jobcomponent.impl.InitRAFJob");
        param = new HashMap<>();
        param.put("path", "/bi/user/cqx/data/mccdr/135.raf");
        param.put("start_key", "0");
        param.put("end_key", "9999999");
        jobBean.setJob_param(param);
        System.out.println(jobBean.toString());

        //10052
        jobBean.setJob_id("10052");
        jobBean.setJob_name("RAFReadJob");
        jobBean.setJob_class("com.cqx.myjob.jobcomponent.impl.RAFReadJob");
        param = new HashMap<>();
        param.put("hadoop_conf", "/cmss/bch/bc/hadoop/etc/hadoop75/");
        param.put("scan_path", "/cqz/mccdr/sum_date=20200326");
        param.put("local_path", "/bi/user/cqx/data/mccdr/135.raf");
        param.put("cnt", "100");
        jobBean.setJob_param(param);
        System.out.println(jobBean.toString());

        //10053
        jobBean.setJob_id("10053");
        jobBean.setJob_name("ModRAFWriteJob");
        jobBean.setJob_class("com.cqx.myjob.jobcomponent.impl.ModRAFWriteJob");
        param = new HashMap<>();
        param.put("hadoop_conf", "/cmss/bch/bc/hadoop/etc/hadoop75/");
        param.put("scan_path", "/cqz/mccdr/sum_date=20200326");
        param.put("local_raf_path", "/bi/user/cqx/data/mccdr/local.raf");
        param.put("local_map_path", "/bi/user/cqx/data/mccdr/local.map");
        jobBean.setJob_param(param);
        System.out.println(jobBean.toString());

        //10054
        jobBean.setJob_id("10054");
        jobBean.setJob_name("ModRAFReadJob");
        jobBean.setJob_class("com.cqx.myjob.jobcomponent.impl.ModRAFReadJob");
        param = new HashMap<>();
        param.put("local_raf_path", "/bi/user/cqx/data/mccdr/local.raf");
        param.put("local_map_path", "/bi/user/cqx/data/mccdr/local.map");
        jobBean.setJob_param(param);
        System.out.println(jobBean.toString());

//        String result = httpClient.doPost(url, data);
//        System.out.println(result);
    }

    @Test
    public void doPut() {
        String msisdn = "1064767893910";
        long all_cnt = 50979751;
        long mod = Long.valueOf(msisdn) % all_cnt;
        long mod_hash = Math.abs(msisdn.hashCode()) % all_cnt;
        System.out.println("mod：" + mod + "，mod_hash：" + mod_hash + "，hash_code：" + Math.abs(msisdn.hashCode()));
    }
}