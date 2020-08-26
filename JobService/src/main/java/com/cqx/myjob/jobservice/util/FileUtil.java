package com.cqx.myjob.jobservice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;

/**
 * FileUtil
 *
 * @author chenqixu
 */
public class FileUtil {
    private static final String fileSparator = File.separator;
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static File[] listFiles(String filePath) {
        return listFiles(filePath, null);
    }

    public static File[] listFiles(String filePath, String endWith) {
        File file = new File(filePath);
        if (file.exists() && file.isDirectory()) {
            return file.listFiles(pathname -> {
                if (endWith != null && endWith.length() > 0) {
                    return pathname.getPath().endsWith(endWith);
                } else {
                    return true;
                }
            });
        }
        return null;
    }

    public static String[] listFile(String path) {
        return listFile(path, null);
    }

    public static String[] listFile(String path, final String keyword) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            if (keyword != null && keyword.length() > 0) {
                logger.info("listFile use keyword：{}.", keyword);
                return file.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.contains(keyword);
                    }
                });
            } else {
                logger.info("listFile not use keyword.");
                return file.list();
            }
        } else {
            logger.warn("path：{}，file not exists：{} or file is not Directory：{}", path, file.exists(), file.isDirectory());
        }
        return new String[0];
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
