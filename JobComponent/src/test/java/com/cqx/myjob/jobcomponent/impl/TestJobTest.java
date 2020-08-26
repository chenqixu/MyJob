package com.cqx.myjob.jobcomponent.impl;

import com.cqx.common.utils.param.ParamUtil;
import com.cqx.myjob.jobclient.jersey.JobRemoteCall;
import com.cqx.myjob.jobcomponent.bean.ShareFileBean;
import org.junit.Test;

import java.util.Map;

public class TestJobTest {

    @Test
    public void upLoadJar() {
        JobRemoteCall.upLoadJar(TestJob.class);
    }

    @Test
    public void run() throws Exception {
        new JobRemoteCall(TestJob.class, assembleParam()).run();
    }

    private Map<String, String> assembleParam() throws Exception {
        ShareFileBean shareFileBean = new ShareFileBean();
        shareFileBean.setHadoop_conf("/cmss/bch/bc/hadoop/etc/hadoop75/");
        shareFileBean.setScan_path("/cqx/data/hbidc/000000_0");
        return ParamUtil.beanToMap(ShareFileBean.class, shareFileBean);
    }
}