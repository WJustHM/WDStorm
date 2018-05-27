package com.yunchen.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;

/**
 * Created by gxy on 17-8-11.
 */
public class LogUtil {

    private volatile static LogUtil INSTANCE;

    public static LogUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (LogUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LogUtil();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 获取异常信息
     *
     * @param t
     * @return 异常信息
     */
    public static String getTrace(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer = stringWriter.getBuffer();
        return buffer.toString();
    }

    /**
     *   快速打印集合类和映射类数据
     * @author LiuJihao
     * @date 2018/5/10 13:46
     * @param [o]
     * @return void
     **/
    public static void printSomething(Object o) {
        StringBuffer sb = new StringBuffer();
        System.out.println(printSomething(o, sb));
    }

    private static String printSomething(Object o, StringBuffer sb) {

        if (o instanceof Collection) {
            sb.append("[");
            Collection c = (Collection)o;
            c.forEach(
                    s -> {
                            printSomething(s, sb);
                    }
            );
            sb.append("]").append("\n");
        }
        else if (o instanceof Map) {
            sb.append("{");
            Map m = (Map)o;
            m.forEach(
                    (k,v) -> {
                        sb.append(k).append(":");
                        printSomething(v, sb);

                    }
            );
            sb.append("}").append("\n");
        }
        else {
            sb.append(o.toString()).append(",").append("\n");
        }

        return sb.toString();
    }
}
