package com.cqx.myjob.jobcomponent.bean;

import com.cqx.common.annotation.BeanDesc;
import com.cqx.common.bean.javabean.BaseBean;

/**
 * GzSplitBean
 *
 * @author chenqixu
 */
public class GzSplitBean extends BaseBean {
    @BeanDesc(value = "源文件路径")
    private String sourcePath;
    @BeanDesc(value = "源文件后缀")
    private String fileSuffix;
    @BeanDesc(value = "压缩文件名")
    private String gzName;
    @BeanDesc(value = "压缩文件输出路径")
    private String outputPath;
    @BeanDesc(value = "切割大小(字节)")
    private int splitSize;
    @BeanDesc(value = "并发数")
    private int parallel_num;

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public String getGzName() {
        return gzName;
    }

    public void setGzName(String gzName) {
        this.gzName = gzName;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public int getSplitSize() {
        return splitSize;
    }

    public void setSplitSize(int splitSize) {
        this.splitSize = splitSize;
    }

    public int getParallel_num() {
        return parallel_num;
    }

    public void setParallel_num(int parallel_num) {
        this.parallel_num = parallel_num;
    }
}
