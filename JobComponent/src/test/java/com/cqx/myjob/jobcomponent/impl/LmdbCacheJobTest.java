package com.cqx.myjob.jobcomponent.impl;

import com.cqx.common.utils.param.ParamUtil;
import com.cqx.myjob.jobclient.jersey.JobRemoteCall;
import com.cqx.myjob.jobcomponent.bean.LmdbCacheBean;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class LmdbCacheJobTest {

    @Before
    public void setUp() {
        //上传Jar包
        JobRemoteCall.upLoadJar(LmdbCacheJob.class);
    }

    @Test
    public void run() throws Exception {
        //远程调用
        new JobRemoteCall(LmdbCacheJob.class, assembleParam()).run();
    }

    /**
     * 参数
     *
     * @return
     * @throws Exception
     */
    private Map<String, String> assembleParam() throws Exception {
        LmdbCacheBean lmdbCacheBean = new LmdbCacheBean();
        lmdbCacheBean.setLmdbPath("/bi/user/cqx/data/lmdb/test1");
        return ParamUtil.beanToMap(LmdbCacheBean.class, lmdbCacheBean);
    }
}