package edu.tjlg.ecg_tester;

import java.util.Map;

import edu.tjlg.ecg_tester.application.ECGApplication;
import edu.tjlg.ecg_tester.database.DbOperate;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class UserInfoActivity extends Activity{

	private EditText nameEd;
	private Spinner genderSpinner;
	private EditText ageEd;
	private EditText phoneNumEd;
	private EditText illnessEd;
	private RelativeLayout updateBtn; 
	private TextView btnTv;

	private final String[] genderStrParams = {"男","女"};
	private ArrayAdapter<String> genderAdapter;
	private ECGApplication mApplication;
	private DbOperate mDbOperate;
	private String userInfoList = "userlist";
	private Map<String, String> mapUser;
	private int userFlag;
	private String genderStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub 123123123123
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfo_layout);

		nameEd = (EditText)findViewById(R.id.name_ed);
		genderSpinner = (Spinner)findViewById(R.id.gender_spinner);
		ageEd = (EditText)findViewById(R.id.age_ed);
		phoneNumEd = (EditText)findViewById(R.id.phoneNum_ed);
		illnessEd = (EditText)findViewById(R.id.illness_ed);
		updateBtn = (RelativeLayout)findViewById(R.id.update_info_btn);
		btnTv = (TextView)findViewById(R.id.update_info_tv);

		//将可选内容与ArrayAdapter连接起来
		genderAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,genderStrParams);
		//设置下拉列表的风格
		genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//将adapter 添加到spinner中
		genderSpinner.setAdapter(genderAdapter);
		//添加事件Spinner事件监听  
		genderSpinner.setOnItemSelectedListener(new SpinnerSelectedListener());
		//设置默认值
		genderSpinner.setVisibility(View.VISIBLE);

		mApplication = (ECGApplication)this.getApplication();
		userFlag = Integer.valueOf(getIntent().getStringExtra("userflag"));
		mDbOperate = new DbOperate(this);
		mapUser = mDbOperate.viewList(1, userInfoList);
		Log.i("", ""+genderStr);

		if(userFlag == 0){
			btnTv.setText("确定");
		}else{
			btnTv.setText("修改");
			nameEd.setText(mapUser.get("name"));
			if(mapUser.get("gender").equals("男")){
				genderSpinner.setSelection(0);
			}else{
				genderSpinner.setSelection(1);
			}
			ageEd.setText(mapUser.get("age"));
			phoneNumEd.setText(mapUser.get("phoneNum"));
			illnessEd.setText(mapUser.get("illness"));
		}
		updateBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if(nameEd.getText().length()==0||ageEd.getText().length()==0||phoneNumEd.getText().length()==0
						||illnessEd.getText().length()==0){
					Builder builder = new Builder(UserInfoActivity.this);  
					//		builder.setIcon(R.drawable.toast_icon);
					builder.setTitle("心电监测系统");  
					builder.setMessage("您的个人信息填写不完整，请填写完整！");  
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
						public void onClick(DialogInterface dialog, int which) {

						}  
					});  
					builder.create();  
					builder.show(); 
				}else{
					if(userFlag == 0){
						mDbOperate.addUserInfo(userInfoList, nameEd.getText().toString(), genderStr,
								ageEd.getText().toString(), phoneNumEd.getText().toString(),illnessEd.getText().toString());
					}else{
						mDbOperate.updateList(1, userInfoList, nameEd.getText().toString(), genderStr,
								ageEd.getText().toString(), phoneNumEd.getText().toString(),illnessEd.getText().toString());
					}

					mApplication.setName(nameEd.getText().toString());
					mApplication.setGender(genderStr);
					mApplication.setAge(ageEd.getText().toString());
					mApplication.setPhoneNum(phoneNumEd.getText().toString());
					mApplication.setillness(illnessEd.getText().toString());
				}
			}
		});

	}
	//使用数组形式操作
	class SpinnerSelectedListener implements OnItemSelectedListener{

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			genderStr = genderStrParams[arg2];
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}


}
