package com.cqx.myjob.jobcomponent.impl;

import com.cqx.common.utils.compress.GZUtil;
import com.cqx.common.utils.file.FileResult;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.myjob.jobcomponent.base.BaseJob;
import com.cqx.myjob.jobcomponent.bean.GzSplitBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * GzSplitJob
 *
 * @author chenqixu
 */
public class GzSplitJob extends BaseJob {
    private static final Logger logger = LoggerFactory.getLogger(GzSplitJob.class);
    private GzSplitBean gzSplitBean;

    @Override
    public void init(Map<String, String> param) throws Throwable {
        //bean
        gzSplitBean = setValueByMap(param, GzSplitBean.class, logger);
        //清理
        clearTmpFiles();
    }

    @Override
    public void run() throws Throwable {
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        List<GZWriteJob> gzWriteJobList = new ArrayList<>();
        //扫描
        timeCostUtil.start();
        String[] sourceFiles = FileUtil.listFileEndWith(gzSplitBean.getSourcePath(), gzSplitBean.getFileSuffix());
        timeCostUtil.stop();
        logger.info("==【完成扫描】文件：{}，耗时：{}", Arrays.asList(sourceFiles), timeCostUtil.getCost());
        //初始化任务
        timeCostUtil.start();
        for (int i = 0; i < gzSplitBean.getParallel_num(); i++) {
            BlockingQueue<String> fileQueue = new LinkedBlockingQueue<>(Arrays.asList(sourceFiles));
            gzWriteJobList.add(new GZWriteJob(fileQueue, i));
        }
        timeCostUtil.stop();
        logger.info("==【初始化任务】任务列表：{}，耗时：{}", gzWriteJobList, timeCostUtil.getCost());
        //启动任务
        timeCostUtil.start();
        for (GZWriteJob gzWriteJob : gzWriteJobList) {
            gzWriteJob.start();
        }
        timeCostUtil.stop();
        logger.info("==【启动任务】启动完成，耗时：{}", timeCostUtil.getCost());
        //等待任务完成
        timeCostUtil.start();
        for (GZWriteJob gzWriteJob : gzWriteJobList) {
            gzWriteJob.join();
        }
        timeCostUtil.stop();
        logger.info("==【等待任务完成】任务完成，耗时：{}", timeCostUtil.getCost());
    }

    @Override
    public void release() throws Throwable {
    }

    /**
     * 清理临时文件
     */
    private void clearTmpFiles() {
        String[] tmpfiles = FileUtil.listFileEndWith(gzSplitBean.getOutputPath(), ".data.gz");
        for (String tmpfile : tmpfiles) {
            FileUtil.del(gzSplitBean.getOutputPath() + tmpfile);
            logger.info("==清理：{}", gzSplitBean.getOutputPath() + tmpfile);
        }
    }

    /**
     * 给一个文件，就写一个，直到可以分割或者调用关闭
     */
    class GZWriteJob extends Thread {
        private AtomicInteger num = new AtomicInteger();
        private GZUtil gzUtil;
        private TimeCostUtil timeCostUtil = new TimeCostUtil();
        private boolean is95 = false;
        private BlockingQueue<String> fileQueue;
        private int parallel_flag;
        private String gzNameFormat = "%s_%s_parallel%s.data.gz";

        public GZWriteJob(BlockingQueue<String> fileQueue, int parallel_flag) {
            this.fileQueue = fileQueue;
            this.parallel_flag = parallel_flag;
        }

        public void run() {
            FileUtil fileUtil = new FileUtil();
            String sourceFileName;
            while ((sourceFileName = fileQueue.poll()) != null) {
                try {
                    fileUtil.setReader(gzSplitBean.getSourcePath() + sourceFileName);
                    FileResult<String> fileResult = new FileResult<String>() {
                        @Override
                        public void run(String s) throws IOException {
                            count("read");
                            write(s.getBytes(), getCount("read"));
                        }

                        @Override
                        public void tearDown() throws IOException {
                        }
                    };
                    TimeCostUtil timeCostUtil = new TimeCostUtil();
                    timeCostUtil.start();
                    fileUtil.read(fileResult);
                    timeCostUtil.stop();
                    logger.info("sourceFileName：{}，cost：{}，cnt：{}",
                            sourceFileName, timeCostUtil.getCost(), fileResult.getCount("read"));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    fileUtil.closeRead();
                }
            }
            try {
                closeGZ();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        private String getGZName() {
            return String.format(gzSplitBean.getOutputPath() + gzNameFormat,
                    gzSplitBean.getGzName(), num.getAndIncrement(), parallel_flag);
        }

        private void newGZ() throws IOException {
            timeCostUtil.start();
            gzUtil = GZUtil.buildFile(getGZName(), true);
            is95 = false;
        }

        private void closeGZ() throws IOException {
            if (gzUtil != null) {
                gzUtil.close();
                gzUtil = null;
            }
            timeCostUtil.stop();
            is95 = false;
            logger.info("{} 本次切割cost：{}", num.get(), timeCostUtil.getCost());
        }

        private void write(byte[] bytes, long cnt) throws IOException {
            if (gzUtil == null) newGZ();
            gzUtil.write(bytes);
            int check_num;
            //判断达到95%，就改成1000条一批
            if (is95) {
                check_num = 1000;
            } else {
                check_num = 10000;
            }
            if (cnt % check_num == 0) {
                gzUtil.flush();
                long current_size = gzUtil.size();
                if (!is95 && (current_size > (gzSplitBean.getSplitSize() * 0.95))) {
                    is95 = true;
                    logger.info("达到切割的95%：{}", current_size);
                }
                if (current_size > gzSplitBean.getSplitSize()) {
                    logger.info("达到切割的文件大小：{}", current_size);
                    closeGZ();
                    newGZ();
                }
            }
        }
    }
}
