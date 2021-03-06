package com.cqx.myjob.jobservice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 调用命令工具
 *
 * @author chenqixu
 */
public class ProcessBuilderFactory {
    private static final Logger logger = LoggerFactory.getLogger(ProcessBuilderFactory.class);
    private static final String separator = System.getProperty("line.separator");
    private int resultcode = 0;
    private String successkey = null;// 成功关键字
    private boolean isLogErrDeal = false;// 是否需要错误日志辅助判断结果
    private ProcessBuilder builder = null;
    private Process process = null;
    private ProcessBuilderLogThread ltinfo = null;
    private ProcessBuilderLogThread lterr = null;
    private String tag_name;
    private StringBuffer success_sb = null;
    private StringBuffer error_sb = null;
    private BlockingQueue<String> success_log_queue = null;
    private BlockingQueue<String> error_log_queue = null;
    private String exec_path;

    public ProcessBuilderFactory() {
    }

    public ProcessBuilderFactory(String tag_name) {
        this.tag_name = tag_name;
    }

    public String getTag_name() {
        return tag_name == null ? "" : "==步骤【" + tag_name + "】：";
    }

    public void setSuccesskey(String successkey) {
        this.successkey = successkey;
    }

    public void setLogErrDeal(boolean isLogErrDeal) {
        this.isLogErrDeal = isLogErrDeal;
    }

    /**
     * 运行命令，日志阻塞模式
     *
     * @param cmd 命令
     * @return
     */
    public int execCmd(String... cmd) {
        int result = -1;
        builder = new ProcessBuilder(cmd);
        if (exec_path != null) {
            File exec_file_path = new File(exec_path);
            if (exec_file_path.exists() && exec_file_path.isDirectory())
                builder.directory(exec_file_path);
        }
        try {
            process = builder.start();
            runLog(process);
            result = waitFor();
            success_sb = ltinfo.getThreadloglog();
            error_sb = lterr.getThreadloglog();
        } catch (IOException | InterruptedException e) {
            logger.error(getTag_name() + e);
            result = -1;
        } finally {
            release();
        }
        if (resultcode == -1) result = resultcode;
        return result;
    }

    /**
     * 运行命令，日志无阻塞模式
     *
     * @param cmd 命令
     * @throws IOException
     */
    private void execCmdNoWait(String... cmd) throws IOException {
        builder = new ProcessBuilder(cmd);
        if (exec_path != null) {
            File exec_file_path = new File(exec_path);
            if (exec_file_path.exists() && exec_file_path.isDirectory())
                builder.directory(exec_file_path);
        }
        process = builder.start();
        runLog(process);
        success_log_queue = ltinfo.getLogQueue();
        error_log_queue = lterr.getLogQueue();
    }

    /**
     * 运行命令，日志无阻塞模式
     *
     * @param logDealInf 无阻塞模式下日志处理过程
     * @param cmd        命令
     * @return
     */
    public int execCmdNoWait(LogDealInf logDealInf, String... cmd) {
        int ret = -1;
        try {
            execCmdNoWait(cmd);
            final BlockingQueue<String> success_log = getSuccess_log_queue();
            final BlockingQueue<String> error_log = getError_log_queue();
            ThreadLogMonitor successLogThread = new ThreadLogMonitor(logDealInf, success_log);
            ThreadLogMonitor errorLogThread = new ThreadLogMonitor(logDealInf, error_log);
            successLogThread.start();
            errorLogThread.start();
            ret = waitFor();
            //ThreadLogMonitor waitFor
            successLogThread.stopMonitor();
            errorLogThread.stopMonitor();
            successLogThread.join();
            errorLogThread.join();
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage(), e);
        } finally {
            release();
        }
        return ret;
    }

    /**
     * 运行日志
     *
     * @param process
     */
    private void runLog(Process process) {
        ltinfo = new ProcessBuilderLogThread(process.getInputStream(), "info");
        lterr = new ProcessBuilderLogThread(process.getErrorStream(), "err");
        ltinfo.start();
        lterr.start();
    }

    /**
     * 等待处理完成
     */
    private int waitFor() throws InterruptedException {
        // 资源释放前必须等待日志线程结束
        if (ltinfo != null)
            try {
                ltinfo.join();
            } catch (InterruptedException e) {
                logger.error(getTag_name() + e);
            }
        if (lterr != null)
            try {
                lterr.join();
            } catch (InterruptedException e) {
                logger.error(getTag_name() + e);
            }
        return process.waitFor();
    }

    /**
     * 资源释放
     */
    public void release() {
        if (process != null) {
            process.destroy();
        }
    }

    public StringBuffer getSuccess_sb() {
        return success_sb;
    }

    public StringBuffer getError_sb() {
        return error_sb;
    }

    public BlockingQueue<String> getSuccess_log_queue() {
        return success_log_queue;
    }

    public BlockingQueue<String> getError_log_queue() {
        return error_log_queue;
    }

    public String getExec_path() {
        return exec_path;
    }

    public void setExec_path(String exec_path) {
        this.exec_path = exec_path;
    }

    /**
     * 日志线程
     */
    private class ProcessBuilderLogThread extends Thread {
        private InputStream is;
        private String type;
        private StringBuffer threadlog = new StringBuffer();
        private BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();

        ProcessBuilderLogThread(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
            InputStreamReader isr = null;
            BufferedReader br = null;
            try {
                isr = new InputStreamReader(is, OtherUtil.getFileEncoding());
                br = new BufferedReader(isr, 1024);
                String line;
                while ((line = br.readLine()) != null) {
                    if (type.equals("err")) {
                        // 需要错误日志辅助判断结果
                        if (isLogErrDeal) {
                            resultcode = -1;
                        }
//                        logger.error(getTag_name() + line);
//                    } else {
//                        logger.info(getTag_name() + line);
                    }
                    threadlog.append(line).append(separator);
                    logQueue.add(line);
                }
                // 任务返回不正确，需要使用关键字进行判断任务是否成功
                if (successkey != null) {
                    if (threadlog.indexOf(successkey) >= 0) {
                        resultcode = 0;
                    }
                }
            } catch (IOException ioe) {
                logger.error(getTag_name() + "##创建/读取 日志流IO异常", ioe);
            } finally {
                if (isr != null) {
                    try {
                        isr.close();
                    } catch (IOException e) {
                        logger.error(getTag_name() + "##日志InputStreamReader流关闭IO异常", e);
                    }
                }
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        logger.error(getTag_name() + "##日志BufferedReader流关闭IO异常", e);
                    }
                }
            }
        }

        StringBuffer getThreadloglog() {
            return threadlog;
        }

        BlockingQueue<String> getLogQueue() {
            return logQueue;
        }
    }

    /**
     * 无阻塞模式日志监控
     */
    class ThreadLogMonitor extends Thread {
        volatile boolean flag = true;
        BlockingQueue<String> log;
        LogDealInf logDealInf;

        ThreadLogMonitor(LogDealInf logDealInf, BlockingQueue<String> log) {
            this.logDealInf = logDealInf;
            this.log = log;
        }

        @Override
        public void run() {
            //flag为false的时候可能还没消费完成，所以还要加一个判断
            while (flag || log.size() > 0) {
                String msg;
                while ((msg = log.poll()) != null) {
                    logDealInf.logDeal(msg);
                }
                SleepUtil.sleepMilliSecond(50);
            }
        }

        void stopMonitor() {
            flag = false;
        }
    }
}
