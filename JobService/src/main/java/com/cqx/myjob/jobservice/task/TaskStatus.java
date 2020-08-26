package com.cqx.myjob.jobservice.task;

/**
 * TaskStatus
 *
 * @author chenqixu
 */
public enum TaskStatus {
    NO_TASK,
    NEW,
    RUNNING,
    SUCCESS,
    FAIL,
    ;

    public static TaskStatus getStatus(int ret) {
        if (ret == -100) return NO_TASK;
        else if (ret == -99) return NEW;
        else if (ret == -98) return RUNNING;
        else if (ret == 0) return SUCCESS;
        else return FAIL;
    }
}
