package com.cqx.myjob.jobcomponent.impl;

import com.cqx.common.utils.hdfs.HdfsBean;
import com.cqx.common.utils.hdfs.HdfsTool;
import com.cqx.common.utils.localcache.rocksdb.RocksDBUtil;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.myjob.jobcomponent.base.BaseJob;
import com.cqx.myjob.jobcomponent.bean.RocksDBCacheBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * RocksDBCacheJob
 *
 * @author chenqixu
 */
public class RocksDBCacheJob extends BaseJob {

    private static final Logger logger = LoggerFactory.getLogger(RocksDBCacheJob.class);
    private RocksDBCacheBean rocksDBCacheBean;
    private HdfsTool hdfsTool;
    private RocksDBUtil rocksDBUtil;

    @Override
    public void init(Map<String, String> param) throws Throwable {
        logger.info("==步骤【开始】：开始初始化参数");
        rocksDBCacheBean = setValueByMap(param, RocksDBCacheBean.class, logger);

        //初始化HDFS工具类
        HdfsBean hdfsBean = new HdfsBean();
        hdfsTool = new HdfsTool(rocksDBCacheBean.getHadoop_conf(), hdfsBean);

        //初始化RocksDB工具类
        rocksDBUtil = new RocksDBUtil(rocksDBCacheBean.getDb_path(), rocksDBCacheBean.getDb_name());

        logger.info("==步骤【0】：完成初始化参数");
    }

    @Override
    public void run() throws Throwable {
        //HDFS扫描
        List<String> fileList = hdfsTool.lsPath(rocksDBCacheBean.getScan_path());
        for (String path : fileList) {
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
                        if (msisdn.length() == 11) {
                            //写入
                            rocksDBUtil.putValue(imsi, msisdn);
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
        }
    }

    @Override
    public void release() throws Throwable {
        logger.info("==步骤【完成】：释放hadoop资源，释放RocksDB资源");
        if (hdfsTool != null) {
            hdfsTool.closeFileSystem();
        }
        if (rocksDBUtil != null) {
            rocksDBUtil.release();
        }
    }
}
