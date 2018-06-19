package com.yunchen.trident;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.trident.spout.ITridentSpout;
import org.apache.storm.tuple.Fields;

import java.util.Map;

public class DiagnosisEventSpout  implements ITridentSpout<Long> {

    private  static  final  long serialVersionUID=1L;
    private  SpoutOutputCollector spoutOutputCollector;
    BatchCoordinator<Long>  coordinator=new DefaultCoordinator();//负责管理批次和元数据
    Emitter<Long> emitter=new DiagnosisEventEmitter();//负责发送tuple


    @Override
    public BatchCoordinator<Long> getCoordinator(String txStateId, Map conf, TopologyContext context) {
        return coordinator;
    }

    @Override
    public Emitter<Long> getEmitter(String txStateId, Map conf, TopologyContext context) {
        return emitter;
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public Fields getOutputFields() {
        return new Fields("event");//声明发送哪些字段
    }
}
