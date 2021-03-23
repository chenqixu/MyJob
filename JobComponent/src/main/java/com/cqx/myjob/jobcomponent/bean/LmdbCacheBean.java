package com.cqx.myjob.jobcomponent.bean;

import com.cqx.common.annotation.BeanDesc;
import com.cqx.common.bean.javabean.BaseBean;

/**
 * LmdbCacheBean
 *
 * @author chenqixu
 */
public class LmdbCacheBean extends BaseBean {
    @BeanDesc(value = "lmdb路径")
    private String lmdbPath;

    public String getLmdbPath() {
        return lmdbPath;
    }

    public void setLmdbPath(String lmdbPath) {
        this.lmdbPath = lmdbPath;
    }
}
