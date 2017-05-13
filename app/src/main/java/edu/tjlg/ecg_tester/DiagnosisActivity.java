package edu.tjlg.ecg_tester;



import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;

public class DiagnosisActivity extends Activity{

	private EditText phoneNumEd;
	private Spinner illnessSpinner;
	private Spinner conditionSpinner;
	private EditText otherEd;
	private RelativeLayout callPohoneBtn; 
	private RelativeLayout sendMessageBtn;
	private TextView beginTimeTv;

	private String phoneNumStr;
	private final String[] illnessStrParams = {"Normal","Sinus Tachycardia","Sinus Bradycardia","Sinus Arrhythmia","Sinus Arrest","Atrial Premature Beats","Premature Ventricular Contraction"};
	private ArrayAdapter<String> illnessAdapter;
	private String illnessStr;
	private String beginTimeStr;

	private final String[] conditionStrParams = {"No Need Diagnosis","Diagnosis Tomorrow","Diagnosis Soon","Health"};
	private ArrayAdapter<String> conditionAdapter;
	private String conditionStr;

	private String messgaeStr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diagnosis_layout);

		phoneNumEd = (EditText)findViewById(R.id.diagnosis_phoneNum_ed);
		otherEd = (EditText)findViewById(R.id.diagnosis_other_ed);
		illnessSpinner = (Spinner)findViewById(R.id.diagnosis_illness_spinner);
		conditionSpinner = (Spinner)findViewById(R.id.diagnosis_condition_spinner);
		callPohoneBtn = (RelativeLayout)findViewById(R.id.diagnosis_phone_rl);
		sendMessageBtn = (RelativeLayout)findViewById(R.id.diagnosis_send_rl);
		beginTimeTv = (TextView)findViewById(R.id.diagnosis_beginTime_tv);

		beginTimeStr = getIntent().getStringExtra("beginTime");
		beginTimeTv.setText(beginTimeStr);
		phoneNumStr = getIntent().getStringExtra("phoneNum");
		phoneNumEd.setText(phoneNumStr);

		//将可选内容与ArrayAdapter连接起来
		illnessAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,illnessStrParams);
		//设置下拉列表的风格
		illnessAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//将adapter 添加到spinner中
		illnessSpinner.setAdapter(illnessAdapter);
		//添加事件Spinner事件监听  
		illnessSpinner.setOnItemSelectedListener(new IllnessSpinnerSelectedListener());
		//设置默认值
		illnessSpinner.setVisibility(View.VISIBLE);

		//将可选内容与ArrayAdapter连接起来
		conditionAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,conditionStrParams);
		//设置下拉列表的风格
		conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//将adapter 添加到spinner中
		conditionSpinner.setAdapter(conditionAdapter);
		//添加事件Spinner事件监听  
		conditionSpinner.setOnItemSelectedListener(new ConditionSpinnerSelectedListener());
		//设置默认值
		conditionSpinner.setVisibility(View.VISIBLE);

		callPohoneBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				callPhone(phoneNumEd.getText().toString());
			}
		});


		sendMessageBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.i("", ""+illnessStr+"  ,   "+conditionStr);
				messgaeStr ="采集时间："+beginTimeTv.getText().toString();
				messgaeStr +="\n"+"病情："+illnessStr;
				messgaeStr +="\n"+"就医情况："+conditionStr;
				messgaeStr +="\n"+otherEd.getText().toString();
				sendMessage(messgaeStr);
			}
		});
	}
	//使用数组形式操作
	class IllnessSpinnerSelectedListener implements OnItemSelectedListener{

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			illnessStr = illnessStrParams[arg2];
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	//使用数组形式操作
	class ConditionSpinnerSelectedListener implements OnItemSelectedListener{

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			conditionStr = conditionStrParams[arg2];
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	public void callPhone(String phoneNum){
		Intent dialIntent = new Intent();
		dialIntent.setAction(Intent.ACTION_DIAL);
		dialIntent.setData(Uri.parse("tel:"+phoneNum));
		startActivity(dialIntent);
	}

	public void sendMessage(String content){

		String SENT_SMS_ACTION = "SENT_SMS_ACTION";  
		Intent sentIntent = new Intent(SENT_SMS_ACTION);  
		PendingIntent sentPI = PendingIntent.getBroadcast(DiagnosisActivity.this, 0, sentIntent,  
				0);  
		// register the Broadcast Receivers  
		DiagnosisActivity.this.registerReceiver(new BroadcastReceiver() {  
			@Override  
			public void onReceive(Context _context, Intent _intent) {  
				switch (getResultCode()) {  
				case Activity.RESULT_OK:  
					Toast.makeText(DiagnosisActivity.this,  
							"短信发送成功", Toast.LENGTH_SHORT)  
							.show();  
					break;  
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:  
					break;  
				case SmsManager.RESULT_ERROR_RADIO_OFF:  
					break;  
				case SmsManager.RESULT_ERROR_NULL_PDU:  
					break;  
				}  
			}  
		}, new IntentFilter(SENT_SMS_ACTION));  
		String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";  
		// create the deilverIntent parameter  
		Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);  
		PendingIntent deliverPI = PendingIntent.getBroadcast(DiagnosisActivity.this, 0,  
				deliverIntent, 0);  
		DiagnosisActivity.this.registerReceiver(new BroadcastReceiver() {  
			@Override  
			public void onReceive(Context _context, Intent _intent) {  
				Toast.makeText(DiagnosisActivity.this,  
						"收信人已经成功接收", Toast.LENGTH_SHORT)  
						.show();  
			}  
		}, new IntentFilter(DELIVERED_SMS_ACTION));

		//直接调用短信接口发短信  
		SmsManager smsManager = SmsManager.getDefault();  
		List<String> divideContents = smsManager.divideMessage(content);    
		for (String text : divideContents) {    
			smsManager.sendTextMessage(phoneNumEd.getText().toString(), null, text, sentPI, deliverPI);    
		}  
	}
}
