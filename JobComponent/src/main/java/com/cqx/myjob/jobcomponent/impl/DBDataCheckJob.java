package com.cqx.myjob.jobcomponent.impl;

import com.cqx.common.utils.mapreduce.JobBuilder;
import com.cqx.myjob.jobcomponent.base.BaseJob;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.Map;

/**
 * 对准备加载到数据库的文件进行预处理校验，在YARN上运行
 *
 * @author chenqixu
 */
public class DBDataCheckJob extends BaseJob {
    @Override
    public void init(Map<String, String> param) throws Throwable {

    }

    @Override
    public void run() throws Throwable {
        boolean status = JobBuilder.newbuilder()
                .buildConf("/cmss/bch/bc/hadoop/etc/hadoop75/")
                .buildFileSystem()
                .buildJob(DBDataCheckJob.class, "")
                .addInputPath("hdfs://master75/cqx/data/mrinput/")
                .deleteAndSetOutPutPath("hdfs://master75/cqx/data/mroutput/", TextOutputFormat.class)
                .setMapperClass(JobMap.class)
                .setOutputKeyValueClass(NullWritable.class, Text.class)
                .setNumReduceTasks(0)
                .waitForCompletion();
        System.out.println(String.format("status：%s", status));
    }

    @Override
    public void release() throws Throwable {

    }

    static class JobMap extends Mapper<LongWritable, Text, NullWritable, Text> {

        protected void setup(Context context) throws IOException, InterruptedException {
        }

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            context.write(NullWritable.get(), value);
        }
    }
}
