package edu.tjlg.ecg_tester.jackson.collection;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import edu.tjlg.ecg_tester.CollectDataActivity;
import edu.tjlg.ecg_tester.R;
import edu.tjlg.ecg_tester.jackson.event.BluetoothEvent;
import edu.tjlg.ecg_tester.task.GetBlueToothECGDataTask;

/**
 * Created by jackson on 2017/5/13.
 */

public class CollectionPresenter {

    private final CollectionContract.View view;
    private final BluetoothManager bluetoothManager;

    public static CollectionPresenter newInstance(CollectionContract.View view){
        return new CollectionPresenter(view);
    }

    public CollectionPresenter(CollectionContract.View view){
        this.view= view;
        bluetoothManager = BluetoothManager.getInstance();
    }

    //断开连接
    public void disConnect() {
        bluetoothManager.disConnect();
    }

    //开始测量
    public void measuring(Context context,ProgressBar progressBarHorizontal) {
        new GetBlueToothECGDataTask2(context, progressBarHorizontal,bluetoothManager.getBtSocket()).execute();
        view.showCollecting();
    }

    //连接蓝牙
    public void connect() {
        bluetoothManager.searchDevices();
    }

    //搜索蓝牙连接
    public void initBluetooth(Context context) {
        bluetoothManager.registeBroadcast(context);
        bluetoothManager.searchDevices();
    }

    public void release(Context context){
        view.showDisConnect();
        bluetoothManager.release(context);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BluetoothEvent event){
        switch (event.state){
            case BluetoothEvent.BLUETOOTH_DISABLED:
                view.showBluetoothDisabled();
                bluetoothManager.setConnecting(false);
                break;
            case BluetoothEvent.FIND_DEVICE:
                view.showFindDevice();
                break;
            case BluetoothEvent.CONNECTING:
                view.showConnecting();
                break;
            case BluetoothEvent.CONNECTING_TIME_OUT:
                view.showConnectError();
                bluetoothManager.setConnecting(false);
                break;
            case BluetoothEvent.CONNECTING_ERROR:
                view.showConnectError();
                bluetoothManager.setConnecting(false);
                break;
            case BluetoothEvent.CONNECTING_SUCCESS:
                view.showConnectSuccess();
                break;
            case BluetoothEvent.DISCONNECT:
                view.showDisConnect();
                bluetoothManager.setConnecting(false);
                break;
        }
    }
}
