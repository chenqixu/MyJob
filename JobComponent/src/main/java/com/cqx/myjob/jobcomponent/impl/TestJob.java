package com.cqx.myjob.jobcomponent.impl;

import com.cqx.common.utils.file.FileCount;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.hdfs.HdfsBean;
import com.cqx.common.utils.hdfs.HdfsTool;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.myjob.jobcomponent.BaseJob;
import com.cqx.myjob.jobcomponent.bean.ShareFileBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * TestJob
 *
 * @author chenqixu
 */
public class TestJob extends BaseJob {

    private static final Logger logger = LoggerFactory.getLogger(TestJob.class);
    private ShareFileBean shareFileBean;

    @Override
    public void init(Map<String, String> param) throws Throwable {
        shareFileBean = setValueByMap(param, ShareFileBean.class, logger);
    }

    @Override
    public void run() throws Throwable {
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        HdfsTool hdfsTool = null;
        FileUtil fileUtil = new FileUtil();
        try {
            hdfsTool = new HdfsTool(shareFileBean.getHadoop_conf(), new HdfsBean());
            fileUtil.setReader(hdfsTool.openFile(shareFileBean.getScan_path()));
            FileCount fileCount = new FileCount() {
                @Override
                public void run(String s) {
                    count("read");
                }
            };
            timeCostUtil.start();
            fileUtil.read(fileCount);
            timeCostUtil.end();
            logger.info("read：{}，cost：{}", fileCount.getCount("read"), timeCostUtil.getCost());
        } finally {
            if (hdfsTool != null) hdfsTool.closeFileSystem();
            fileUtil.closeRead();
        }
    }

    @Override
    public void release() throws Throwable {

    }
}
