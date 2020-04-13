package com.cqx.myjob.jobcomponent.bean;

import com.cqx.common.annotation.BeanDesc;
import com.cqx.common.bean.javabean.BaseBean;

/**
 * ModRAFWriteBean
 *
 * @author chenqixu
 */
public class ModRAFWriteBean extends BaseBean {
    @BeanDesc(value = "hadoop配置文件路径")
    private String hadoop_conf;
    @BeanDesc(value = "hadoop扫描路径")
    private String scan_path;
    @BeanDesc(value = "本地映像文件路径")
    private String local_raf_path;
    @BeanDesc(value = "本地map读文件路径")
    private String local_map_read_path;
    @BeanDesc(value = "本地map写文件路径")
    private String local_map_write_path;
    @BeanDesc(value = "map清洗次数")
    private int deal_cnt;
    @BeanDesc(value = "生成的本地索引文件")
    private String local_index_path;

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

    public String getLocal_raf_path() {
        return local_raf_path;
    }

    public void setLocal_raf_path(String local_raf_path) {
        this.local_raf_path = local_raf_path;
    }

    public int getDeal_cnt() {
        return deal_cnt;
    }

    public void setDeal_cnt(int deal_cnt) {
        this.deal_cnt = deal_cnt;
    }

    public String getLocal_index_path() {
        return local_index_path;
    }

    public void setLocal_index_path(String local_index_path) {
        this.local_index_path = local_index_path;
    }

    public String getLocal_map_read_path() {
        return local_map_read_path;
    }

    public void setLocal_map_read_path(String local_map_read_path) {
        this.local_map_read_path = local_map_read_path;
    }

    public String getLocal_map_write_path() {
        return local_map_write_path;
    }

    public void setLocal_map_write_path(String local_map_write_path) {
        this.local_map_write_path = local_map_write_path;
    }
}
