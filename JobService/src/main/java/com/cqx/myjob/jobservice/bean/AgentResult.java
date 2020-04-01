package com.cqx.myjob.jobservice.bean;

import java.io.Serializable;

/**
 * AgentResult
 *
 * @author chenqixu
 */
public class AgentResult implements Serializable {
    private String resultCode;
    private String successLog;
    private String errLog;

    public AgentResult(String resultCode, String successLog, String errLog) {
        this.resultCode = resultCode;
        this.successLog = successLog;
        this.errLog = errLog;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getSuccessLog() {
        return successLog;
    }

    public void setSuccessLog(String successLog) {
        this.successLog = successLog;
    }

    public String getErrLog() {
        return errLog;
    }

    public void setErrLog(String errLog) {
        this.errLog = errLog;
    }
}
