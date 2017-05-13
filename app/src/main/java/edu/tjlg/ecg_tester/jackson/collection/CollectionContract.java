package edu.tjlg.ecg_tester.jackson.collection;

/**
 * Created by jackson on 2017/5/13.
 */

public class CollectionContract {

    interface View{

        void showConnecting();//正在连接

        void showConnectSuccess();//连接成功

        void showConnectError();//连接失败

        void showCollecting();//正在采集

        void showBluetoothDisabled();//蓝牙不可用

        void showFindDevice();//发现设备

        void showDisConnect();//断开连接

    }


}
