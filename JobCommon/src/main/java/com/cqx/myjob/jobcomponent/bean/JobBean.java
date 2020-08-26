package com.cqx.myjob.jobcomponent.bean;

import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * JobBean
 *
 * @author chenqixu
 */
public class JobBean {
    private String job_id;
    private String job_name;
    private String job_class;
    private String job_jvm;
    private Map<String, String> job_param;

    public String toString() {
        return JSON.toJSONString(this);
    }

    public String getJob_name() {
        return job_name;
    }

    public void setJob_name(String job_name) {
        this.job_name = job_name;
    }

    public String getJob_class() {
        return job_class;
    }

    public void setJob_class(String job_class) {
        this.job_class = job_class;
    }

    public Map<String, String> getJob_param() {
        return job_param;
    }

    public void setJob_param(Map<String, String> job_param) {
        this.job_param = job_param;
    }

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }
}
