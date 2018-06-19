package com.yunchen.trident;


import org.apache.storm.trident.spout.ITridentSpout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;


/**
 * @Author djj
 * @Date 2018-06-05 11:21
 * @Description
 * @Version  2018-06-05
 * @Copyright 上海云辰信息科技有限公司
 * 作为一个Storm Bolt运行再一个单线程中
 **/
public class DefaultCoordinator implements ITridentSpout.BatchCoordinator<Long>,Serializable {

    private static  final Logger logger=LoggerFactory.getLogger(DefaultCoordinator.class);
    @Override
    public Long initializeTransaction(long txid, Long prevMetadata, Long currMetadata) {
        logger.info("InitDJJ Transaction" + txid);
        return null;
    }

    @Override
    public void success(long txid) {
     logger.info("successful transaction" + txid);
    }

    @Override
    public boolean isReady(long txid) {
        return true;
    }

    @Override
    public void close() {

    }
}
