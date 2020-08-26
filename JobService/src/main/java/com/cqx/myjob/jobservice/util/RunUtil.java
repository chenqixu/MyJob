package com.cqx.myjob.jobservice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * RunUtil
 *
 * @author chenqixu
 */
public class RunUtil {
    private static final Logger logger = LoggerFactory.getLogger(RunUtil.class);
    private static final String fileSparator = File.separator;
    private String mainClass;
    private String classPath;
    private String javaHome;
    private String job_id;
    private List<String> params;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public String[] getCommand() {
        //参数list
        List<String> lists = new ArrayList<>();

        //java
        javaHome = FileUtil.endWith(javaHome);
        if (OtherUtil.isWindow()) {
            lists.add(javaHome + "bin" + fileSparator + "java.exe");
        } else {
            lists.add(javaHome + "bin" + fileSparator + "java");
        }
        lists.add("-server");

        //依赖
        lists.add("-classpath");
        classPath = FileUtil.endWith(classPath);
        String[] jars = FileUtil.listFile(classPath, ".jar");
        StringBuilder sb = new StringBuilder();
        for (String jar : jars) {
            sb.append(classPath).append(jar);
            if (OtherUtil.isWindow()) {
                sb.append(";");
            } else {
                sb.append(":");
            }
        }
        lists.add(sb.toString());

        //日志参数
        lists.add("-Dcurrent_date=" + sdf.format(new Date()));
        lists.add("-Djob_id=" + job_id);

        //Main类
        lists.add(mainClass);

        //Main类需要的参数
        if (params != null && params.size() > 0) lists.addAll(params);

        //最后变成数组
        String[] arr = new String[lists.size()];
        logger.debug("getCommand：{}", lists);
        return lists.toArray(arr);
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }
}
