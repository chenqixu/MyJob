package com.cqx.myjob.jobcomponent.bean;

import com.cqx.common.annotation.BeanDesc;
import com.cqx.common.bean.javabean.BaseBean;

/**
 * RocksDBCacheBean
 *
 * @author chenqixu
 */
public class RocksDBCacheBean extends BaseBean {
    @BeanDesc(value = "hadoop配置文件路径")
    private String hadoop_conf;
    @BeanDesc(value = "hadoop扫描路径")
    private String scan_path;
    @BeanDesc(value = "数据库路径")
    private String db_path;
    @BeanDesc(value = "数据库名称")
    private String db_name;
    @BeanDesc(value = "读取次数")
    private int db_read_cnt;
    @BeanDesc(value = "是否打印具体内容")
    private boolean is_print_value;

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

    public String getDb_path() {
        return db_path;
    }

    public void setDb_path(String db_path) {
        this.db_path = db_path;
    }

    public String getDb_name() {
        return db_name;
    }

    public void setDb_name(String db_name) {
        this.db_name = db_name;
    }

    public int getDb_read_cnt() {
        return db_read_cnt;
    }

    public void setDb_read_cnt(int db_read_cnt) {
        this.db_read_cnt = db_read_cnt;
    }

    public boolean isIs_print_value() {
        return is_print_value;
    }

    public void setIs_print_value(boolean is_print_value) {
        this.is_print_value = is_print_value;
    }
}
