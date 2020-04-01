package com.cqx.myjob.jobcomponent.impl;

import com.cqx.common.utils.hdfs.HdfsTool;
import com.cqx.myjob.jobcomponent.IJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 读取hdfs文件，按照规则转换成本地文件
 *
 * @author chenqixu
 */
public class ShareFileJob implements IJob {

    private static final Logger logger = LoggerFactory.getLogger(ShareFileJob.class);
    private HdfsTool hdfsTool;

    @Override
    public void init(Map<String, String> param) throws Throwable {

    }

    @Override
    public void run() throws Throwable {

    }

    @Override
    public void release() throws Throwable {

    }
}
