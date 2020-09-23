package com.cqx.myjob.jobcomponent.impl;

import com.cqx.common.utils.param.ParamUtil;
import com.cqx.myjob.jobclient.jersey.JobRemoteCall;
import com.cqx.myjob.jobcomponent.bean.GzSplitBean;
import org.junit.Test;

import java.util.Map;

public class GzSplitJobTest {

    @Test
    public void run() throws Exception {
        new JobRemoteCall(GzSplitJob.class, assembleParam()).run();
    }

    private Map<String, String> assembleParam() throws Exception {
        GzSplitBean gzSplitBean = new GzSplitBean();
        gzSplitBean.setFileSuffix(".csv.txt");
        gzSplitBean.setGzName("dpi_zongbu");
        gzSplitBean.setOutputPath("/bi/user/cqx/data/dpi_zongbu/output/");
        gzSplitBean.setSourcePath("/bi/user/cqx/data/dpi_zongbu/source/");
        gzSplitBean.setSplitSize(100 * 1024 * 1024);
        gzSplitBean.setParallel_num(7);
        return ParamUtil.beanToMap(GzSplitBean.class, gzSplitBean);
    }

    @Test
    public void otherTest() {
        String gzNameFormat = "%s_%s.data.gz";
        System.out.println(String.format(gzNameFormat, "dpi_zongbu", 1));
    }
}