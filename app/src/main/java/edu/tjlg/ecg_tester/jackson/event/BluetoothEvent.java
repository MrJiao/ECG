package edu.tjlg.ecg_tester.jackson.event;

/**
 * Created by jackson on 2017/5/13.
 */

public class BluetoothEvent {

    public BluetoothEvent(int state){
        this.state = state;
    }

    public static final int BLUETOOTH_DISABLED= 1;//蓝牙不可用
    public static final int SEARCHING= 1;//正在搜索
    public static final int FIND_DEVICE= 2;//发现蓝牙设备
    public static final int CONNECTING= 3;//正在连接
    public static final int CONNECTING_TIME_OUT= 4;//连接超时
    public static final int CONNECTING_ERROR= 5;//连接异常
    public static final int CONNECTING_SUCCESS= 6;//连接成功
    public static final int DISCONNECT= 7;//断开连接
    public static final int COLLECT_FINISHED = 8;

    public int state;

}
