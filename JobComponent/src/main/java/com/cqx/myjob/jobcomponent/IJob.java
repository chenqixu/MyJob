package com.cqx.myjob.jobcomponent;

import java.util.Map;

/**
 * IJob
 *
 * @author chenqixu
 */
public interface IJob {
    void init(Map<String, String> param) throws Throwable;

    void run() throws Throwable;

    void release() throws Throwable;
}
