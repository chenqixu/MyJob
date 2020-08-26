package com.cqx.myjob.jobservice.util;

import java.io.File;
import java.lang.management.ManagementFactory;

/**
 * Other tools
 *
 * @author chenqixu
 */
public class OtherUtil {
    /**
     * 获取PID
     */
    public static String getCurrentPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        return pid;
    }

    /**
     * 获取系统属性
     */
    public static String getSystemProperty(String args) {
        return System.getProperty(args);
    }

    /**
     * 获取字符集，默认返回GB2312
     */
    public static String getFileEncoding() {
        String fileencoding = OtherUtil.getSystemProperty("file.encoding");
        return fileencoding == null ? "GB2312" : fileencoding;
    }

    /**
     * 通过全路径判断是否是文件
     */
    public static boolean isFile(String path) {
        File file = new File(path);
        return file.isFile();
    }

    /**
     * 通过全路径判断是否是目录
     */
    public static boolean isDirectory(String path) {
        File file = new File(path);
        return file.isDirectory();
    }

    public static String reIfNull(String _str) {
        if (_str == null) {
            return "";
        }
        return _str;
    }

    /**
     * 是否是window系统
     *
     * @return
     */
    public static boolean isWindow() {
        String systemType = System.getProperty("os.name");
        if (systemType.toUpperCase().startsWith("WINDOWS")) {
            return true;
        } else {
            return false;
        }
    }
}
