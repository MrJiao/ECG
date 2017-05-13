package edu.tjlg.ecg_tester.utils;

import android.util.Log;


/**
 * Created by Jackson on 2016/6/12.
 * Version : 1
 * Details : 打印日志工具类
 */
public class L {
    private static final boolean debug = true;
    private static final ThreadLocal<StringBuilder> tl = new ThreadLocal<StringBuilder>();
    private static final String myTag = " debug_1.04";

    private static StringBuilder getStringBuilder() {
        StringBuilder sb = tl.get();
        if(sb==null){
            sb = new StringBuilder();
            tl.set(sb);
        }
        sb.setLength(0);
        return sb;
    }


    public static void i(String tag, Object...objs){
        if(debug){
            StringBuilder sb = appendBaseMsg(objs);
            Log.i(tag,sb.toString());
        }
    }

    public static void e(String tag, Object...objs){
        if(debug){
            StringBuilder sb = appendBaseMsg(objs);
            Log.e(tag,sb.toString());
        }
    }

    private static StringBuilder appendBaseMsg(Object...objs){
        final StringBuilder sb = getStringBuilder();
        appendThreadName(sb);
        appendTag(sb);
        for(Object o:objs){
            if(o!=null)
                sb.append(o);
            else
                sb.append("null");
            sb.append(" ");
        }
        return sb;
    }

    private static StringBuilder appendThreadName(StringBuilder sb){
        String ThreadName = Thread.currentThread().getName();
        sb.append(ThreadName).append(" ");
        return sb;
    }

    private static StringBuilder appendTag(StringBuilder sb){
        sb.append(myTag).append(" ");
        return sb;
    }

    private static String getTag(Object obj){
        if(obj instanceof String){
            return (String) obj;
        }
        return obj.getClass().getCanonicalName();
    }

}
