package com.cqx.myjob.jobcomponent.impl;

import com.cqx.common.utils.localcache.lmdb.LmdbUtil;
import com.cqx.myjob.jobcomponent.base.BaseJob;
import com.cqx.myjob.jobcomponent.bean.LmdbCacheBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * LmdbCacheJob
 *
 * @author chenqixu
 */
public class LmdbCacheJob extends BaseJob {
    private static final Logger logger = LoggerFactory.getLogger(LmdbCacheJob.class);
    private LmdbUtil lmdbUtil;
    private LmdbCacheBean lmdbCacheBean;

    @Override
    public void init(Map<String, String> param) throws Throwable {
        logger.info("==步骤【开始】：开始初始化参数");
        lmdbCacheBean = setValueByMap(param, LmdbCacheBean.class, logger);
        //初始化lmdb工具
        lmdbUtil = new LmdbUtil(lmdbCacheBean.getLmdbPath());
        logger.info("==步骤【0】：完成初始化参数");
    }

    @Override
    public void run() throws Throwable {
        lmdbUtil.put("123", "abc");
        String val = lmdbUtil.get("123");
        logger.info("val：{}", val);
    }

    @Override
    public void release() throws Throwable {
        logger.info("==步骤【完成】：释放Lmdb资源");
        if (lmdbUtil != null) lmdbUtil.release();
    }
}
