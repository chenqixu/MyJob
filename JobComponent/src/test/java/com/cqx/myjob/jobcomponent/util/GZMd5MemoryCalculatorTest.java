package com.cqx.myjob.jobcomponent.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GZMd5MemoryCalculatorTest {

    private static final Logger logger = LoggerFactory.getLogger(GZMd5MemoryCalculatorTest.class);
    private GZMd5MemoryCalculator gzMd5MemoryCalculator;

    @Before
    public void setUp() throws Exception {
        gzMd5MemoryCalculator = new GZMd5MemoryCalculator();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void write() throws Exception {
        gzMd5MemoryCalculator.write_flush("123");
        String md5 = gzMd5MemoryCalculator.digest();
        int fileSize = gzMd5MemoryCalculator.fileSize();
        logger.info("md5：{}，fileSize：{}", md5, fileSize);
    }
}