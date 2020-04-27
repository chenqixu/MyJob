package com.cqx.myjob.jobcomponent.impl;

import com.cqx.myjob.jobcomponent.test.BaseJobTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SplitPackageJobTest extends BaseJobTest {

    @Before
    public void setUp() throws Throwable {
        job = new SplitPackageJob();
        super.init();
    }

    @After
    public void tearDown() throws Throwable {
        job.release();
    }

    @Test
    public void run() throws Throwable {
        job.run();
    }

    @Override
    protected Map<String, String> assembleParam() {
        Map<String, String> param = new HashMap<>();
        param.put("local_bak_path", "d:/tmp/bi/databackup/if_upload_hb_netlog/${run_date}/");
        param.put("extension", "01-${device_id}-${seq}-${file_start_time}-${file_end_time}-${record_count}-${md5}-${file_size}.txt.gz");
        param.put("max_line", "10000");
        param.put("hadoop_conf", "d:\\tmp\\etc\\hadoop\\conf75\\");
        param.put("hdfs_file_path", "/cqx/data/hbidc/000000_1");
        param.put("zookeeper", "10.1.4.186:2183");
        param.put("seq_zk_path", "/computecenter/task_context/if_upload_iptrace_jitian/infoId");
        param.put("host", "10.1.8.204");
        param.put("port", "22");
        param.put("user", "edc_base");
        param.put("password", "fLyxp1s*");
        param.put("remote_path", "/bi/user/cqx/data/hblog/");
        return param;
    }
}