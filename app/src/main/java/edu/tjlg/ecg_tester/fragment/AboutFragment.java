package edu.tjlg.ecg_tester.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import edu.tjlg.ecg_tester.BlueToothActivity;
import edu.tjlg.ecg_tester.CollectDataActivity;
import edu.tjlg.ecg_tester.R;
import edu.tjlg.ecg_tester.application.ECGApplication;

@SuppressLint("NewApi")
public class AboutFragment extends Fragment{

	private TextView mTitleText;//标题
	private ListView lvBTDevices;
	private Button btnSearch;
	private Button btnDis;
	private Button btnExit;
	private ToggleButton tbtnSwitch;

	static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	private ArrayAdapter<String> adtDevices;
	private List<String> lstDevices = new ArrayList<String>();
	private BluetoothAdapter btAdapt;
	public static BluetoothSocket btSocket;
	private String ECGDeviceName = "HMSoft";
	private String ECGDeviceAddress;
	private boolean ECGDeviceFlag = false;
	private ECGApplication mApplication;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.bluetooth_layout, null);
		
		mApplication = ECGApplication.getInstance();
		
		bindView(view);
		setListener();
		initView();
		
		return view;
	}
	
	private void bindView(View view) {
		mTitleText = (TextView) view.findViewById(R.id.title_textView);
		btnSearch = (Button) view.findViewById(R.id.btnSearch);
		btnExit = (Button) view.findViewById(R.id.btnExit);
		btnDis = (Button) view.findViewById(R.id.btnDis);
		tbtnSwitch = (ToggleButton) view.findViewById(R.id.tbtnSwitch);
		lvBTDevices = (ListView) view.findViewById(R.id.lvDevices);
	}
	
	private void initView() {
		mTitleText.setText("关于软件");
		
		adtDevices = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, lstDevices);
		lvBTDevices.setAdapter(adtDevices);
		
		btAdapt = BluetoothAdapter.getDefaultAdapter();// 初始化本机蓝牙功能
		
		if (btAdapt.getState() == BluetoothAdapter.STATE_OFF) {
			tbtnSwitch.setChecked(false);
		} else if (btAdapt.getState() == BluetoothAdapter.STATE_ON) {
			tbtnSwitch.setChecked(true);
		}
		
		// 注册Receiver来获取蓝牙设备相关的结果
		IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果
		intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		getActivity().registerReceiver(searchDevices, intent);
	}
	
	private void setListener(){
		btnSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (btAdapt.getState() == BluetoothAdapter.STATE_OFF) {// 如果蓝牙还没开启
					Toast.makeText(getActivity(), "请先打开蓝牙", Toast.LENGTH_SHORT).show();
					return;
				}
				
				getActivity().setTitle("本机蓝牙地址：" + btAdapt.getAddress());
				lstDevices.clear();
				btAdapt.startDiscovery();
			}
		});
		
		btnExit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					if (btSocket != null)
						btSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				Builder builder = new Builder(getActivity());  
				//	builder.setIcon(R.drawable.toast_icon);
				builder.setTitle("心电监测系统");  
				builder.setMessage("确定退出心电监测系统？");  
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
					public void onClick(DialogInterface dialog, int which) { 
						if(mApplication.getBtSocketConnectFlag())
							try {
								CollectDataActivity.btSocket.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						System.exit(0);
						return ;
					}  
				});  
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
					public void onClick(DialogInterface dialog, int which) {  
						dialog.dismiss();
					}  
				});  
				builder.create();  
				builder.show(); 
			}
		});
		
		btnDis.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
				startActivity(discoverableIntent);
			}
		});
		
		tbtnSwitch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (tbtnSwitch.isChecked() == true) {
					btAdapt.enable();
				} else if (tbtnSwitch.isChecked() == false) {
					btAdapt.disable();
				}
			}
		});
		
		lvBTDevices.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Log.e("", "---- 准备配对！");
				btAdapt.cancelDiscovery();
				String str = lstDevices.get(position);
				String[] values = str.split("\\|");
				String address=values[1];
				UUID uuid = UUID.fromString(SPP_UUID);
				if (ECGDeviceFlag){
					address = ECGDeviceAddress;
				}
				
				BluetoothDevice btDev = btAdapt.getRemoteDevice(address);
				try {
					btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
					btSocket.connect();
					//打开波形图实例
					Intent intent = new Intent(getActivity(), CollectDataActivity.class);
					startActivity(intent);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getActivity(), "连接失败，请重新点击连接！", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		getActivity().unregisterReceiver(searchDevices);
		super.onDestroy();
	}
	
	private BroadcastReceiver searchDevices = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Bundle b = intent.getExtras();
			Object[] lstName = b.keySet().toArray();

			// 显示所有收到的消息及其细节
			for (int i = 0; i < lstName.length; i++) {
				String keyName = lstName[i].toString();
			}
			//搜索设备时，取得设备的MAC地址
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				Log.e("", "---- 广播响应！");
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String str= device.getName() + "|" + device.getAddress();
				if (device.getName().equals(ECGDeviceName)){
					ECGDeviceAddress = device.getAddress();
					ECGDeviceFlag = true;
					Log.i("ECGBlueToothDeviceStr", "---------"+ECGDeviceAddress);
				}
				
				if (lstDevices.indexOf(str) == -1){
					lstDevices.add(str); // 获取设备名称和mac地址
				}
				adtDevices.notifyDataSetChanged();
			}
		}
	};
}
