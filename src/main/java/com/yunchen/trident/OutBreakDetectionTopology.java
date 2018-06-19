package com.yunchen.trident;

import org.apache.storm.generated.StormTopology;
import org.apache.storm.trident.TridentTopology;

public class OutBreakDetectionTopology {
    public  static StormTopology buildTopology(){
        TridentTopology tridentTopology = new TridentTopology();
        DiagnosisEventSpout diagnosisEventSpout = new DiagnosisEventSpout();
        //filter  for critical events关键事件过滤器
        //locate the closest city 找到最近的城市
        //derive the hour segment 推导小时段
        //group occurrences in same city and hour 同城时群
        //count occurrences and persist the results 计数发生并保持结果
        //detect and outbreak  检测与暴发
        return null;
    }
}
