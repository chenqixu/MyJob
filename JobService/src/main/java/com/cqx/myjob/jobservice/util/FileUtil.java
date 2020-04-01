package com.cqx.myjob.jobservice.util;

import java.io.File;

/**
 * FileUtil
 *
 * @author chenqixu
 */
public class FileUtil {

    private static final String fileSparator = File.separator;

    public static File[] listFiles(String filePath) {
        return listFiles(filePath, null);
    }

    public static File[] listFiles(String filePath, String endWith) {
        File file = new File(filePath);
        if (file.exists() && file.isDirectory()) {
            return file.listFiles(pathname -> {
                if (endWith != null || endWith.length() > 0) {
                    if (pathname.getPath().endsWith(endWith)) return true;
                    else return false;
                } else {
                    return true;
                }
            });
        }
        return null;
    }

    /**
     * 如果文件夹不以\结尾则加上
     *
     * @param path
     * @return
     */
    public static String endWith(String path) {
        if (path.endsWith(fileSparator)) return path;
        else return path + fileSparator;
    }
}
