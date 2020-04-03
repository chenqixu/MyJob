package com.cqx.myjob.jobcomponent.impl;

import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.file.MyRandomAccessFile;
import com.cqx.common.utils.hdfs.HdfsBean;
import com.cqx.common.utils.hdfs.HdfsTool;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.myjob.jobcomponent.BaseJob;
import com.cqx.myjob.jobcomponent.bean.ModRAFWriteBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * ModRAFWriteJob
 *
 * @author chenqixu
 */
public class ModRAFWriteJob extends BaseJob {

    private static final Logger logger = LoggerFactory.getLogger(ModRAFWriteJob.class);
    private static final byte[] NULL_BYTE = new byte[15];
    private static final String NULL_VALUE = new String(NULL_BYTE);
    private ModRAFWriteBean modRAFWriteBean;
    private MyRandomAccessFile myRandomAccessFile;
    private HdfsTool hdfsTool;

    @Override
    public void init(Map<String, String> param) throws Throwable {
        logger.info("==步骤【开始】：开始初始化参数");
        modRAFWriteBean = setValueByMap(param, ModRAFWriteBean.class, logger);

        //如果raf文件存在，先删除
        FileUtil.del(modRAFWriteBean.getLocal_raf_path());
        //如果map文件存在，先删除
        FileUtil.del(modRAFWriteBean.getLocal_map_path());

        HdfsBean hdfsBean = new HdfsBean();
        hdfsTool = new HdfsTool(modRAFWriteBean.getHadoop_conf(), hdfsBean);
        myRandomAccessFile = new MyRandomAccessFile(modRAFWriteBean.getLocal_raf_path());
        logger.info("==步骤【0】：完成初始化参数");
    }

    @Override
    public void run() throws Throwable {
        long all_cnt = 0;
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        for (String path : hdfsTool.lsPath(modRAFWriteBean.getScan_path())) {
            long cnt = 0;
            timeCostUtil.start();
            InputStream is = null;
            BufferedReader br = null;
            if (path != null && path.length() > 0) {
                try {
                    is = hdfsTool.openFile(path);
                    br = new BufferedReader(new InputStreamReader(is));
                    while (br.readLine() != null) {
                        cnt++;
                        all_cnt++;
                    }
                } finally {
                    if (br != null) br.close();
                    if (is != null) is.close();
                }
            }
            timeCostUtil.end();
            logger.info("==步骤【1】：处理文件：{}，处理记录数：{}，处理耗时：{}", path, cnt, timeCostUtil.getCost());
        }
        logger.info("==步骤【1】：总处理记录数：{}", all_cnt);
        //##########################################################
        //造一个空的raf
        timeCostUtil.start();
        myRandomAccessFile.write((all_cnt + 1) * 15, NULL_VALUE);
        timeCostUtil.end();
        logger.info("==步骤【2】：造一个空的raf：{}，总耗时：{}",
                modRAFWriteBean.getLocal_raf_path(), timeCostUtil.getCost());
        //##########################################################
        //真正开始处理
        Map<String, String> cacheMap = new HashMap<>();
        for (String path : hdfsTool.lsPath(modRAFWriteBean.getScan_path())) {
            timeCostUtil.start();
            InputStream is = null;
            BufferedReader br = null;
            long raf_cnt = 0;
            long map_cnt = 0;
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
                        if (msisdn != null && msisdn.length() > 0) {
                            //计算mod
                            long mod = Long.valueOf(msisdn) % all_cnt;//直接用msisdn来计算mod
//                            long mod = Math.abs(msisdn.hashCode()) % all_cnt;//使用hashcode来计算mod
                            //计算下位置，从raf读值
                            long pos = (mod + 1) * 15;
                            String read_imsi = myRandomAccessFile.read(pos, 15);
                            if (read_imsi.equals(NULL_VALUE)) {//没有从raf取到数据
                                //写入raf
                                myRandomAccessFile.write(pos, imsi);
                                raf_cnt++;
                            } else {//有从raf取到数据
                                //写到map
                                cacheMap.put(msisdn, imsi);
                                map_cnt++;
                            }
                        }
                    }
                } finally {
                    if (br != null) br.close();
                    if (is != null) is.close();
                }
            }
            timeCostUtil.end();
            logger.info("==步骤【3】：处理文件：{}，raf处理记录数：{}，map处理记录数：{}，处理耗时：{}",
                    path, raf_cnt, map_cnt, timeCostUtil.getCost());
        }
        //##########################################################
        //最后把map写到本地文件
        timeCostUtil.start();
        FileUtil fileUtil = new FileUtil();
        long map_file_cnt = 0;
        try {
            fileUtil.createFile(modRAFWriteBean.getLocal_map_path(), "UTF-8");
            for (Map.Entry<String, String> entry : cacheMap.entrySet()) {
                String _tmp = entry.getKey() + "|" + entry.getValue() + "\r\n";
                fileUtil.write(_tmp);
                map_file_cnt++;
            }
        } finally {
            fileUtil.closeWrite();
        }
        timeCostUtil.end();
        logger.info("==步骤【4】：处理文件：{}，处理记录数：{}，处理耗时：{}",
                modRAFWriteBean.getLocal_map_path(), map_file_cnt, timeCostUtil.getCost());
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
