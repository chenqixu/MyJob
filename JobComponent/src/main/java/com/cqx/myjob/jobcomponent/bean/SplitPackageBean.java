package com.cqx.myjob.jobcomponent.bean;

import com.cqx.common.annotation.BeanDesc;
import com.cqx.common.bean.javabean.BaseBean;

/**
 * SplitPackageBean
 *
 * @author chenqixu
 */
public class SplitPackageBean extends BaseBean {
    @BeanDesc(value = "创建本地备份文件的路径")
    private String local_bak_path;
    @BeanDesc(value = "生成文件表达式")
    private String extension;
    @BeanDesc(value = "文件最大行数")
    private String max_line;
    @BeanDesc(value = "hadoop配置文件路径")
    private String hadoop_conf;
    @BeanDesc(value = "hadoop处理的文件")
    private String hdfs_file_path;
    @BeanDesc(value = "zookeeper")
    private String zookeeper;
    @BeanDesc(value = "zookeeper上的分布式序列路径")
    private String seq_zk_path;
    @BeanDesc(value = "主机ip")
    private String host;
    @BeanDesc(value = "主机端口")
    private Integer port;
    @BeanDesc(value = "用户名")
    private String user;
    @BeanDesc(value = "密码")
    private String password;
    @BeanDesc(value = "远程路径")
    private String remote_path;

    public String getLocal_bak_path() {
        return local_bak_path;
    }

    public void setLocal_bak_path(String local_bak_path) {
        this.local_bak_path = local_bak_path;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getMax_line() {
        return max_line;
    }

    public void setMax_line(String max_line) {
        this.max_line = max_line;
    }

    public String getHadoop_conf() {
        return hadoop_conf;
    }

    public void setHadoop_conf(String hadoop_conf) {
        this.hadoop_conf = hadoop_conf;
    }

    public String getHdfs_file_path() {
        return hdfs_file_path;
    }

    public void setHdfs_file_path(String hdfs_file_path) {
        this.hdfs_file_path = hdfs_file_path;
    }

    public String getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(String zookeeper) {
        this.zookeeper = zookeeper;
    }

    public String getSeq_zk_path() {
        return seq_zk_path;
    }

    public void setSeq_zk_path(String seq_zk_path) {
        this.seq_zk_path = seq_zk_path;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemote_path() {
        return remote_path;
    }

    public void setRemote_path(String remote_path) {
        this.remote_path = remote_path;
    }
}
