package com.cqx.myjob.jobservice.service;

import com.alibaba.fastjson.JSON;
import com.cqx.myjob.jobcomponent.bean.JobBean;
import com.cqx.myjob.jobservice.bean.AgentResult;
import com.cqx.myjob.jobservice.task.ExecShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;

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
    private String submit_path = "/";

    public JobServer() {
        URL url = this.getClass().getResource("/");
        if (url != null) {
            submit_path = url.getPath().replace("file:", "");
            int index_jar = submit_path.indexOf("JobService-1.0.0.jar");
            if (index_jar > 0) submit_path = submit_path.substring(0, index_jar);
        }
    }

    /**
     * 提交任务，返回任务ID
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/submit/", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
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
        AgentResultBuilder(resultcode, execShell);
        return job_id;
    }

    private AgentResult AgentResultBuilder(int resultcode, ExecShell execShell) {
        StringBuilder success_sb = new StringBuilder();
        StringBuilder error_sb = new StringBuilder();
        execShell.writeSuccessLog(success_sb);
        execShell.writeErrLog(error_sb);
        return new AgentResult("" + resultcode, success_sb.toString(), error_sb.toString());
    }
}
