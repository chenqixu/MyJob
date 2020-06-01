package com.cqx.myjob.jobcomponent.impl;

import com.cqx.common.utils.file.FileCount;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.file.IFileRead;
import com.cqx.common.utils.file.MyRandomAccessFile;
import com.cqx.common.utils.hdfs.HdfsBean;
import com.cqx.common.utils.hdfs.HdfsTool;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.myjob.jobcomponent.BaseJob;
import com.cqx.myjob.jobcomponent.bean.ModRAFWriteBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
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
    private static final String SP = "|";
    private static final String LINE = "\r\n";
    private ModRAFWriteBean modRAFWriteBean;
    private HdfsTool hdfsTool;
    private int threadNum = 0;//并行
    private List<MyRandomAccessFile> myRandomAccessFileList;

    @Override
    public void init(Map<String, String> param) throws Throwable {
        logger.info("==步骤【开始】：开始初始化参数");
        modRAFWriteBean = setValueByMap(param, ModRAFWriteBean.class, logger);

        //如果map文件存在，先删除
        FileUtil.del(modRAFWriteBean.getLocal_map_read_path());
        FileUtil.del(modRAFWriteBean.getLocal_map_write_path());
        //如果索引文件存在，先删除
        FileUtil.del(modRAFWriteBean.getLocal_index_path());
        //如果raf文件存在，先删除，加1是因为HDFS文件也需要raf，但不算在map raf中
        for (int i = 0; i < modRAFWriteBean.getDeal_cnt() + 1; i++) {
            FileUtil.del(modRAFWriteBean.getLocal_raf_path() + i);
        }

        //初始化HDFS工具类
        HdfsBean hdfsBean = new HdfsBean();
        hdfsTool = new HdfsTool(modRAFWriteBean.getHadoop_conf(), hdfsBean);
        //初始化本地映像文件，加1是因为HDFS文件也需要raf，但不算在map raf中
        myRandomAccessFileList = new ArrayList<>();
        for (int i = 0; i < modRAFWriteBean.getDeal_cnt() + 1; i++) {
            myRandomAccessFileList.add(new MyRandomAccessFile(modRAFWriteBean.getLocal_raf_path() + i));
        }
        logger.info("==步骤【0】：完成初始化参数");
    }

    @Override
    public void run() throws Throwable {
        //索引内容List
        List<String> indexList = new ArrayList<>();
        //HDFS扫描
        List<String> fileList = hdfsTool.lsPath(modRAFWriteBean.getScan_path());
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        //##########################################################
        //计数器
        long tmp_all_cnt = 0;
        for (String path : fileList) {
            long cnt;
            timeCostUtil.start();
            FileCount fileCount = new FileCount() {
                @Override
                public void run(String s) {
                    count("read");
                }
            };
            readHdfs(path, fileCount, threadNum);
            cnt = fileCount.getCount("read");
            tmp_all_cnt += cnt;
            timeCostUtil.stop();
            logger.info("==步骤【1】：处理文件：{}，处理记录数：{}，处理耗时：{}", path, cnt, timeCostUtil.getCost());
        }
        final long all_cnt = tmp_all_cnt;
        logger.info("==步骤【1】：总处理记录数：{}", all_cnt);
        //##########################################################
        //造空的raf
        timeCostUtil.start();
        for (MyRandomAccessFile myRandomAccessFile : myRandomAccessFileList) {
            myRandomAccessFile.write((all_cnt + 1) * 15, NULL_VALUE);
        }
        timeCostUtil.stop();
        logger.info("==步骤【2】：造空的raf：{}，总耗时：{}",
                modRAFWriteBean.getLocal_raf_path(), timeCostUtil.getCost());
        //##########################################################
        //第一次处理
        FileUtil fileWriterUtil = new FileUtil();
        //初始化map缓存文件
        fileWriterUtil.createFile(modRAFWriteBean.getLocal_map_read_path(), "UTF-8");
        long map_file_cnt = 0;
        for (String path : fileList) {
            timeCostUtil.start();
            FileCount fileCount = dealContent(all_cnt, myRandomAccessFileList.get(0), fileWriterUtil);
            readHdfs(path, fileCount, threadNum);
            long raf_cnt = fileCount.getCount("raf_cnt");
            long map_cnt = fileCount.getCount("map_cnt");
            map_file_cnt += map_cnt;
            timeCostUtil.stop();
            logger.info("==步骤【3】：处理文件：{}，mod：{}，raf处理记录数：{}，map处理记录数：{}，处理耗时：{}",
                    path, all_cnt, raf_cnt, map_cnt, timeCostUtil.getCost());
        }
        //关闭map缓存文件
        fileWriterUtil.closeWrite();
        //索引数据：raf文件名 | mod
        indexList.add(myRandomAccessFileList.get(0).getFileName() + SP + all_cnt);
        logger.info("==步骤【3】：总map处理记录数：{}", map_file_cnt);
        //##########################################################
        //从本地文件读取map缓存，再进一步转换
        for (int i = 0; i < modRAFWriteBean.getDeal_cnt(); i++) {
            timeCostUtil.start();
            long last_mod = map_file_cnt;
            //初始化写map缓存文件
            fileWriterUtil.createFile(modRAFWriteBean.getLocal_map_write_path(), "UTF-8");
            FileCount fileCount = dealContent(map_file_cnt, myRandomAccessFileList.get(i + 1), fileWriterUtil);
            readLocal(modRAFWriteBean.getLocal_map_read_path(), fileCount);
            //关闭写map缓存文件
            fileWriterUtil.closeWrite();
            long raf_cnt = fileCount.getCount("raf_cnt");
            long map_cnt = fileCount.getCount("map_cnt");
            map_file_cnt = map_cnt;
            //删除读取map缓存文件
            FileUtil.del(modRAFWriteBean.getLocal_map_read_path());
            //把写map缓存文件重命名为读取map缓存文件
            FileUtil.rename(modRAFWriteBean.getLocal_map_write_path(), modRAFWriteBean.getLocal_map_read_path());
            timeCostUtil.stop();
            //索引数据：raf文件名 | mod
            indexList.add(myRandomAccessFileList.get(i + 1).getFileName() + SP + last_mod);
            logger.info("==步骤【4】：从本地文件读取map缓存，再进一步转换，记录的raf文件：{}，mod：{}，raf处理记录数：{}，map处理记录数：{}，处理耗时：{}",
                    myRandomAccessFileList.get(i + 1).getFileName(), last_mod, raf_cnt, map_cnt, timeCostUtil.getCost());
        }
        //索引数据写入索引文件
        timeCostUtil.start();
        writeIndexFile(indexList);
        timeCostUtil.stop();
        logger.info("==步骤【5】：索引数据写入索引文件，文件名：{}，处理耗时：{}", modRAFWriteBean.getLocal_index_path(), timeCostUtil.getCost());
    }

    @Override
    public void release() throws Throwable {
        logger.info("==步骤【完成】：释放hadoop资源，释放RandomAccessFile资源");
        if (hdfsTool != null) {
            hdfsTool.closeFileSystem();
        }
        if (myRandomAccessFileList != null) {
            for (MyRandomAccessFile myRandomAccessFile : myRandomAccessFileList) {
                myRandomAccessFile.close();
            }
        }
    }

    /**
     * 读取HDFS并进行处理
     *
     * @param path
     * @param iFileRead
     * @throws Exception
     */
    private void readHdfs(String path, IFileRead iFileRead, int threadNum) throws Exception {
        if (path != null && path.length() > 0) {
            FileUtil fileUtil = new FileUtil();
            try {
                fileUtil.setReader(hdfsTool.openFile(path));
                if (threadNum > 0) fileUtil.read(iFileRead, threadNum);
                else fileUtil.read(iFileRead);
            } finally {
                fileUtil.closeRead();
            }
        }
    }

    /**
     * 读取本地文件并进行处理
     *
     * @param path
     * @param iFileRead
     * @throws Exception
     */
    private void readLocal(String path, IFileRead iFileRead) throws Exception {
        if (path != null && path.length() > 0) {
            FileUtil fileLocalUtil = new FileUtil();
            try {
                fileLocalUtil.setReader(path);
                if (threadNum > 0) fileLocalUtil.read(iFileRead, threadNum);
                else fileLocalUtil.read(iFileRead);
            } finally {
                fileLocalUtil.closeRead();
            }
        }
    }

    /**
     * 数据处理
     *
     * @param mod_cnt
     * @param myRandomAccessFile
     * @param fileUtil
     * @return
     */
    private FileCount dealContent(long mod_cnt, MyRandomAccessFile myRandomAccessFile, FileUtil fileUtil) {
        return new FileCount() {
            @Override
            public void run(String str) throws IOException {
                String[] arr = str.split("\\|", -1);
                //450009493554750|13859494500|1
                String imsi = arr[0];
                String msisdn = arr[1];
                if (msisdn != null && msisdn.length() > 0) {
                    //计算mod
                    long mod = Long.valueOf(msisdn) % mod_cnt;//直接用msisdn来计算mod
                    //计算下位置，从raf读值
                    long pos = (mod + 1) * 15;
                    String read_imsi = myRandomAccessFile.read(pos, 15);
                    boolean isToMap = false;
                    if (read_imsi.equals(NULL_VALUE)) {//没有从raf取到数据
                        //写入raf
                        if (myRandomAccessFile.write(pos, imsi)) {
                            //计数
                            count("raf_cnt");
                        } else {
                            isToMap = true;
                        }
                    } else {//有从raf取到数据
                        isToMap = true;
                    }
                    //写到map文件
                    if (isToMap) {
                        count("map_cnt");
                        String _tmp = imsi + SP + msisdn + LINE;
                        fileUtil.write(_tmp);
                    }
                }
            }
        };
    }

    /**
     * 写索引文件
     *
     * @param contents
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    private void writeIndexFile(List<String> contents) throws FileNotFoundException, UnsupportedEncodingException {
        FileUtil fileUtil = new FileUtil();
        try {
            fileUtil.createFile(modRAFWriteBean.getLocal_index_path(), "UTF-8");
            for (String s : contents) {
                fileUtil.write(s + LINE);
            }
        } finally {
            fileUtil.closeWrite();
        }
    }

}
