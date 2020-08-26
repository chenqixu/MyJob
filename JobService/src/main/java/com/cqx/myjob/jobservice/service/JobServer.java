package com.cqx.myjob.jobservice.service;

import com.alibaba.fastjson.JSON;
import com.cqx.myjob.jobcomponent.bean.JobBean;
import com.cqx.myjob.jobservice.bean.AgentResult;
import com.cqx.myjob.jobservice.util.CallJobUtil;
import com.cqx.myjob.jobservice.util.ExecShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * JobServer
 *
 * @author chenqixu
 */
@EnableAutoConfiguration
@RestController
@RequestMapping("job")
public class JobServer {
    private static final Logger logger = LoggerFactory.getLogger(JobServer.class);
    private CallJobUtil callJobUtil;

    //submit_path
    @Value("${server.submit-path}")
    private String submit_path;

    //java_home
    @Value("${server.java-home}")
    private String java_home;

    //组件lib路径
    @Value("${server.class-path}")
    private String class_path;

    /**
     * 初始化
     */
    @PostConstruct
    private void init() {
        logger.info("submit_path：{}，java_home：{}，class_path：{}", submit_path, java_home, class_path);
        callJobUtil = new CallJobUtil(submit_path, java_home, class_path);
    }

    /**
     * 单个文件上传
     *
     * @param file
     * @return
     */
    @RequestMapping("/upload")
    public boolean upload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            logger.info("file.getOriginalFilename : {}", originalFilename);
            try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(
                    new File(class_path + originalFilename)))) {
                byte[] bytes = file.getBytes();
                stream.write(bytes);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                return false;
            }
            logger.info("the {} file upload success.", originalFilename);
        } else {
            logger.info("the file is Empty.");
            return false;
        }
        return true;
    }

    /**
     * 多个文件上传
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/batch/upload", method = RequestMethod.POST)
    public boolean batchUpload(HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        logger.info("receive files.size : {}", files.size());
        MultipartFile file;
        for (int i = 0; i < files.size(); i++) {
            file = files.get(i);
            if (!file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                logger.info("file.getOriginalFilename : {}", originalFilename);
                try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(
                        new File(class_path + originalFilename)))) {
                    byte[] bytes = file.getBytes();
                    stream.write(bytes);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    return false;
                }
                logger.info("the {} {} file upload success.", i, originalFilename);
            } else {
                logger.info("the {} file is Empty.", i);
                return false;
            }
        }
        return true;
    }

    /**
     * 提交任务，返回任务ID
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/submit", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public int submitJob(@RequestBody String param) {
        logger.info("submit：{}，submit_path：{}", param, submit_path);
        //解析出入口类和参数，启动脚本
        JobBean jobBean = JSON.parseObject(param, JobBean.class);
        //传入的json替换{和}，还有引号
        param = param.replaceAll("\\{", "\\\\{");
        param = param.replaceAll("}", "\\\\}");
        param = param.replaceAll("\"", "\\\\\"");
        int job_id = Integer.valueOf(jobBean.getJob_id());
        String[] cmd = {"/bin/sh", "-c", "sh job.sh " + param + " " + job_id};
        ExecShell execShell = ExecShell.builder(false, job_id);
        int resultcode = execShell.run(cmd, submit_path);
        AgentResult agentResult = AgentResultBuilder(resultcode, execShell);
        return job_id;
    }

    private AgentResult AgentResultBuilder(int resultcode, ExecShell execShell) {
        StringBuilder success_sb = new StringBuilder();
        StringBuilder error_sb = new StringBuilder();
        execShell.writeSuccessLog(success_sb);
        execShell.writeErrLog(error_sb);
        return new AgentResult("" + resultcode, success_sb.toString(), error_sb.toString());
    }

    /**
     * 调用任务
     *
     * @param jobBean
     */
    @RequestMapping(value = "/submit_jobBean", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public void submitJob(@RequestBody JobBean jobBean) {
        callJobUtil.submitJob(jobBean);
    }

    /**
     * 获取任务日志
     *
     * @param job_id
     * @return
     */
    @RequestMapping(value = "/get_job_log/{job_id}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getJobLog(@PathVariable String job_id) {
        return callJobUtil.queryJobLog(job_id);
    }

    /**
     * 查询任务状态
     *
     * @param job_id
     * @return
     */
    @RequestMapping(value = "/get_job_status/{job_id}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    public int getJobStatus(@PathVariable String job_id) {
        return callJobUtil.queryJobStatus(job_id);
    }

    /**
     * 任务完成后需要释放，否则同样的任务id无法继续调用
     *
     * @param job_id
     */
    @RequestMapping(value = "/release_job/{job_id}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    public void releaseJob(@PathVariable String job_id) {
        callJobUtil.releaseJob(job_id);
    }

}
