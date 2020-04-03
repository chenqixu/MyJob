package com.cqx.myjob.jobcomponent.bean;

import com.cqx.common.annotation.BeanDesc;
import com.cqx.common.bean.javabean.BaseBean;

/**
 * ModRAFReadBean
 *
 * @author chenqixu
 */
public class ModRAFReadBean extends BaseBean {
    @BeanDesc(value = "本地映像文件路径")
    private String local_raf_path;
    @BeanDesc(value = "本地map文件路径")
    private String local_map_path;

    public String getLocal_raf_path() {
        return local_raf_path;
    }

    public void setLocal_raf_path(String local_raf_path) {
        this.local_raf_path = local_raf_path;
    }

    public String getLocal_map_path() {
        return local_map_path;
    }

    public void setLocal_map_path(String local_map_path) {
        this.local_map_path = local_map_path;
    }
}
