package com.cqx.myjob.jobcomponent.impl;

import com.cqx.common.utils.param.ParamUtil;
import com.cqx.myjob.jobclient.jersey.JobRemoteCall;
import com.cqx.myjob.jobcomponent.bean.RocksDBCacheBean;
import org.junit.Test;

import java.util.Map;

public class RocksDBCacheReadJobTest {

    @Test
    public void upLoadJar() {
        JobRemoteCall.upLoadJar(TestJob.class);
    }

    @Test
    public void run() throws Exception {
        new JobRemoteCall(RocksDBCacheReadJob.class, assembleParam()).run();
    }

    private Map<String, String> assembleParam() throws Exception {
        RocksDBCacheBean rocksDBCacheBean = new RocksDBCacheBean();
        rocksDBCacheBean.setHadoop_conf("/cmss/bch/bc/hadoop/etc/hadoop75/");
        rocksDBCacheBean.setScan_path("/cqz/mccdr/sum_date=20200611/");
        rocksDBCacheBean.setDb_path("/bi/user/cqx/data/rocksdb/");
        rocksDBCacheBean.setDb_name("mccdr");
        rocksDBCacheBean.setDb_read_cnt(100000);
        rocksDBCacheBean.setIs_print_value(false);
        return ParamUtil.beanToMap(RocksDBCacheBean.class, rocksDBCacheBean);
    }
}