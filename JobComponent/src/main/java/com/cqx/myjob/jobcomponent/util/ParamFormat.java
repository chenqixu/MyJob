package com.cqx.myjob.jobcomponent.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 参数格式化
 *
 * @author chenqixu
 */
public class ParamFormat {

    private ParamFormat() {
    }

    public static ParamFormat builder() {
        return new ParamFormat();
    }

    private String getDateFormat(String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date());
    }

    public void format(Map<String, String> param) {
        String run_date = getDateFormat("yyyyMMdd");
        for (Map.Entry<String, String> entry : param.entrySet()) {
            entry.setValue(entry.getValue().replace("${run_date}", run_date));
        }
    }
}
