package com.yunchen.trident;

import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.spout.ITridentSpout;
import org.apache.storm.trident.topology.TransactionAttempt;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class DiagnosisEventEmitter implements ITridentSpout.Emitter<Long> ,Serializable {
    AtomicInteger atomicInteger=new AtomicInteger(0);
    @Override
    public void emitBatch(TransactionAttempt tx, Long coordinatorMeta, TridentCollector collector) {

    }

    @Override
    public void success(TransactionAttempt tx) {

    }

    @Override
    public void close() {

    }
}
