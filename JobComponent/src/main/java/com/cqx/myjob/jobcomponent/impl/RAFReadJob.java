package com.cqx.myjob.jobcomponent.impl;

import com.cqx.common.utils.hdfs.HdfsBean;
import com.cqx.common.utils.hdfs.HdfsTool;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.myjob.jobcomponent.BaseJob;
import com.cqx.myjob.jobcomponent.bean.RAFReadBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RAFReadJob
 *
 * @author chenqixu
 */
public class RAFReadJob extends BaseJob {

    private static final Logger logger = LoggerFactory.getLogger(RAFReadJob.class);
    //    private MyRandomAccessFile myRandomAccessFile;
    private HdfsTool hdfsTool;
    private RAFReadBean rafReadBean;
    private ModRAFRead modRAFRead = new ModRAFRead();

    @Override
    public void init(Map<String, String> param) throws Throwable {
        logger.info("==步骤【开始】：开始初始化参数");
        rafReadBean = setValueByMap(param, RAFReadBean.class, logger);
        HdfsBean hdfsBean = new HdfsBean();
        hdfsTool = new HdfsTool(rafReadBean.getHadoop_conf(), hdfsBean);
//        myRandomAccessFile = new MyRandomAccessFile(rafReadBean.getLocal_path());
        //初始化索引
        modRAFRead.init(rafReadBean);
        logger.info("==步骤【0】：完成初始化参数");
    }

    @Override
    public void run() throws Throwable {
        for (String path : hdfsTool.lsPath(rafReadBean.getScan_path())) {
            TimeCostUtil timeCostUtil = new TimeCostUtil();
            InputStream is = null;
            BufferedReader br = null;
//            int cnt = 0;
            Map<String, String> readMap = new HashMap<>();
            Map<String, String> rafMap = new HashMap<>();
            List<String> msisdnList = new ArrayList<>();
            if (path != null && path.length() > 0) {
                try {
                    is = hdfsTool.openFile(path);
                    br = new BufferedReader(new InputStreamReader(is));
                    String str;
                    int readCnt = 0;
                    while ((str = br.readLine()) != null && readCnt < rafReadBean.getCnt()) {
                        String[] arr = str.split("\\|", -1);
                        //450009493554750|13859494500|1
                        String imsi = arr[0];
                        String msisdn = arr[1];
                        String msisdn_head = msisdn.substring(0, 3);
                        if (msisdn.length() == 11 && msisdn_head.equals("135")) {
                            logger.debug("==步骤【1】：add，str：{}，msisdn：{}，imsi：{}", str, msisdn, imsi);
                            readMap.put(msisdn, imsi);
                            msisdnList.add(msisdn);
                            readCnt++;
                        }
                    }
                } finally {
                    if (br != null) br.close();
                    if (is != null) is.close();
                }
            }
            //开始测试Map
            timeCostUtil.start();
            for (String msisdn : msisdnList) {
                String imsi = readMap.get(msisdn);
                logger.debug("==步骤【1】：Map，msisdn：{}，imsi：{}", msisdn, imsi);
            }
            timeCostUtil.stop();
            logger.info("==步骤【1】：Map，处理耗时：{}", timeCostUtil.getCost());
            //开始测试RAF
            timeCostUtil.start();
            for (String msisdn : msisdnList) {
//                String msisdn_index = msisdn.substring(3);
//                //计算位置
//                long pos = (Long.valueOf(msisdn_index) + 1) * 15;
//                String imsi = myRandomAccessFile.read(pos, 15);
//                rafMap.put(msisdn, imsi);
                String imsi = modRAFRead.getValue(msisdn);
                logger.debug("==步骤【1】：RAF，msisdn：{}，imsi：{}", msisdn, imsi);
            }
            timeCostUtil.stop();
            logger.info("==步骤【1】：RAF，处理耗时：{}", timeCostUtil.getCost());
//            //质量比较
//            int fail_cnt = 0;
//            for (Map.Entry<String, String> entry : readMap.entrySet()) {
//                String readValue = entry.getValue();
//                String rafValue = rafMap.get(entry.getKey());
//                if (!readValue.equals(rafValue)) {
//                    fail_cnt++;
//                    logger.warn("找到不匹配项目：msisdn：{}，read_imsi：{}，raf_imsi：{}", entry.getKey(), readValue, rafValue);
//                }
//            }
//            if (fail_cnt == 0) {
//                logger.info("map和raf完全匹配！");
//            }
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
//        if (myRandomAccessFile != null) {
//            myRandomAccessFile.close();
//        }
        if (modRAFRead != null) {
            modRAFRead.release();
        }
    }
}
