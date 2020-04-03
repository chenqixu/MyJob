package com.cqx.myjob.jobcomponent;

import com.cqx.common.bean.javabean.BaseBean;
import com.cqx.common.utils.param.ParamUtil;
import org.slf4j.Logger;

import java.util.Map;

/**
 * BaseJob
 *
 * @author chenqixu
 */
public abstract class BaseJob implements IJob {
    private ParamUtil paramUtil = new ParamUtil();

    /**
     * 设置参数，并打印日志
     *
     * @param map
     * @param cls
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T setValueByMap(Map<String, String> map, Class<T> cls, Logger logger) throws Exception {
        //设置参数
        T t = paramUtil.setValueByMap(map, cls);
        //打印日志
        ParamUtil.info((BaseBean) t, logger);
        return t;
    }
}
