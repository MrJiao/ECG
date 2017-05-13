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

		//����ѡ������ArrayAdapter��������
		illnessAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,illnessStrParams);
		//���������б�ķ��
		illnessAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//��adapter ��ӵ�spinner��
		illnessSpinner.setAdapter(illnessAdapter);
		//����¼�Spinner�¼�����  
		illnessSpinner.setOnItemSelectedListener(new IllnessSpinnerSelectedListener());
		//����Ĭ��ֵ
		illnessSpinner.setVisibility(View.VISIBLE);

		//����ѡ������ArrayAdapter��������
		conditionAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,conditionStrParams);
		//���������б�ķ��
		conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//��adapter ��ӵ�spinner��
		conditionSpinner.setAdapter(conditionAdapter);
		//����¼�Spinner�¼�����  
		conditionSpinner.setOnItemSelectedListener(new ConditionSpinnerSelectedListener());
		//����Ĭ��ֵ
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
				messgaeStr ="�ɼ�ʱ�䣺"+beginTimeTv.getText().toString();
				messgaeStr +="\n"+"���飺"+illnessStr;
				messgaeStr +="\n"+"��ҽ�����"+conditionStr;
				messgaeStr +="\n"+otherEd.getText().toString();
				sendMessage(messgaeStr);
			}
		});
	}
	//ʹ��������ʽ����
	class IllnessSpinnerSelectedListener implements OnItemSelectedListener{

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			illnessStr = illnessStrParams[arg2];
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	//ʹ��������ʽ����
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
							"���ŷ��ͳɹ�", Toast.LENGTH_SHORT)  
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
						"�������Ѿ��ɹ�����", Toast.LENGTH_SHORT)  
						.show();  
			}  
		}, new IntentFilter(DELIVERED_SMS_ACTION));

		//ֱ�ӵ��ö��Žӿڷ�����  
		SmsManager smsManager = SmsManager.getDefault();  
		List<String> divideContents = smsManager.divideMessage(content);    
		for (String text : divideContents) {    
			smsManager.sendTextMessage(phoneNumEd.getText().toString(), null, text, sentPI, deliverPI);    
		}  
	}
}
