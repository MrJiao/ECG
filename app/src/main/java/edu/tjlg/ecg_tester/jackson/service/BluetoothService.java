package edu.tjlg.ecg_tester.jackson.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by jackson on 2017/5/13.
 */

public class BluetoothService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }





}
