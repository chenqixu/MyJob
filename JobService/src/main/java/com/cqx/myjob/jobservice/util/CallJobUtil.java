package com.cqx.myjob.jobservice.util;

import com.cqx.myjob.jobcomponent.bean.JobBean;
import com.cqx.myjob.jobservice.task.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * CallJobUtil
 *
 * @author chenqixu
 */
public class CallJobUtil {
    private static final Logger logger = LoggerFactory.getLogger(CallJobUtil.class);
    private Map<String, JobThread> jobThreadMap = new ConcurrentHashMap<>();
    private Map<String, BlockingQueue<String>> logQueueMap = new ConcurrentHashMap<>();
    private String exec_path;
    private String java_home;
    private String class_path;

    public CallJobUtil(String exec_path, String java_home, String class_path) {
        this.exec_path = exec_path;
        this.java_home = java_home;
        this.class_path = class_path;
    }

    /**
     * 提交任务
     *
     * @param jobBean
     */
    public void submitJob(JobBean jobBean) {
        String job_id = jobBean.getJob_id();
        //判断下任务是否重复提交
        JobThread jobThread = jobThreadMap.get(job_id);
        if (jobThread == null) {
            JobThread newTask = new JobThread(jobBean);
            jobThreadMap.put(job_id, newTask);
            newTask.start();
            logger.info("任务{}提交", job_id);
        } else {
            logger.warn("任务{}重复提交，请检查！", job_id);
        }
    }

    /**
     * 查询任务状态
     *
     * @param job_id
     * @return
     */
    public int queryJobStatus(String job_id) {
        JobThread jobThread = jobThreadMap.get(job_id);
        if (jobThread != null) {
            return jobThread.getRet();
        } else {
            return -100;//未找到这个任务
        }
    }

    /**
     * 查询任务日志
     *
     * @param job_id
     * @return
     */
    public List<String> queryJobLog(String job_id) {
        List<String> logs = new ArrayList<>();
        BlockingQueue<String> logQueue = logQueueMap.get(job_id);
        if (logQueue != null) {
            String log;
            while ((log = logQueue.poll()) != null) {
                logs.add(log);
            }
        }
        return logs;
    }

    /**
     * 释放已完成任务
     *
     * @param job_id
     */
    public void releaseJob(String job_id) {
        int ret = queryJobStatus(job_id);
        switch (TaskStatus.getStatus(ret)) {
            case SUCCESS:
            case FAIL:
                jobThreadMap.remove(job_id);
                logQueueMap.remove(job_id);
                logger.info("释放已完成任务：{}", job_id);
                break;
            default:
                break;
        }
    }

    /**
     * 任务调用线程
     */
    class JobThread extends Thread {
        private volatile int ret = -99;//准备
        private JobBean jobBean;

        JobThread(JobBean jobBean) {
            this.jobBean = jobBean;
        }

        public void run() {
            ret = -98;//运行中
            ProcessBuilderFactory processBuilderFactory = new ProcessBuilderFactory(jobBean.getJob_id());
            //设置运行路径
            processBuilderFactory.setExec_path(exec_path);

            List<String> params = new ArrayList<>();
            params.add(jobBean.toString());

            RunUtil runUtil = new RunUtil();
            runUtil.setJavaHome(java_home);
            runUtil.setClassPath(class_path);
            runUtil.setMainClass("com.cqx.myjob.jobworker.JobWorker");
            runUtil.setParams(params);
            runUtil.setJob_id(jobBean.getJob_id());

            BlockingQueue<String> logQueue = logQueueMap.get(jobBean.getJob_id());
            if (logQueue == null) {
                logQueue = new LinkedBlockingQueue<>();
                logQueueMap.put(jobBean.getJob_id(), logQueue);
            }

            BlockingQueue<String> finalLogQueue = logQueue;
            //运行命令
            ret = processBuilderFactory.execCmdNoWait(logMsg -> {
                logger.debug(logMsg);
                finalLogQueue.add(logMsg);
            }, runUtil.getCommand());
        }

        /**
         * 返回任务状态
         *
         * @return
         */
        int getRet() {
            return ret;
        }
    }
}
