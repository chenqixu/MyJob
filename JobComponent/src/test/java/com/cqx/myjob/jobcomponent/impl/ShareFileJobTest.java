package com.cqx.myjob.jobcomponent.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareFileJobTest {
    private static final Logger logger = LoggerFactory.getLogger(ShareFileJobTest.class);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void run() {
        String msisdn = "13509323824";
        String msisdn_head = msisdn.substring(0, 3);
        String msisdn_index = msisdn.substring(3);
        logger.info("msisdn_head：{}，msisdn_index：{}", msisdn_head, msisdn_index);
    }
}