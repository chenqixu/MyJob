package com.cqx.myjob.jobcomponent.bean;

import com.cqx.common.annotation.BeanDesc;
import com.cqx.common.bean.javabean.BaseBean;

/**
 * InitRAFBean
 *
 * @author chenqixu
 */
public class InitRAFBean extends BaseBean {
    @BeanDesc(value = "文件路径")
    private String path;
    @BeanDesc(value = "起始位置")
    private long start_key;
    @BeanDesc(value = "结束位置")
    private long end_key;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getStart_key() {
        return start_key;
    }

    public void setStart_key(long start_key) {
        this.start_key = start_key;
    }

    public long getEnd_key() {
        return end_key;
    }

    public void setEnd_key(long end_key) {
        this.end_key = end_key;
    }
}
