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

        mTextView.setText("������������...");


        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);// ��BroadcastReceiver��ȡ���������
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // ע��Receiver����ȡ�����豸��صĽ��
        registerReceiver(searchDevices, intent);

        // ��ʼ��������������
        btAdapt = BluetoothAdapter.getDefaultAdapter();
        if (btAdapt.getState() == BluetoothAdapter.STATE_OFF) {// ���������û����
            btAdapt.enable();
            ECGApplication.getInstance().setBtSocketConnectFlag(false);
            //			Toast.makeText(CollectDataActivity.this, "���ȴ�����", Toast.LENGTH_LONG).show();
        }
        if (mApplication.getBtSocketConnectFlag()) {
            measureECGBtn.setVisibility(View.VISIBLE);
            connectTv.setText("�Ͽ�����");
            mTextView.setText("�������ӳɹ���");
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
                        connectTv.setText("�����������");
                        measureECGBtn.setVisibility(View.GONE);
                        mTextView.setText("���������ѶϿ���");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    mTextView.setText("������������...");
                    btAdapt.startDiscovery();
                }
            }
        });

        measureECGBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if (mApplication.getBtSocketConnectFlag()) {
                    mTextView.setText("�ɼ���......");

                    //String [] params = {fi.toString()};
                    //progressDialog = CustomProgressDialog.createDialog(CollectDataActivity.this);
                    //�������AsyncTask�첽�̷߳�ʽ���������е㲻�ã�ʹ��Thread+handler
                    logger.e("����getBlueToothECGDataTask ����");
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
        //		mTextView.setText("�ɼ���......");
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
                builder.setTitle("�ĵ���ϵͳ");
                builder.setMessage("û�������κ������豸����ȷ���Ƿ����ĵ�ɼ��豸��������");
                builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
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
            // ��Ҫ������:������Ϣ
            Message message = new Message();
            if (btAdaptStartDiscoveryFlag && connectTv.getText().equals("�����ĵ��豸"))
                message.what = 1;
            handler.sendMessage(message);
        }
    };
    private final BroadcastReceiver searchDevices = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            Log.e("", "---- �㲥��Ӧ��");
            String action = intent.getAction();
            //			mTextView.setText("����������......");
            //�����豸ʱ��ȡ���豸��MAC��ַ
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
                connectTv.setText("�����������");
                Toast.makeText(CollectDataActivity.this, "û�з����ĵ�ɼ��豸������ʧ�ܣ������µ�����ӣ�", Toast.LENGTH_LONG).show();
            }
        }
    };

    private boolean connectECGDevice() {
        logger.e("connectECGDevice");
        Log.e("", "---- ׼����ԣ�");
        btAdapt.cancelDiscovery();
        UUID uuid = UUID.fromString(SPP_UUID);
        BluetoothDevice btDev = btAdapt.getRemoteDevice(ECGDeviceAddress);
        try {
            btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
            btSocket.connect();
            logger.e("btSocket ��ʼ���ɹ�");
            ECGApplication.getInstance().setBtSocketConnectFlag(true);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mTextView.setText("��������ʧ�ܣ�");
            connectTv.setText("�����������");
            ECGApplication.getInstance().setBtSocketConnectFlag(false);
            Toast.makeText(CollectDataActivity.this, "����ʧ�ܣ������µ�����ӣ�", Toast.LENGTH_LONG).show();
        }
        if (mApplication.getBtSocketConnectFlag()) {
            measureECGBtn.setVisibility(View.VISIBLE);
            connectTv.setText("�Ͽ�����");
            mTextView.setText("�������ӳɹ���");
            return true;
        } else {
            try {
                Log.e("", "���Թر�Socket");
                btSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Toast.makeText(CollectDataActivity.this, "û�з����ĵ�ɼ��豸������ʧ�ܣ������µ�����ӣ�", Toast.LENGTH_LONG).show();
        return false;
    }

    /**
     private void creatECGFile(){
     //�����ļ�
     fidirectory = new File("sdcard/ECGBlueToothFile/");
     if(!fidirectory.exists()){
     fidirectory.mkdir();
     }
     mCalendar = Calendar.getInstance();
     //���ڸ�ʽ
     simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
     //��ʱ�������ļ���
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

     //���û���Ϣ�����Լ�����ʱ���װ��JSON���ݸ�ʽд���ļ�
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
     * �ַ���ת����ʮ�������ַ���
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
    //���̼���
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

