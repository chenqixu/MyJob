package com.cqx.myjob.jobservice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LogThread extends Thread {
    private static final String separator = System.getProperty("line.separator");
    private static final String file_encoding = System.getProperty("file.encoding");
    private static Logger logger = LoggerFactory.getLogger(LogThread.class);
    private InputStream is;
    private StringBuffer threadlog = new StringBuffer();
    private String type;
    private boolean isNeedPrintLog;
    private BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();

    public LogThread(InputStream is, String type) {
        this(is, type, true);
    }

    public LogThread(InputStream is, String type, boolean isNeedPrintLog) {
        this.is = is;
        this.type = type;
        this.isNeedPrintLog = isNeedPrintLog;
    }

    public void run() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(is, file_encoding);
            br = new BufferedReader(isr, 1024);
            String line;
            while ((line = br.readLine()) != null) {
                if (isNeedPrintLog) {
                    if (type.equals("err")) {
                        logger.error(line);
                    } else {
                        logger.info(line);
                    }
                }
                threadlog.append(line).append(separator);
                logQueue.add(line);
            }
        } catch (IOException ioe) {
            threadlog.append(ioe.getMessage()).append(separator);
            logger.error("创建/读取 IO异常", ioe);
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    threadlog.append(e.getMessage()).append(separator);
                    logger.error("InputStreamReader流关闭IO异常", e);
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    threadlog.append(e.getMessage()).append(separator);
                    logger.error("BufferedReader流关闭IO异常", e);
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
