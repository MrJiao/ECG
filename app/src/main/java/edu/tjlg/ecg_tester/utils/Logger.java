package edu.tjlg.ecg_tester.utils;

/**
 * Created by Jackson on 2016/11/23.
 * Version : 1
 * Details :
 */
public class Logger {
    private final String tag ;
    public Logger(String tag){
        this.tag = tag;
    }

    public  void i(Object... objects){
        L.i(tag,objects);
    }

    public void e(Object... objects){
        L.e(tag,objects);
    }
}
