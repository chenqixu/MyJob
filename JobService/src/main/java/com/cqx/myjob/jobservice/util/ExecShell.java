package com.cqx.myjob.jobservice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

/**
 * ExecShell
 *
 * @author chenqixu
 */
public class ExecShell {

    private static final Logger logger = LoggerFactory.getLogger(ExecShell.class);
    private Process process = null;
    private LogThread ltinfo = null;
    private LogThread lterr = null;
    private boolean isNeedPrintLog = true;//是否需要打印日志
    private String job_id;

    private ExecShell(boolean isNeedPrintLog, long job_id) {
        this.isNeedPrintLog = isNeedPrintLog;
        this.job_id = job_id + "_" + System.currentTimeMillis();
    }

    public static ExecShell builder(boolean isNeedPrintLog, long job_id) {
        return new ExecShell(isNeedPrintLog, job_id);
    }

    public int run(String[] cmd, String path) {
        int resultcode = -1;
        try {
            Runtime runtime = Runtime.getRuntime();
            logger.info("job_id【{}】，status【start】，cmd：{}", job_id, Arrays.asList(cmd));
            process = runtime.exec(cmd, null, new File(path));
            runLog();
            resultcode = waitFor();
            logger.info("job_id【{}】，status【end】，resultcode：{}", job_id, resultcode);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            release();
        }
        return resultcode;
    }

    /**
     * 运行日志
     */
    private void runLog() {
        ltinfo = new LogThread(process.getInputStream(), "info", isNeedPrintLog);
        lterr = new LogThread(process.getErrorStream(), "err", isNeedPrintLog);
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
                logger.error(e.getMessage(), e);
            }
        if (lterr != null)
            try {
                lterr.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        return process.waitFor();
    }

    public void writeSuccessLog(StringBuilder sb) {
        sb.append(ltinfo.getThreadloglog());
    }

    public void writeErrLog(StringBuilder sb) {
        sb.append(lterr.getThreadloglog());
    }

    /**
     * 资源释放
     */
    private void release() {
        if (process != null) {
            logger.info("job_id【{}】，status【release】，process.destroy.", job_id);
            process.destroy();
        }
    }
}
