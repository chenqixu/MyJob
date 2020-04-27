package com.cqx.myjob.jobcomponent.test;

import com.cqx.myjob.jobcomponent.BaseJob;
import com.cqx.myjob.jobcomponent.util.ParamFormat;

import java.util.Map;

/**
 * BaseJobTest
 *
 * @author chenqixu
 */
public abstract class BaseJobTest {
    protected BaseJob job;

    protected void init() throws Throwable {
        if (job != null) {
            Map<String, String> param = assembleParam();
            //参数替换
            ParamFormat.builder().format(param);
            job.init(param);
        }
    }

    protected abstract Map<String, String> assembleParam();

}
