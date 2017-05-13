package edu.tjlg.ecg_tester.jackson.collection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.SocketHandler;

import edu.tjlg.ecg_tester.jackson.event.BluetoothEvent;
import edu.tjlg.ecg_tester.utils.Logger;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static android.R.attr.action;

/**
 * Created by jackson on 2017/5/13.
 */

public class BluetoothManager {

    static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private String ECGDeviceName = "HMSoft";

    Logger logger = new Logger("BluetoothManager");

    private BluetoothAdapter btAdapt;

    public BluetoothSocket btSocket;

    private boolean isConnecting ;

    private BluetoothManager(){}

    private static BluetoothManager instance = new BluetoothManager();

    public static BluetoothManager getInstance(){
        return instance;
    }

    public boolean isConnecting(){
        return isConnecting;
    }

    public void setConnecting(boolean isConnecting){
        this.isConnecting= isConnecting;
    }



    public void searchDevices(){
        EventBus.getDefault().post(new BluetoothEvent(BluetoothEvent.SEARCHING));
        btAdapt = BluetoothAdapter.getDefaultAdapter();
        if (btAdapt.getState() == BluetoothAdapter.STATE_OFF) {// 如果蓝牙还没开启
            btAdapt.enable();
            EventBus.getDefault().post(new BluetoothEvent(BluetoothEvent.BLUETOOTH_DISABLED));
            return;
        }
        if(!btAdapt.isDiscovering()){
            btAdapt.startDiscovery();
            timing();
        }
    }

    public void registeBroadcast(Context context){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(searchDevices,intentFilter);
    }

    public void release(Context context){
        isConnecting = false;
        context.unregisterReceiver(searchDevices);
    }

    public void disConnect(){
        try {
            if(btSocket!=null)
            btSocket.close();
        } catch (IOException e) {
            logger.e("断开蓝牙失败",e);
        }
    }

    public BluetoothSocket getBtSocket(){
        return btSocket;
    }


    private final BroadcastReceiver searchDevices = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            if(isConnecting)return;
            String action = intent.getAction();
            //搜索设备时，取得设备的MAC地址
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device!= null || !TextUtils.isEmpty(device.getName()))
                    logger.e("发现设备",device.getName());
                    if(TextUtils.equals(device.getName(),ECGDeviceName)){
                        String address = device.getAddress();
                        EventBus.getDefault().post(new BluetoothEvent(BluetoothEvent.FIND_DEVICE));
                        btAdapt.cancelDiscovery();
                        connectECGDevice(address);
                    }
            }
  /*          else{
                logger.e("没有发现设备");
                EventBus.getDefault().post(new BluetoothEvent(BluetoothEvent.NOT_FIND_DEVICE));
            }*/
        }
    };

    private void connectECGDevice(String address){
        EventBus.getDefault().post(new BluetoothEvent(BluetoothEvent.CONNECTING));
        logger.e("", "---- 准备配对！");
        UUID uuid = UUID.fromString(SPP_UUID);
        BluetoothDevice btDev = btAdapt.getRemoteDevice(address);
        try {
            btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
            btSocket.connect();
        } catch (IOException e) {
            EventBus.getDefault().post(new BluetoothEvent(BluetoothEvent.CONNECTING_ERROR));
            try {
                logger.e("", "连接失败 尝试关闭Socket",e);
                if(btSocket!=null)
                    btSocket.close();
            } catch (IOException ee) {
                logger.e("关闭失败",ee);
                return;
            }
        }
        isConnecting = true;
        EventBus.getDefault().post(new BluetoothEvent(BluetoothEvent.CONNECTING_SUCCESS));
    }


    private void timing(){
        Observable.timer(10, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long aLong) throws Exception {
                if(!isConnecting){
                    EventBus.getDefault().post(new BluetoothEvent(BluetoothEvent.CONNECTING_TIME_OUT));
                }
            }
        });
    }

}
