package com.cqx.myjob.jobcomponent.impl;

import com.cqx.common.utils.file.FileCount;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.hdfs.HdfsBean;
import com.cqx.common.utils.hdfs.HdfsTool;
import com.cqx.common.utils.system.TimeCostUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModRAFWriteJobTest {
    private static final Logger logger = LoggerFactory.getLogger(ModRAFWriteJobTest.class);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void run() throws Exception {
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        HdfsTool hdfsTool = null;
        FileUtil fileUtil = new FileUtil();
        try {
            hdfsTool = new HdfsTool("d:\\tmp\\etc\\hadoop\\conf75\\", new HdfsBean());
            fileUtil.setReader(hdfsTool.openFile("/cqx/data/hbidc/000000_0"));
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
}