package com.cqx.myjob.jobcomponent.impl;

import com.cqx.common.utils.file.FileResult;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.ftp.FtpParamCfg;
import com.cqx.common.utils.hdfs.HdfsBean;
import com.cqx.common.utils.hdfs.HdfsTool;
import com.cqx.common.utils.sftp.SftpConnection;
import com.cqx.common.utils.sftp.SftpUtil;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.common.utils.thread.ThreadTool;
import com.cqx.common.utils.zookeeper.ZookeeperTools;
import com.cqx.myjob.jobcomponent.BaseJob;
import com.cqx.myjob.jobcomponent.bean.SplitPackageBean;
import com.cqx.myjob.jobcomponent.util.FileNameFormat;
import com.cqx.myjob.jobcomponent.util.GZMd5MemoryCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 拆分和打包
 *
 * @author chenqixu
 */
public class SplitPackageJob extends BaseJob {

    private static final Logger logger = LoggerFactory.getLogger(SplitPackageJob.class);
    private SplitPackageBean splitPackageBean;
    private FileNameFormat fileNameFormat;
    private ZookeeperTools zookeeperTools;
    private SftpConnection sftpConnection;
    private HdfsTool hdfsTool;

    @Override
    public void init(Map<String, String> param) throws Throwable {
        splitPackageBean = setValueByMap(param, SplitPackageBean.class, logger);
        zookeeperTools = ZookeeperTools.getInstance();
        zookeeperTools.init(splitPackageBean.getZookeeper());
        fileNameFormat = new FileNameFormat(splitPackageBean.getExtension(), splitPackageBean.getSeq_zk_path());
        FtpParamCfg ftpParamCfg = new FtpParamCfg(splitPackageBean.getHost(),
                splitPackageBean.getPort(), splitPackageBean.getUser(), splitPackageBean.getPassword());
        sftpConnection = SftpUtil.getSftpConnection(ftpParamCfg);
        logger.info("mkdir：{}", splitPackageBean.getLocal_bak_path());
        FileUtil.CreateDir(splitPackageBean.getLocal_bak_path());
    }

    @Override
    public void run() throws Throwable {
        hdfsTool = new HdfsTool(splitPackageBean.getHadoop_conf(), new HdfsBean());
        //扫描
        for (String path : hdfsTool.lsPath(splitPackageBean.getHdfs_file_path())) {
            logger.info("scan hdfs：{}", path);
            //分割打包上传
            splitAndSend(path);
        }
    }

    @Override
    public void release() throws Throwable {
        if (sftpConnection != null) SftpUtil.closeSftpConnection(sftpConnection);
        if (zookeeperTools != null) zookeeperTools.close();
        if (hdfsTool != null) hdfsTool.closeFileSystem();
    }

    private void splitAndSend(String hdfs_file_path) throws Throwable {
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        FileUtil fileUtil = new FileUtil();
        List<String> results = new ArrayList<>();
        List<Thread> tasks = new ArrayList<>();
        try {
            fileUtil.setReader(hdfsTool.openFile(hdfs_file_path));
            int max_line = Integer.valueOf(splitPackageBean.getMax_line());
            FileResult<String> fileResult = new FileResult<String>() {
                int cnt = 0;

                @Override
                public void run(String s) {
                    cnt++;
                    addFileresult(s + "\r\n");
                    if (cnt == max_line) {
                        addTask(tasks, submit(results, getFileresult()));
                        cnt = 0;
                        count("submit");
                        clearFileresult();
                    }
                    count("read");
                }
            };
            //================================
            //打包
            timeCostUtil.start();
            fileUtil.read(fileResult);
            //处理没有被提交的
            if (fileResult.getFileresult().size() > 0) {
                addTask(tasks, submit(results, fileResult.getFileresult()));
            }
            //等待完成
            waitFor(tasks);
            timeCostUtil.end();
            logger.info("==========={} The file processing is completed, as follows，read：{}，submit：{}，cost：{}",
                    hdfs_file_path,
                    fileResult.getCount("read"),
                    fileResult.getCount("submit"),
                    timeCostUtil.getCost());
            //================================
            //上传
            ThreadTool threadTool = new ThreadTool(splitPackageBean.getSftp_parallel_num());//并发控制
            timeCostUtil.start();
            //添加任务
            for (String fileName : results) {
                String local = splitPackageBean.getLocal_bak_path() + fileName;
                String remote = splitPackageBean.getRemote_path() + fileName;
                threadTool.addTask(new Runnable() {
                    @Override
                    public void run() {
                        SftpUtil.upload(sftpConnection, local, remote);
                    }
                });
            }
            //启动任务
            threadTool.startTask();
            timeCostUtil.end();
            logger.info("==========={} Upload succeeded，fileNum：{}，cost：{}", hdfs_file_path, results.size(), timeCostUtil.getCost());
        } finally {
            fileUtil.closeRead();
        }
    }

    private Thread submit(List<String> results, List<String> messages) {
        List<String> cp_messages = new ArrayList<>(messages);
        SplitPackage splitPackage = new SplitPackage(results, cp_messages, splitPackageBean.getLocal_bak_path());
        FutureTask<String> futureTask = new FutureTask<>(splitPackage);
        Thread thread = new Thread(futureTask);
        thread.start();
        return thread;
    }

    private void addTask(List<Thread> tasks, Thread task) {
        tasks.add(task);
    }

    private void waitFor(List<Thread> tasks) throws InterruptedException {
        for (Thread task : tasks) {
            task.join();
        }
    }

    class SplitPackage implements Callable<String> {
        private List<String> messages;
        private String local_bak_path;//创建本地备份文件的路径
        private List<String> results;

        SplitPackage(List<String> results, List<String> messages, String local_bak_path) {
            this.results = results;
            this.messages = messages;
            this.local_bak_path = local_bak_path;
        }

        @Override
        public String call() throws Exception {
            TimeCostUtil timeCostUtil = new TimeCostUtil();
            timeCostUtil.start();
            Date startTime = new Date();
            GZMd5MemoryCalculator gzMd5MemoryCalculator = new GZMd5MemoryCalculator();
            StringBuilder sb = new StringBuilder();
            for (String msg : messages) {
                sb.append(msg);
            }
            gzMd5MemoryCalculator.write_flush(sb.toString());
            String md5 = gzMd5MemoryCalculator.digest();
            long size = gzMd5MemoryCalculator.fileSize();
            Date endTime = new Date();
            String fileName = fileNameFormat.getName(startTime, endTime, md5, messages.size(), size);
            String localBakFile = local_bak_path + fileName;
            // 构造本地文件输出
            try (OutputStream fileOut = new FileOutputStream(localBakFile)) {
                fileOut.write(gzMd5MemoryCalculator.getResultFile());
                fileOut.flush();
            } catch (Exception e) {
                throw new RuntimeException("无法创建文件:" + localBakFile, e);
            }
            // 构造本地OK文件
            try (OutputStream fileOut = new FileOutputStream(localBakFile + ".ok")) {
                fileOut.flush();
            } catch (Exception e) {
                throw new RuntimeException("无法创建文件:" + localBakFile + ".ok", e);
            }
            timeCostUtil.end();
            logger.debug("成功新建本地备份文件：{}，耗时：{}", localBakFile, timeCostUtil.getCost());
            results.add(fileName);
            return fileName;
        }
    }
}
