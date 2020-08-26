package com.cqx.myjob.jobcomponent.bean;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class JobBeanTest {

    @Test
    public void do1() {
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
    public void do2() {
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
//        param.put("local_path", "/bi/user/cqx/data/mccdr/135.raf");
        param.put("cnt", "100");
        param.put("local_index_path", "/bi/user/cqx/data/mccdr/local.index");
        param.put("local_map_read_path", "/bi/user/cqx/data/mccdr/local.read.map");
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
        param.put("local_map_read_path", "/bi/user/cqx/data/mccdr/local.read.map");
        param.put("local_map_write_path", "/bi/user/cqx/data/mccdr/local.write.map");
        param.put("deal_cnt", "2");
        param.put("local_index_path", "/bi/user/cqx/data/mccdr/local.index");
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

        //10056
        jobBean.setJob_id("10056");
        jobBean.setJob_name("SplitPackageJob");
        jobBean.setJob_class("com.cqx.myjob.jobcomponent.impl.SplitPackageJob");
        param = new HashMap<>();
        param.put("local_bak_path", "/bi/user/cqx/data/hblog/if_upload_hb_netlog/${run_date}/");
        param.put("extension", "01-${device_id}-${seq}-${file_start_time}-${file_end_time}-${record_count}-${md5}-${file_size}.txt.gz");
        param.put("max_line", "10000");
        param.put("hadoop_conf", "/cmss/bch/bc/hadoop/etc/hadoop75/");
        param.put("hdfs_file_path", "/cqx/data/hbidc/000000_0");
        param.put("zookeeper", "10.1.4.186:2183");
        param.put("seq_zk_path", "/computecenter/task_context/if_upload_iptrace_jitian/infoId");
        param.put("host", "10.1.8.204");
        param.put("port", "22");
        param.put("user", "edc_base");
        param.put("password", "fLyxp1s*");
        param.put("remote_path", "/bi/user/cqx/data/hblog/");
        param.put("sftp_parallel_num", "10");
        jobBean.setJob_param(param);
        System.out.println(jobBean.toString());

        //生产10057
        jobBean.setJob_id("10057");
        jobBean.setJob_name("SplitPackageJob");
        jobBean.setJob_class("com.cqx.myjob.jobcomponent.impl.SplitPackageJob");
        param = new HashMap<>();
        param.put("local_bak_path", "/bi/databackup/if_upload_hb_netlog/${run_date}/");
        param.put("extension", "01-${device_id}-${seq}-${file_start_time}-${file_end_time}-${record_count}-${md5}-${file_size}.txt.gz");
        param.put("max_line", "10000");
        param.put("hadoop_conf", "/cmss/bch/bc1.3.6/hadoop/etc/hadoop/retnhd.conf/");
        param.put("hdfs_file_path", "hdfs://fjedcretnhd/user/bdoc/20/services/hdfs/17/yz/bigdata/if_upload_hb_netlog/202005200000/nat/000000_0");
        param.put("zookeeper", "10.47.217.179:2185");
        param.put("seq_zk_path", "/computecenter/task_context/if_upload_hb_netlog/infoId");
        param.put("host", "10.48.152.7");
        param.put("port", "31247");
        param.put("user", "FJRZSB001");
        param.put("password", "Jt!@34");
        param.put("remote_path", "/data/IF_UPLOAD_BOSS/HOME_Log_Server/FJ/YDFJQ00003/");
        param.put("sftp_parallel_num", "40");
        jobBean.setJob_param(param);
        System.out.println(jobBean.toString());

//        String result = httpClient.doPost(url, data);
//        System.out.println(result);
    }

    @Test
    public void do3() {
        String msisdn = "1064767893910";
        long all_cnt = 50979751;
        long mod = Long.valueOf(msisdn) % all_cnt;
        long mod_hash = Math.abs(msisdn.hashCode()) % all_cnt;
        System.out.println("mod：" + mod + "，mod_hash：" + mod_hash + "，hash_code：" + Math.abs(msisdn.hashCode()));
    }
}