package com.cqx.myjob.jobworker;

import com.alibaba.fastjson.JSON;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.myjob.jobcomponent.base.IJob;
import com.cqx.myjob.jobcomponent.bean.JobBean;
import com.cqx.myjob.jobcomponent.utils.ParamFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * JobWorker
 *
 * @author chenqixu
 */
public class JobWorker {
    /**
     * 日志变量从外部加载
     * -Dcurrent_date=20200401 -Djob_id=10051
     */
//    private static final Logger logger = LoggerFactory.getLogger(JobWorker.class);
    public static void main(String[] args) {
        //日志变量从外部加载，使用-Dcurrent_date=20200401 -Djob_id=10051
        //实际上这个时候这个是设置到System.Property中
//        System.setProperty("current_date", "20200401");
//        System.setProperty("job_id", "10052");
        Logger logger = LoggerFactory.getLogger(JobWorker.class);
        if (args.length == 1) {
            TimeCostUtil timeCostUtil = new TimeCostUtil();
            timeCostUtil.start();
            JobBean jobBean = null;
            IJob iJob = null;
            try {
                jobBean = JSON.parseObject(args[0], JobBean.class);
            } catch (Throwable e) {
                logger.error(String.format("解析json参数异常，参数：%s，异常信息：%s",
                        args[0], e.getMessage()), e);
            }
            if (jobBean != null) {
                try {
                    Class cls = Class.forName(jobBean.getJob_class());
                    iJob = (IJob) cls.newInstance();
                } catch (ClassNotFoundException e) {
                    logger.error(String.format("==%s【找不到类】，异常信息：%s",
                            jobBean.getJob_name(), e.getMessage()), e);
                } catch (IllegalAccessException | InstantiationException e) {
                    logger.error(String.format("==%s【反射异常】，异常信息：%s",
                            jobBean.getJob_name(), e.getMessage()), e);
                }
                if (iJob != null) {
//                    //设置单独日志
//                    FileAppender fileAppender = (FileAppender) logger.getLoggerRepository().getRootLogger().getAppender("R");
//                    //logs/yyyymmdd/task_id
//                    String logFileName = "logs/" + getDate("yyyyMMdd") + "/" + jobBean.getJob_id() + ".log";
//                    fileAppender.setFile(logFileName);
//                    fileAppender.activateOptions();
                    try {
                        //初始化
                        logger.info(String.format("==%s【初始化】", jobBean.getJob_name()));
                        //参数处理
                        Map<String, String> param = jobBean.getJob_param();
                        ParamFormat.builder().format(param);
                        //参数打印
                        for (Map.Entry<String, String> entry : param.entrySet()) {
                            logger.info(String.format("==%s【参数打印】，key：%s，value：%s",
                                    jobBean.getJob_name(), entry.getKey(), entry.getValue()));
                        }
                        iJob.init(jobBean.getJob_param());
                        //运行
                        logger.info(String.format("==%s【运行】", jobBean.getJob_name()));
                        iJob.run();
                        logger.info(String.format("==%s【资源释放】", jobBean.getJob_name()));
                        //资源释放
                        iJob.release();
                        timeCostUtil.stop();
                        logger.info(String.format("==%s【运行总时长】%s", jobBean.getJob_name(), timeCostUtil.getCost()));
                    } catch (Throwable e) {
                        logger.error(String.format("==%s【运行异常】，异常信息：%s",
                                jobBean.getJob_name(), e.getMessage()), e);
                    }
                }
            }
        } else {
            logger.error("传入的参数个数不正确！");
        }
    }
}
