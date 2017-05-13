package edu.tjlg.ecg_tester.application;

import android.app.Application;

public class ECGApplication extends Application {
	private String name;
	private String gender;
	private String age;
	private String phoneNum;
	private String illness;
	private boolean loginFlag;
	private boolean btSocketConnectFlag;
	public static final int Smaplerate = 250;

	private static ECGApplication application;
	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
	}

	public static ECGApplication getInstance(){
		return application;
	}

	public boolean getBtSocketConnectFlag() {
		return btSocketConnectFlag;
	}
	public void setBtSocketConnectFlag(boolean btSocketConnectFlag) {
		this.btSocketConnectFlag = btSocketConnectFlag;
	}
	
	public boolean getLoginFlag() {
		return loginFlag;
	}
	public void setLoginFlag(boolean loginFlag) {
		this.loginFlag = loginFlag;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getIllness() {
		return illness;
	}
	public void setillness(String illness) {
		this.illness = illness;
	}

}
