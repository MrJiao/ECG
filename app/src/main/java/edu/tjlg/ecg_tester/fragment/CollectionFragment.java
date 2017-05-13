package edu.tjlg.ecg_tester.fragment;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import edu.tjlg.ecg_tester.CollectDataActivity;
import edu.tjlg.ecg_tester.MainActivity;
import edu.tjlg.ecg_tester.R;
import edu.tjlg.ecg_tester.application.ECGApplication;
import edu.tjlg.ecg_tester.task.GetBlueToothECGDataTask;
import android.annotation.SuppressLint;
import android.app.Fragment;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static edu.tjlg.ecg_tester.CollectDataActivity.mApplication;

@SuppressLint("NewApi")
public class CollectionFragment extends Fragment{

	private TextView mTitleText;//����
	private TextView mPromptText;//��ʾ
	private Button mConnectECGButton;
	private Button mMeasureECGButton;

	private GetBlueToothECGDataTask getBlueToothECGDataTask;
	private ProgressBar progressBarHorizontal;

	static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	public static BluetoothSocket btSocket;
	private BluetoothAdapter btAdapt;
	private String ECGDeviceName = "HMSoft";
	private String ECGDeviceAddress;
	private boolean ECGDeviceFlag = false;
	private boolean btAdaptStartDiscoveryFlag = false;
	private Timer mTimer = new Timer(true);
	private TimerTask mTask;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.collect_data, null);

		bindView(view);
		setListener();
		initView();
		return view;
	}

	private void bindView(View view) {
		mTitleText = (TextView) view.findViewById(R.id.title_textView);
		mPromptText = (TextView) view.findViewById(R.id.collect_textview);
		mConnectECGButton = (Button) view.findViewById(R.id.connect_ecg_button);
		mMeasureECGButton = (Button) view.findViewById(R.id.measure_ecg_button);
		progressBarHorizontal = (ProgressBar) view.findViewById(R.id.draft_progress_bar);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		getActivity().unregisterReceiver(searchDevices);
		super.onDestroy();
	}
	
	
	
	private void setListener() {
		// TODO Auto-generated method stub
		mConnectECGButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startTimer();
				if (mApplication.getBtSocketConnectFlag()){
					try {
						CollectDataActivity.btSocket.close();
						ECGApplication.getInstance().setBtSocketConnectFlag(false);
						mConnectECGButton.setText("�����������");
						mMeasureECGButton.setEnabled(false);
						mPromptText.setText("���������ѶϿ���");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					mPromptText.setText("������������...");
					btAdapt.startDiscovery();
				}
			}
		});
		
		mMeasureECGButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mApplication.getBtSocketConnectFlag()) {
					mMeasureECGButton.setText("�ɼ���...");
					getBlueToothECGDataTask = new GetBlueToothECGDataTask(getActivity(), progressBarHorizontal);
					getBlueToothECGDataTask.execute();
				}
			}
		});
	}
	
	private void initView() {
		mTitleText.setText("�ĵ�ɼ�");
		mPromptText.setText("����׼����������...");
		mMeasureECGButton.setEnabled(false);
		
		IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_FOUND);// ��BroadcastReceiver��ȡ���������
		intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		// ע��Receiver����ȡ�����豸��صĽ��
		getActivity().registerReceiver(searchDevices, intent);

		// ��ʼ��������������
		btAdapt = BluetoothAdapter.getDefaultAdapter();
		if (btAdapt.getState() == BluetoothAdapter.STATE_OFF) {// ���������û����
			btAdapt.enable();
			ECGApplication.getInstance().setBtSocketConnectFlag(false);
			//			Toast.makeText(CollectDataActivity.this, "���ȴ�����", Toast.LENGTH_LONG).show();
		}
		
		if (mApplication.getBtSocketConnectFlag()){
			mMeasureECGButton.setEnabled(true);;
			mConnectECGButton.setText("�Ͽ�����");
			mPromptText.setText("�������ӳɹ���");
		} else {
			btAdaptStartDiscoveryFlag = btAdapt.startDiscovery();
		}
	}
	
	
	
	
	
	
	
	
	@Override
	public void onResume() {
		super.onResume();
		if (mTask != null){
	    	mTask.cancel();
	    }
	};
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mTask != null){
	    	mTask.cancel();
	    }
	};
	
	Handler handler = new Handler() {  
		public void handleMessage(Message msg) {  
			if (msg.what == 1) {  
				Builder builder = new Builder(getActivity());  
				//				builder.setIcon(R.drawable.toast_icon);
				builder.setTitle("�ĵ���ϵͳ");  
				builder.setMessage("û�������κ������豸����ȷ���Ƿ����ĵ�ɼ��豸��������");  
				builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {  
					public void onClick(DialogInterface dialog, int which) { 
						dialog.dismiss();
					}  
				});  
				builder.create();  
				builder.show(); 
			}  
			super.handleMessage(msg);  
		};  
	};
	
	
	private void startTimer(){
	     if (mTimer != null){
		     if (mTask != null){
		    	mTask.cancel();
		     }
	      
		      mTask = new TimerTask() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message message = new Message();
					if(btAdaptStartDiscoveryFlag)
						message.what = 1;  
					handler.sendMessage(message);  
				}
		      }; 
		      mTimer.schedule(mTask, 10000);
	     }
	 }
	
	private final BroadcastReceiver searchDevices = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			Log.e("", "---- �㲥��Ӧ��");
			String action = intent.getAction();
			//			mTextView.setText("����������......");
			//�����豸ʱ��ȡ���豸��MAC��ַ
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Log.i("device.getName()", "---------      "+device.getName());
				if(device.getName()!=null)
					if(device.getName().equals(ECGDeviceName)){
						ECGDeviceAddress = device.getAddress();
						ECGDeviceFlag = true;
						Log.i("ECGBlueToothDeviceStr", "---------      "+ECGDeviceAddress);
					}
			}else{
				mConnectECGButton.setText("�����������");
				Toast.makeText(getActivity(), "û�з����ĵ�ɼ��豸������ʧ�ܣ������µ�����ӣ�", Toast.LENGTH_SHORT).show();
			}
			connectECGDevice();
		}
	};
	
	private void connectECGDevice(){
		Log.e("", "---- ׼����ԣ�");
		btAdapt.cancelDiscovery();
		UUID uuid = UUID.fromString(SPP_UUID);
		if(ECGDeviceFlag){

			BluetoothDevice btDev = btAdapt.getRemoteDevice(ECGDeviceAddress);
			try {
				btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
				btSocket.connect();
				ECGApplication.getInstance().setBtSocketConnectFlag(true);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mPromptText.setText("��������ʧ�ܣ�");
				mConnectECGButton.setText("�����������");
				ECGApplication.getInstance().setBtSocketConnectFlag(false);
				Toast.makeText(getActivity(), "����ʧ�ܣ������µ�����ӣ�", Toast.LENGTH_SHORT).show();
			}
			
			if (ECGApplication.getInstance().getBtSocketConnectFlag()){
				mMeasureECGButton.setEnabled(true);
				mConnectECGButton.setText("�Ͽ�����");
				mPromptText.setText("�������ӳɹ���");
			} else {
				try {
					Log.e("", "���Թر�Socket");
					btSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			Toast.makeText(getActivity(), "û�з����ĵ�ɼ��豸������ʧ�ܣ������µ�����ӣ�", Toast.LENGTH_SHORT).show();
		}
	}
}
