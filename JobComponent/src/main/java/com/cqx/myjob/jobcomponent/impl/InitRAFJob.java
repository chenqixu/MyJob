package com.cqx.myjob.jobcomponent.impl;

import com.cqx.common.utils.file.MyRandomAccessFile;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.myjob.jobcomponent.base.BaseJob;
import com.cqx.myjob.jobcomponent.bean.InitRAFBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * InitRAFJob
 *
 * @author chenqixu
 */
public class InitRAFJob extends BaseJob {

    private static final Logger logger = LoggerFactory.getLogger(InitRAFJob.class);
    private InitRAFBean initRAFBean;
    private MyRandomAccessFile myRandomAccessFile;

    @Override
    public void init(Map<String, String> param) throws Throwable {
        logger.info("==步骤【开始】：开始初始化参数");
        initRAFBean = setValueByMap(param, InitRAFBean.class, logger);
        myRandomAccessFile = new MyRandomAccessFile(initRAFBean.getPath());
        logger.info("==步骤【0】：完成初始化参数");
    }

    @Override
    public void run() throws Throwable {
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        timeCostUtil.start();
//        long key = initRAFBean.getStart_key();
//        while (key <= initRAFBean.getEnd_key()) {
//            myRandomAccessFile.write("000000000000000");
//            key++;
//        }
        long pos = (initRAFBean.getEnd_key() + 1) * 15;
        myRandomAccessFile.write(pos, "000000000000000");
        timeCostUtil.stop();
        logger.info("==步骤【1】：处理文件：{}，处理记录数：{}，处理耗时：{}", initRAFBean.getPath(), pos, timeCostUtil.getCost());
    }

    @Override
    public void release() throws Throwable {
        logger.info("==步骤【完成】：释放RandomAccessFile资源");
        if (myRandomAccessFile != null) {
            myRandomAccessFile.close();
        }
    }
}
