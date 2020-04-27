package com.cqx.myjob.jobservice.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LogThread extends Thread {

    private static final String separator = System.getProperty("line.separator");
    private static final String file_encoding = System.getProperty("file.encoding");
    private static Logger logger = LoggerFactory.getLogger(LogThread.class);
    private InputStream is;
    private StringBuffer threadlog = new StringBuffer();
    private String type;
    private boolean isNeedPrintLog;

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
                        logger.info("##错误日志##" + line);
                        threadlog.append(line).append(separator);
                    } else {
                        logger.info("##内容##" + line);
                        threadlog.append(line).append(separator);
                    }
                }
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

    public StringBuffer getThreadloglog() {
        return threadlog;
    }
}
