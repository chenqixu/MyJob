package com.cqx.myjob.jobcomponent.impl;

import com.cqx.common.utils.file.BaseRandomAccessFile;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.file.IFileRead;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.myjob.jobcomponent.base.BaseJob;
import com.cqx.myjob.jobcomponent.bean.ModRAFReadBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ModRAFReadJob
 *
 * @author chenqixu
 */
public class ModRAFReadJob extends BaseJob {

    private static final Logger logger = LoggerFactory.getLogger(ModRAFReadJob.class);
    private ModRAFReadBean modRAFReadBean;
    private BaseRandomAccessFile myRandomAccessFile;
    private Map<String, String> cacheMap;

    @Override
    public void init(Map<String, String> param) throws Throwable {
        logger.info("==步骤【开始】：开始初始化参数");
        modRAFReadBean = setValueByMap(param, ModRAFReadBean.class, logger);
        myRandomAccessFile = new BaseRandomAccessFile(modRAFReadBean.getLocal_raf_path());
        cacheMap = new HashMap<>();
        //初始化本地map
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        timeCostUtil.start();
        FileUtil fileUtil = new FileUtil();
        try {
            fileUtil.getFile(modRAFReadBean.getLocal_map_path(), "UTF-8");
            fileUtil.read(new IFileRead() {
                @Override
                public void run(String content) throws IOException {
                    //切割字符串，存入map
                    String[] arr = content.split("\\|", -1);
                    cacheMap.put(arr[0], arr[1]);
                }

                @Override
                public void run(byte[] content) throws IOException {
                }

                @Override
                public void tearDown() throws IOException {
                }
            });
        } finally {
            fileUtil.closeRead();
        }
        timeCostUtil.stop();
        logger.info("==步骤【0】：cacheMap大小：{}，处理耗时：{}", cacheMap.size(), timeCostUtil.getCost());
        logger.info("==步骤【0】：完成初始化参数");
    }

    @Override
    public void run() throws Throwable {
        Thread.sleep(20000);
    }

    @Override
    public void release() throws Throwable {
        logger.info("==步骤【完成】：释放RandomAccessFile资源");
        if (myRandomAccessFile != null) {
            myRandomAccessFile.close();
        }
    }
}
