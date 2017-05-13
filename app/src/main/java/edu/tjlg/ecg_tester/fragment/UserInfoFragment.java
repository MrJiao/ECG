package edu.tjlg.ecg_tester.fragment;

import java.util.Map;

import edu.tjlg.ecg_tester.MainActivity;
import edu.tjlg.ecg_tester.R;
import edu.tjlg.ecg_tester.application.ECGApplication;
import edu.tjlg.ecg_tester.database.DbOperate;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

@SuppressLint("NewApi")
public class UserInfoFragment extends Fragment{

	private TextView mTitleText;//标题
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
	private String genderStr;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.userinfo_layout, null);
		
		bindView(view);
		setListener();
		initView();
		
		return view;
	}

	private void bindView(View view) {
		mTitleText = (TextView) view.findViewById(R.id.title_textView);
		nameEd = (EditText) view.findViewById(R.id.name_ed);
		genderSpinner = (Spinner) view.findViewById(R.id.gender_spinner);
		ageEd = (EditText) view.findViewById(R.id.age_ed);
		phoneNumEd = (EditText) view.findViewById(R.id.phoneNum_ed);
		illnessEd = (EditText) view.findViewById(R.id.illness_ed);
		updateBtn = (RelativeLayout) view.findViewById(R.id.update_info_btn);
		btnTv = (TextView) view.findViewById(R.id.update_info_tv);
	}
	
	private void setListener() {
		// TODO Auto-generated method stub
		updateBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (nameEd.getText().length() == 0 || ageEd.getText().length() == 0 || phoneNumEd.getText().length() == 0 || illnessEd.getText().length() == 0){
					Builder builder = new Builder(getActivity());  
					// builder.setIcon(R.drawable.toast_icon);
					builder.setTitle("心电监测系统");  
					builder.setMessage("您的个人信息填写不完整，请填写完整！");  
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}  
					});  
					builder.create();  
					builder.show();
				} else {
					if (mApplication.getLoginFlag() == false){
						mDbOperate.addUserInfo(userInfoList, nameEd.getText().toString(), genderStr, ageEd.getText().toString(), phoneNumEd.getText().toString(), illnessEd.getText().toString());
					} else {
						mDbOperate.updateList(1, userInfoList, nameEd.getText().toString(), genderStr, ageEd.getText().toString(), phoneNumEd.getText().toString(), illnessEd.getText().toString());
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
	
	private void initView() {
		mTitleText.setText("个人信息");
		
		//将可选内容与ArrayAdapter连接起来
		genderAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, genderStrParams);
		//设置下拉列表的风格
		genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//将adapter 添加到spinner中
		genderSpinner.setAdapter(genderAdapter);
		//添加事件Spinner事件监听  
		genderSpinner.setOnItemSelectedListener(new SpinnerSelectedListener());
		//设置默认值
		genderSpinner.setVisibility(View.VISIBLE);
		
		mApplication = (ECGApplication)getActivity().getApplication();
		mDbOperate = new DbOperate(getActivity());
		mapUser = mDbOperate.viewList(1, userInfoList);
		
		if (mDbOperate.viewList(1, userInfoList).size() < 1){
			btnTv.setText("确定");
			mApplication.setLoginFlag(false);
		} else {
			btnTv.setText("修改");
			mApplication.setLoginFlag(true);
			mApplication.setName(mapUser.get("name"));
			mApplication.setGender(mapUser.get("gender"));
			mApplication.setAge(mapUser.get("age"));
			mApplication.setPhoneNum(mapUser.get("phoneNum"));
			mApplication.setillness(mapUser.get("illness"));
			
			nameEd.setText(mapUser.get("name"));
			if(mapUser.get("gender").equals("男")){
				genderSpinner.setSelection(0);
			} else {
				genderSpinner.setSelection(1);
			}
			ageEd.setText(mapUser.get("age"));
			phoneNumEd.setText(mapUser.get("phoneNum"));
			illnessEd.setText(mapUser.get("illness"));
		}
	}
	
	//使用数组形式操作
	class SpinnerSelectedListener implements OnItemSelectedListener{

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			genderStr = genderStrParams[arg2];
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			
		}
	}
}
