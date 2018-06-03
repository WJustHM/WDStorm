package com.yunchen.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//import backtype.storm.Config;
//import backtype.storm.LocalCluster;
//import backtype.storm.StormSubmitter;
//import backtype.storm.generated.AlreadyAliveException;
//import backtype.storm.generated.AuthorizationException;
//import backtype.storm.generated.InvalidTopologyException;
//import backtype.storm.spout.SpoutOutputCollector;
//import backtype.storm.task.TopologyContext;
//import backtype.storm.topology.BasicOutputCollector;
//import backtype.storm.topology.OutputFieldsDeclarer;
//import backtype.storm.topology.TopologyBuilder;
//import backtype.storm.topology.base.BaseBasicBolt;
//import backtype.storm.topology.base.BaseRichSpout;
//import backtype.storm.tuple.Fields;
//import backtype.storm.tuple.Tuple;
//import backtype.storm.tuple.Values;
//import backtype.storm.utils.Utils;

/**
 * Hello world!
 */
public class WordCountTopolopgyAllInJavaRichBolt {
    private static Logger logger = LogManager.getLogger(WordCountTopolopgyAllInJavaRichBolt.class);

    // 定义一个喷头，用于产生数据。该类继承自BaseRichSpout
    public static class SentenceSpout extends BaseRichSpout {
        //提供发射tuple的方法
        private SpoutOutputCollector _collector;
        private ConcurrentHashMap<UUID, Values> pending;


        //在Spout组件在初始化时调用这个方法
        @Override
        public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
            this._collector = spoutOutputCollector;
            this.pending = new ConcurrentHashMap<>();
        }

        //循环调用
        @Override
        public void nextTuple() {
            // 发射该句子给Bolt
            for (int i = 0; i < 4; i++) {
                Values values = new Values("djj");
                UUID uuid = UUID.randomUUID();
                this.pending.put(uuid, values);
                this._collector.emit(values, uuid);
            }
            Utils.sleep(20000);
        }

        //输出流
        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            // 定义一个字段sentence类似于表名
            outputFieldsDeclarer.declare(new Fields("sentence"));
        }

        @Override
        public void ack(Object msgId) {
            super.ack(msgId);
            System.out.println("移除元素：" + msgId);
            //从当前集合移除成功元素
            this.pending.remove(msgId);
        }

        @Override
        public void fail(Object msgId) {
            super.fail(msgId);
            System.out.println("失败元素：" + msgId);
            this._collector.emit(this.pending.get(msgId), msgId);
        }
    }

    public static class SplitSentenceBolt extends BaseRichBolt {

        private OutputCollector outputCollector;
        private int index=0;

        //在Bolt初始化时调用
        @Override
        public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
            this.outputCollector = collector;
        }

        //每当从订阅的数据流中接收一个Tuple，都会调用这个方法
        @Override
        public void execute(Tuple tuple) {
            try {
                // 接收到一个句子
                String sentence = tuple.getStringByField("sentence");
                // 把句子切割为单词
                StringTokenizer iter = new StringTokenizer(sentence);

                // 发送每一个单词
                while (iter.hasMoreElements()) {
                    index++;
                    System.out.println("djy:"+index);
                    if(index==3){
                        throw new Exception("人为处理失败");
                    }
                    outputCollector.emit(tuple, new Values(iter.nextToken()));
                }
                outputCollector.ack(tuple);
            }catch (Exception e){
                this.outputCollector.fail(tuple);
                e.printStackTrace();
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            //表名
            outputFieldsDeclarer.declare(new Fields("word"));
        }
    }

    public static class WordCountBolt extends BaseBasicBolt {
        private Map<String, Long> counts = null;

        public WordCountBolt() {

        }

        @Override
        public void prepare(Map stormConf, TopologyContext context) {
            //在这个方法中对不可序列化的对象进行实例化
            counts = new HashMap<>();
        }

        @Override
        public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {

            String word = tuple.getStringByField("word");
            Long count = this.counts.get(word);
            if (count == null) {
                count = 0L;
            }
            count++;
            this.counts.put(word, count);
            basicOutputCollector.emit("1", new Values(word, count));
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declareStream("1", new Fields("word", "count"));
        }
    }

    public static class ReportBolt extends BaseBasicBolt {

        Map<String, Long> counts = new HashMap<>();

        @Override
        public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
            String word = tuple.getStringByField("word");
            Long count = tuple.getLongByField("count");
            System.out.println(word + " " + count);
            this.counts.put(word, count);
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

        }

        @Override
        public void cleanup() {
            System.out.println("--------final counts");
            List<String> keys = new ArrayList<>();
            keys.addAll(this.counts.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                System.out.println(key + " : " + this.counts.get(key));
            }
            System.out.println("------------------");
        }
    }

    public static void main(String[] args) {
        String TOPOLOGY_NAME = "WordsRecords";
        // 创建一个拓扑
        TopologyBuilder builder = new TopologyBuilder();

        // 设置Spout，这个Spout的名字叫做"Spout"，设置并行度为2,2个Excetuor
        builder.setSpout("Spout", new SentenceSpout());//一个Spout  component，2个Executor
        // 设置slot——“split”，并行度为2，它的数据来源是spout的，4个taskh和2个executor(线程)
        builder.setBolt("SplitSentenceBolt", new SplitSentenceBolt()).shuffleGrouping("Spout");//一个Spout  component，2个Executor

        // 设置slot——“count”,你并行度为12，它的数据来源是split的word字段
        //一个Spout  component，4个Executor
        builder.setBolt("WordCountBolt", new WordCountBolt()).fieldsGrouping("SplitSentenceBolt", new Fields("word"));//word类似于reduce，同一个单词会落在同一个线程
        builder.setBolt("ReportBolt", new ReportBolt()).shuffleGrouping("WordCountBolt", "1");////一个Spout  component，1个Executor
        Config config = new Config();
        config.setNumWorkers(1);//本地不生效

        try {
            StormSubmitter.submitTopologyWithProgressBar(args[0], config,
                    builder.createTopology());
        } catch (Exception e) {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(TOPOLOGY_NAME, config, builder.createTopology());
            Utils.sleep(20000);
            cluster.killTopology(TOPOLOGY_NAME);
            cluster.shutdown();
        }

    }


}