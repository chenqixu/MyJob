package com.cqx.myjob.jobservice.task;

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

    private ExecShell() {
    }

    public static ExecShell builder() {
        return new ExecShell();
    }

    public int run(String cmd) {
        int resultcode = -1;
        try {
            Runtime runtime = Runtime.getRuntime();
            logger.info("cmd：{}", cmd);
            process = runtime.exec(cmd);
            runLog();
            resultcode = process.waitFor();
            logger.info("cmd：{}，resultcode：{}", cmd, resultcode);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            release();
        }
        return resultcode;
    }

    public int runs(String cmd) {
        int resultcode = -1;
        try {
            Runtime runtime = Runtime.getRuntime();
            logger.info("cmd：{}", cmd);
            String[] cmdarr = cmd.split(";", -1);
            for (String _cmd : cmdarr) {
                if (_cmd != null && _cmd.length() > 0) {
                    process = runtime.exec(_cmd);
                    runLog();
                    resultcode = process.waitFor();
                    logger.info("cmd：{}，resultcode：{}", _cmd, resultcode);
                    if (resultcode != 0) break;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            release();
        }
        return resultcode;
    }

    public int run(String[] cmd, String path) {
        int resultcode = -1;
        try {
            Runtime runtime = Runtime.getRuntime();
            logger.info("##start##cmd：{}", Arrays.asList(cmd));
            process = runtime.exec(cmd, null, new File(path));
            runLog();
            resultcode = process.waitFor();
            logger.info("##end##cmd：{}，resultcode：{}", Arrays.asList(cmd), resultcode);
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
        ltinfo = new LogThread(process.getInputStream(), "info");
        lterr = new LogThread(process.getErrorStream(), "err");
        ltinfo.start();
        lterr.start();
        try {
            ltinfo.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        try {
            lterr.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
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
    public void release() {
        if (process != null) {
            logger.info("##release##process.destroy");
            process.destroy();
        }
    }
}
