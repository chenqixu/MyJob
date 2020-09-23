package com.cqx.myjob.jobclient.jersey;

import com.alibaba.fastjson.JSON;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.myjob.jobcomponent.base.BaseJob;
import com.cqx.myjob.jobcomponent.bean.JobBean;
import com.cqx.myjob.jobcomponent.utils.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * JobRemoteCall
 *
 * @author chenqixu
 */
public class JobRemoteCall {
    private static final Logger logger = LoggerFactory.getLogger(JobRemoteCall.class);
    private static final String serverUrl = "http://10.1.8.203:10091/jobservice/job/";
    private static final String STR_SEED = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final char[] STR_CHAR = STR_SEED.toCharArray();
    private JobBean jobBean;
    private Class<? extends BaseJob> cls;
    private String job_id;

    public JobRemoteCall(Class<? extends BaseJob> cls, Map<String, String> job_param) {
        this.cls = cls;
        job_id = createJobId();
        jobBean = new JobBean();
        jobBean.setJob_id(job_id);
        jobBean.setJob_param(job_param);
        jobBean.setJob_class(cls.getName());
        jobBean.setJob_name(cls.getSimpleName());
    }

    /**
     * 本地编译好的组件包上传
     *
     * @param cls
     */
    public static void upLoadJar(Class<? extends BaseJob> cls) {
        JerseyClientFactory jerseyClientFactory = JerseyClientFactory.getInstance().buildFile();
        File file = new File(cls.getResource("/").getPath());
        String path = FileUtil.endWith(file.getParent());
        logger.info("扫描jar包的路径：{}", path);
        String[] fileNames = FileUtil.listFile(path, ".jar");
        for (String f : fileNames) {
            logger.info("准备上传的jar包：{}", path + f);
            jerseyClientFactory.addFile(new File(path + f));
        }
        boolean uploadResult = jerseyClientFactory.postFile(serverUrl + "batch/upload", Boolean.class);
        logger.info("上传结果：{}", uploadResult);
    }

    /**
     * 任务ID：类名+当前毫秒+5位随机数
     *
     * @return
     */
    private String createJobId() {
        //5位随机数
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(STR_CHAR[random.nextInt(STR_SEED.length())]);
        return cls.getSimpleName() + "@" + System.currentTimeMillis() + "@" + sb.toString();
    }

    public void run() {
        //Jersey客户端
        JerseyClientFactory clientFactory = JerseyClientFactory.getInstance();
        //post调用任务
        clientFactory.postJSON(serverUrl + "submit_jobBean",
                JSON.toJSONString(jobBean));
        //获取日志
        int ret;
        while (!TaskStatus.isComplete(ret = clientFactory.get(serverUrl + "get_job_status/" + job_id, Integer.class))) {
            List logs = clientFactory.get(serverUrl + "get_job_log/" + job_id, List.class);
            for (Object log : logs) {
                logger.info("log：{}", log);
            }
            SleepUtil.sleepMilliSecond(500);
        }
        //日志有可能没消费完，再消费一次
        List logs = clientFactory.get(serverUrl + "get_job_log/" + job_id, List.class);
        for (Object log : logs) {
            logger.info("log：{}", log);
        }
        //最后释放任务
        clientFactory.get(serverUrl + "release_job/" + job_id);
        if (ret == 137) {
            logger.warn("log：任务被异常kill！");
        }
    }
}
