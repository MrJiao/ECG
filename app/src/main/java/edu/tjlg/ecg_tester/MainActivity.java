package edu.tjlg.ecg_tester;

import java.io.IOException;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import edu.tjlg.ecg_tester.application.ECGApplication;
import edu.tjlg.ecg_tester.database.DbOperate;
import edu.tjlg.ecg_tester.fragment.AboutFragment;
import edu.tjlg.ecg_tester.fragment.CollectionFragment;
import edu.tjlg.ecg_tester.fragment.HistoryFragment;
import edu.tjlg.ecg_tester.fragment.UserInfoFragment;
import edu.tjlg.ecg_tester.utils.L;

public class MainActivity extends Activity {

	private RadioGroup mRadioGroup;
	private RadioButton mCollectECG;//心电采集
	private RadioButton mHistoryECG;//历史记录
	private RadioButton mUserInfo;//个人信息
	private RadioButton mAboutECG;//关于软件
	
	private FragmentManager mFragmentManager;
	private FragmentTransaction mTransaction;

	private DbOperate mDbOperate;
	private String userInfoList = "userlist";
	private Map<String, String> mapUser;
	private ECGApplication mApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bindView();
		setListener();
		initView();
		
		mApplication = ECGApplication.getInstance();
		mDbOperate = new DbOperate(this);
		mapUser = mDbOperate.viewList(1, userInfoList);

		if (mDbOperate.viewList(1, userInfoList).size() < 1){
			mApplication.setLoginFlag(false);
			showWriteUserInfoDialog();
		} else {
			mApplication.setLoginFlag(true);
			mApplication.setName(mapUser.get("name"));
			mApplication.setGender(mapUser.get("gender"));
			mApplication.setAge(mapUser.get("age"));
			mApplication.setPhoneNum(mapUser.get("phoneNum"));
			mApplication.setillness(mapUser.get("illness"));
		}
	}
	
	private void showWriteUserInfoDialog(){
		Builder builder = new Builder(MainActivity.this);  
		//		builder.setIcon(R.drawable.toast_icon);
		builder.setTitle("心电监测系统");  
		builder.setMessage("您的个人信息不完整，请点击确定，填写");  
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
			@SuppressLint("NewApi")
			public void onClick(DialogInterface dialog, int which) {
				mUserInfo.setChecked(true);
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction transaction = fragmentManager.beginTransaction();
				transaction.replace(R.id.replace_fragmentLayout, new UserInfoFragment());
				transaction.commit();
//				Intent intent = new Intent();
//				intent.setClass(MainActivity.this, UserInfoActivity.class);
//				intent.putExtra("userflag", "0");
//				startActivity(intent);
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

	//键盘监听
	public boolean onKeyDown(int keyCode, KeyEvent e){
		if(keyCode!=4){
			//如果不是按下的返回按钮时不做任何处理，直接返回
			return false;
		}

		Builder builder = new Builder(MainActivity.this);  
		//				builder.setIcon(R.drawable.toast_icon);
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
		return false;
	}
	
	private void bindView() {
		mRadioGroup = (RadioGroup) findViewById(R.id.activity_main_radioGroup);
		mCollectECG = (RadioButton) findViewById(R.id.ecg_collect);
		mHistoryECG = (RadioButton) findViewById(R.id.ecg_history);
		mUserInfo = (RadioButton) findViewById(R.id.user_info);
		mAboutECG = (RadioButton) findViewById(R.id.about);
	}
	
	private void setListener(){
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				mFragmentManager = getFragmentManager();
				mTransaction = mFragmentManager.beginTransaction();
				
				switch (checkedId) {
				case R.id.ecg_collect:
					mTransaction.replace(R.id.replace_fragmentLayout, new CollectionFragment());
					break;
				case R.id.ecg_history:
					mTransaction.replace(R.id.replace_fragmentLayout, new HistoryFragment());
					break;
				case R.id.user_info:
					mTransaction.replace(R.id.replace_fragmentLayout, new UserInfoFragment());
					break;
				case R.id.about:
					mTransaction.replace(R.id.replace_fragmentLayout, new AboutFragment());
					break;
				}
				mTransaction.commit();
			}
		});
	}
	
	@SuppressLint("NewApi")
	private void initView(){
		mFragmentManager = getFragmentManager();
		mTransaction = mFragmentManager.beginTransaction();
		mTransaction.replace(R.id.replace_fragmentLayout, new CollectionFragment());
		mTransaction.commit();
	}
}
