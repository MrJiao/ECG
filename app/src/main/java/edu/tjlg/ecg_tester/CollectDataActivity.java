package edu.tjlg.ecg_tester;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import edu.tjlg.ecg_tester.application.ECGApplication;
import edu.tjlg.ecg_tester.common.CustomProgressDialog;
import edu.tjlg.ecg_tester.task.GetBlueToothECGDataTask;
import edu.tjlg.ecg_tester.utils.Logger;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CollectDataActivity extends Activity {

    //	private Calendar mCalendar;
    //	private String fileId;
    //	private SimpleDateFormat simpleDateFormat;
    //	private String beginTime;

    //	private File fi = null;//
    //	private File fidirectory = null;//
    //	private FileOutputStream fos = null;//

    private TextView mTextView;
    private RelativeLayout connectECGBtn;
    private RelativeLayout measureECGBtn;
    private TextView connectTv;

    private GetBlueToothECGDataTask getBlueToothECGDataTask;
    public static ECGApplication mApplication;
    private ProgressBar progressBarHorizontal;

    static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    public static BluetoothSocket btSocket;
    private BluetoothAdapter btAdapt;
    private String ECGDeviceName = "HMSoft";
    private String ECGDeviceAddress;
    private boolean ECGDeviceFlag = false;
    private boolean btAdaptStartDiscoveryFlag = false;
    private Timer timer = new Timer();

    private Logger logger = new Logger("CollectDataActivity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_data);
        mApplication = (ECGApplication) this.getApplication();

        mTextView = (TextView) findViewById(R.id.collect_textview);
        connectECGBtn = (RelativeLayout) findViewById(R.id.connect_ecg_rl);
        connectTv = (TextView) findViewById(R.id.connect_ecg_tv);
        measureECGBtn = (RelativeLayout) findViewById(R.id.measure_ecg_rl);
        measureECGBtn.setVisibility(View.GONE);
        progressBarHorizontal = (ProgressBar) findViewById(R.id.draft_progress_bar);

        mTextView.setText("正在连接蓝牙...");


        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 注册Receiver来获取蓝牙设备相关的结果
        registerReceiver(searchDevices, intent);

        // 初始化本机蓝牙功能
        btAdapt = BluetoothAdapter.getDefaultAdapter();
        if (btAdapt.getState() == BluetoothAdapter.STATE_OFF) {// 如果蓝牙还没开启
            btAdapt.enable();
            ECGApplication.getInstance().setBtSocketConnectFlag(false);
            //			Toast.makeText(CollectDataActivity.this, "请先打开蓝牙", Toast.LENGTH_LONG).show();
        }
        if (mApplication.getBtSocketConnectFlag()) {
            measureECGBtn.setVisibility(View.VISIBLE);
            connectTv.setText("断开连接");
            mTextView.setText("蓝牙连接成功！");
        } else {
            btAdaptStartDiscoveryFlag = btAdapt.startDiscovery();
        }

        timer.schedule(task, 10000);
        connectECGBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (mApplication.getBtSocketConnectFlag()) {
                    try {
                        CollectDataActivity.btSocket.close();
                        ECGApplication.getInstance().setBtSocketConnectFlag(false);
                        connectTv.setText("点击重新连接");
                        measureECGBtn.setVisibility(View.GONE);
                        mTextView.setText("蓝牙连接已断开！");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    mTextView.setText("正在连接蓝牙...");
                    btAdapt.startDiscovery();
                }
            }
        });

        measureECGBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if (mApplication.getBtSocketConnectFlag()) {
                    mTextView.setText("采集中......");

                    //String [] params = {fi.toString()};
                    //progressDialog = CustomProgressDialog.createDialog(CollectDataActivity.this);
                    //这里采用AsyncTask异步线程方式接受数据有点不好，使用Thread+handler
                    logger.e("开启getBlueToothECGDataTask 任务");
                    getBlueToothECGDataTask = new GetBlueToothECGDataTask(CollectDataActivity.this, progressBarHorizontal);
                    getBlueToothECGDataTask.execute();
                } else {

                }

            }
        });
        //		contectECGDevice();
        //
        //		creatECGFile();
        //		mTextView = (TextView)findViewById(R.id.collect_textview);
        //		mTextView.setText("采集中......");
        //
        //		String [] params = {fi.toString()};
        //		progressDialog = CustomProgressDialog.createDialog(this);
        //		getBlueToothECGDataTask = new GetBlueToothECGDataTask(this, progressDialog);
        //		getBlueToothECGDataTask.execute(params);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Builder builder = new Builder(CollectDataActivity.this);
                //				builder.setIcon(R.drawable.toast_icon);
                builder.setTitle("心电监测系统");
                builder.setMessage("没法发现任何蓝牙设备，请确定是否开启心电采集设备的蓝牙？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                builder.create();
                builder.show();
            }
            super.handleMessage(msg);
        }

        ;
    };
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            if (btAdaptStartDiscoveryFlag && connectTv.getText().equals("连接心电设备"))
                message.what = 1;
            handler.sendMessage(message);
        }
    };
    private final BroadcastReceiver searchDevices = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            Log.e("", "---- 广播响应！");
            String action = intent.getAction();
            //			mTextView.setText("蓝牙连接中......");
            //搜索设备时，取得设备的MAC地址
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i("device.getName()", "---------      " + device.getName());
                if (device.getName() != null)
                    if (device.getName().equals(ECGDeviceName)) {
                        ECGDeviceAddress = device.getAddress();
                        ECGDeviceFlag = true;
                        Log.i("ECGBlueToothDeviceStr", "---------      " + ECGDeviceAddress);
                        connectECGDevice();
                    }
            } else {
                connectTv.setText("点击重新连接");
                Toast.makeText(CollectDataActivity.this, "没有发现心电采集设备，连接失败，请重新点击连接！", Toast.LENGTH_LONG).show();
            }
        }
    };

    private boolean connectECGDevice() {
        logger.e("connectECGDevice");
        Log.e("", "---- 准备配对！");
        btAdapt.cancelDiscovery();
        UUID uuid = UUID.fromString(SPP_UUID);
        BluetoothDevice btDev = btAdapt.getRemoteDevice(ECGDeviceAddress);
        try {
            btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
            btSocket.connect();
            logger.e("btSocket 初始化成功");
            ECGApplication.getInstance().setBtSocketConnectFlag(true);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mTextView.setText("蓝牙连接失败！");
            connectTv.setText("点击重新连接");
            ECGApplication.getInstance().setBtSocketConnectFlag(false);
            Toast.makeText(CollectDataActivity.this, "连接失败，请重新点击连接！", Toast.LENGTH_LONG).show();
        }
        if (mApplication.getBtSocketConnectFlag()) {
            measureECGBtn.setVisibility(View.VISIBLE);
            connectTv.setText("断开连接");
            mTextView.setText("蓝牙连接成功！");
            return true;
        } else {
            try {
                Log.e("", "尝试关闭Socket");
                btSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Toast.makeText(CollectDataActivity.this, "没有发现心电采集设备，连接失败，请重新点击连接！", Toast.LENGTH_LONG).show();
        return false;
    }

    /**
     private void creatECGFile(){
     //创建文件
     fidirectory = new File("sdcard/ECGBlueToothFile/");
     if(!fidirectory.exists()){
     fidirectory.mkdir();
     }
     mCalendar = Calendar.getInstance();
     //日期格式
     simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
     //用时间命名文件名
     fileId = ""+mCalendar.get(Calendar.YEAR)+(mCalendar.get(Calendar.MONTH)+1)+
     +mCalendar.get(Calendar.DAY_OF_MONTH)+ mCalendar.get(Calendar.HOUR_OF_DAY)+
     +mCalendar.get(Calendar.MINUTE)+mCalendar.get(Calendar.SECOND);
     beginTime = simpleDateFormat.format(mCalendar.getTime()).toString();
     String filestr = "";
     filestr += "sdcard/ECGBlueToothFile/" +mApplication.getPhoneNum();
     filestr += "_";
     filestr += fileId ;
     filestr+= ".txt";
     fi = new File(filestr);
     if(!fi.exists()){
     System.out.println("creating it");
     try{
     fi.createNewFile();
     }
     catch(IOException e){
     e.printStackTrace();
     System.out.println(e.toString());
     }
     }

     //将用户信息数据以及测量时间封装成JSON数据格式写入文件
     String testInfoStr = getJsonStr();
     testInfoStr += "##";
     try {
     byte[] testInfoBytes = testInfoStr.getBytes();
     fos=new FileOutputStream(fi.getAbsolutePath(),true);
     //output file
     fos.write(testInfoBytes);
     //flush this stream
     fos.flush();
     //close this stream
     fos.close();
     } catch (IOException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
     }

     }
     */
    /**
     private String getJsonStr(){
     JSONObject testInfoJSONObject = new JSONObject();
     try {
     testInfoJSONObject.put("name",mApplication.getName());
     testInfoJSONObject.put("gender", mApplication.getGender());
     testInfoJSONObject.put("age", mApplication.getAge());
     testInfoJSONObject.put("phoneNum", mApplication.getPhoneNum());
     testInfoJSONObject.put("illness", mApplication.getIllness());
     testInfoJSONObject.put("beginTime", beginTime);
     return testInfoJSONObject.toString();
     } catch (JSONException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
     return null;
     }
     }
     */
    /**
     * 字符串转换成十六进制字符串
     */
    /**
     * public static String str2HexStr(String str) {
     * char[] chars = "0123456789ABCDEF".toCharArray();
     * StringBuilder sb = new StringBuilder("");
     * byte[] bs = str.getBytes();
     * int bit;
     * for (int i = 0; i < bs.length; i++) {
     * bit = (bs[i] & 0x0f0) >> 4;
     * sb.append(chars[bit]);
     * bit = bs[i] & 0x0f;
     * sb.append(chars[bit]);
     * }
     * return sb.toString();
     * }
     */
    //键盘监听
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            //			if(ECGDeviceContectSuccessFlag){
            //				try {
            //					CollectDataActivity.btSocket.close();
            //				} catch (IOException e1) {
            //					// TODO Auto-generated catch block
            //					e1.printStackTrace();
            //				}
            //			}

            Intent intent = new Intent();
            intent.setClass(CollectDataActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return false;
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        this.unregisterReceiver(searchDevices);
        super.onDestroy();
    }


}

