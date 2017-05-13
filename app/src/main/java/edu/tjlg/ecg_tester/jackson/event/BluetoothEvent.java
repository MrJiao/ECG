package edu.tjlg.ecg_tester.jackson.event;

/**
 * Created by jackson on 2017/5/13.
 */

public class BluetoothEvent {

    public BluetoothEvent(int state){
        this.state = state;
    }

    public static final int BLUETOOTH_DISABLED= 1;//����������
    public static final int SEARCHING= 1;//��������
    public static final int FIND_DEVICE= 2;//���������豸
    public static final int CONNECTING= 3;//��������
    public static final int CONNECTING_TIME_OUT= 4;//���ӳ�ʱ
    public static final int CONNECTING_ERROR= 5;//�����쳣
    public static final int CONNECTING_SUCCESS= 6;//���ӳɹ�
    public static final int DISCONNECT= 7;//�Ͽ�����
    public static final int COLLECT_FINISHED = 8;

    public int state;

}
