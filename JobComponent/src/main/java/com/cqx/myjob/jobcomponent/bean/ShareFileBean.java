package com.cqx.myjob.jobcomponent.bean;

import com.cqx.common.annotation.BeanDesc;
import com.cqx.common.bean.javabean.BaseBean;

/**
 * ShareFileBean
 *
 * @author chenqixu
 */
public class ShareFileBean extends BaseBean {
    @BeanDesc(value = "hadoop配置文件路径")
    private String hadoop_conf;
    @BeanDesc(value = "hadoop扫描路径")
    private String scan_path;
    @BeanDesc(value = "本地映像文件路径")
    private String local_path;

    public String getHadoop_conf() {
        return hadoop_conf;
    }

    public void setHadoop_conf(String hadoop_conf) {
        this.hadoop_conf = hadoop_conf;
    }

    public String getScan_path() {
        return scan_path;
    }

    public void setScan_path(String scan_path) {
        this.scan_path = scan_path;
    }

    public String getLocal_path() {
        return local_path;
    }

    public void setLocal_path(String local_path) {
        this.local_path = local_path;
    }
}
