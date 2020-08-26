package com.cqx.myjob.jobcomponent.impl;

import com.cqx.common.utils.file.MyRandomAccessFile;
import com.cqx.common.utils.hdfs.HdfsBean;
import com.cqx.common.utils.hdfs.HdfsTool;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.myjob.jobcomponent.base.BaseJob;
import com.cqx.myjob.jobcomponent.bean.ShareFileBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * 读取hdfs文件，按照规则转换成本地文件
 *
 * @author chenqixu
 */
public class ShareFileJob extends BaseJob {

    private static final Logger logger = LoggerFactory.getLogger(ShareFileJob.class);
    private HdfsTool hdfsTool;
    private MyRandomAccessFile myRandomAccessFile;
    private ShareFileBean shareFileBean;

    @Override
    public void init(Map<String, String> param) throws Throwable {
        logger.info("==步骤【开始】：开始初始化参数");
        shareFileBean = setValueByMap(param, ShareFileBean.class, logger);
        HdfsBean hdfsBean = new HdfsBean();
        hdfsTool = new HdfsTool(shareFileBean.getHadoop_conf(), hdfsBean);
        myRandomAccessFile = new MyRandomAccessFile(shareFileBean.getLocal_path());
        logger.info("==步骤【0】：完成初始化参数");
    }

    @Override
    public void run() throws Throwable {
        for (String path : hdfsTool.lsPath(shareFileBean.getScan_path())) {
            TimeCostUtil timeCostUtil = new TimeCostUtil();
            timeCostUtil.start();
            InputStream is = null;
            BufferedReader br = null;
            int cnt = 0;
            if (path != null && path.length() > 0) {
                try {
                    is = hdfsTool.openFile(path);
                    br = new BufferedReader(new InputStreamReader(is));
                    String str;
                    while ((str = br.readLine()) != null) {
                        String[] arr = str.split("\\|", -1);
                        //450009493554750|13859494500|1
                        String imsi = arr[0];
                        String msisdn = arr[1];
                        String msisdn_head = msisdn.substring(0, 3);
                        String msisdn_index = msisdn.substring(3);
                        if (msisdn.length() == 11 && msisdn_head.equals("135")) {
                            //计算位置
                            long pos = (Long.valueOf(msisdn_index) + 1) * 15;
                            myRandomAccessFile.write(pos, imsi);
                            cnt++;
                        }
                    }
                } finally {
                    if (br != null) br.close();
                    if (is != null) is.close();
                }
            }
            timeCostUtil.stop();
            logger.info("==步骤【1】：处理文件：{}，处理记录数：{}，处理耗时：{}", path, cnt, timeCostUtil.getCost());
            //只处理第一个文件
            break;
        }
    }

    @Override
    public void release() throws Throwable {
        logger.info("==步骤【完成】：释放hadoop资源，释放RandomAccessFile资源");
        if (hdfsTool != null) {
            hdfsTool.closeFileSystem();
        }
        if (myRandomAccessFile != null) {
            myRandomAccessFile.close();
        }
    }
}
