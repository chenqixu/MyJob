package com.cqx.myjob.jobcomponent.impl;

import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.file.IFileRead;
import com.cqx.common.utils.file.MyRandomAccessFile;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.myjob.jobcomponent.bean.RAFReadBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ModRAFRead
 * <pre>
 *     init：读取索引文件，加载map缓存
 *     get：
 *          先从map缓存取数
 *          取不到从raf缓存列表获取，先mod，然后从raf读取
 * </pre>
 *
 * @author chenqixu
 */
public class ModRAFRead {

    private static final Logger logger = LoggerFactory.getLogger(ModRAFRead.class);
    private static final byte[] NULL_BYTE = new byte[15];
    private static final String NULL_VALUE = new String(NULL_BYTE);
    private Map<String, String> cacheMap;
    private LinkedHashMap<MyRandomAccessFile, Long> myRandomAccessFiles;

    /**
     * 初始化
     *
     * @param rafReadBean
     * @throws Exception
     */
    public void init(RAFReadBean rafReadBean) throws Exception {
        //读取索引文件
        Map<String, String> indexMap = readMap(rafReadBean.getLocal_index_path(), true);
        //初始化本地映像文件
        myRandomAccessFiles = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : indexMap.entrySet()) {
            myRandomAccessFiles.put(new MyRandomAccessFile(entry.getKey()), Long.valueOf(entry.getValue()));
        }
        //初始化map缓存
        cacheMap = readMap(rafReadBean.getLocal_map_read_path(), false);
    }

    /**
     * 读取文件到map缓存中
     *
     * @param filename
     * @return
     * @throws Exception
     */
    private Map<String, String> readMap(String filename, boolean issort) throws Exception {
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        timeCostUtil.start();
        FileUtil fileUtil = new FileUtil();
        Map<String, String> map;
        if (issort) {
            map = new LinkedHashMap<>();
        } else {
            map = new HashMap<>();
        }
        try {
            fileUtil.getFile(filename, "UTF-8");
            fileUtil.read(new IFileRead() {
                @Override
                public void run(String content) throws IOException {
                    //切割字符串，存入map
                    String[] arr = content.split("\\|", -1);
                    map.put(arr[0], arr[1]);
                }

                @Override
                public void tearDown() throws IOException {
                }
            });
        } finally {
            fileUtil.closeRead();
        }
        timeCostUtil.stop();
        logger.info("==索引初始化：读取文件：{}，处理耗时：{}", filename, timeCostUtil.getCost());
        return map;
    }

    /**
     * 取值
     *
     * @param key
     * @return
     * @throws IOException
     */
    public String getValue(String key) throws IOException {
        //先从map缓存取数
        String value = cacheMap.get(key);
        //取不到从raf缓存列表获取，先mod，然后从raf读取
        if (value == null) {
            for (Map.Entry<MyRandomAccessFile, Long> entry : myRandomAccessFiles.entrySet()) {
                //计算mod
                long mod = Long.valueOf(key) % entry.getValue();//直接用msisdn来计算mod
                //计算下位置，从raf读值
                long pos = (mod + 1) * 15;
                String read_imsi = entry.getKey().read(pos, 15);
                if (read_imsi.equals(NULL_VALUE)) {//没有从raf取到数据
                } else {
                    return read_imsi;
                }
            }
        }
        return value;
    }

    /**
     * 资源释放
     *
     * @throws IOException
     */
    public void release() throws IOException {
        for (Map.Entry<MyRandomAccessFile, Long> entry : myRandomAccessFiles.entrySet()) {
            entry.getKey().close();
        }
    }
}
