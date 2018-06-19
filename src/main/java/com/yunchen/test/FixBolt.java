package com.yunchen.test;

import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentState;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.fluent.GroupedStream;
import org.apache.storm.trident.operation.BaseFilter;
import org.apache.storm.trident.operation.builtin.Count;
import org.apache.storm.trident.operation.builtin.FilterNull;
import org.apache.storm.trident.operation.builtin.MapGet;
import org.apache.storm.trident.operation.builtin.Sum;
import org.apache.storm.trident.testing.FixedBatchSpout;
import org.apache.storm.trident.testing.MemoryMapState;
import org.apache.storm.trident.testing.Split;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.nio.ByteBuffer;

public class FixBolt {

    private static final String serverLocaltions = "192.168.241.31:11211";

    public void fixSpout() {
        //192.168.241.31:11211
    }

    public static void main(String[] args) {
        FixedBatchSpout fixedBatchSpout = new FixedBatchSpout(new Fields("sentence"), 3,
                new Values("the cow jumped over the moon"),
                new Values("the man went to the store and bought some candy"),
                new Values("four score and seven years ago"),
                new Values("how many apples can you eat"));
        fixedBatchSpout.setCycle(true);
        TridentTopology tridentTopology = new TridentTopology();
        Stream spout1 = tridentTopology.newStream("spout1", fixedBatchSpout);
        Stream each = spout1.each(new Fields("sentence"), new Split(), new Fields("word"));
        Stream each1 = each.each(new Fields("word"), new BaseFilter() {
                    @Override
                    public boolean isKeep(TridentTuple tuple) {
                        String word = tuple.getStringByField("word");
                        if (word.length() > 4) {
                            return true;
                        }
                        return  false;
                    }
                });
        GroupedStream word = each1.groupBy(new Fields("word"));
        TridentState count = word.persistentAggregate(new MemoryMapState.Factory(), new Count(), new Fields("count"));
        TridentState tridentState = count.parallelismHint(6);
        System.out.println(tridentState.toString());


    }
}


