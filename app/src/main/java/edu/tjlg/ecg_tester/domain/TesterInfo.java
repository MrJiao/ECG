package edu.tjlg.ecg_tester.domain;

public class TesterInfo {

	private String name;
	private String sex;
	private String age;
	private String gender;
	private String phoneNum;
	private String condition;
	private String beginTime;
	private String illness;
	private String rPeakStr;
	private String preBeatStr;

	public String getrPeakStr() {
		return rPeakStr;
	}
	public String getPreBeatStr() {
		return preBeatStr;
	}
	public void setrPeakStr(String rPeaksStr) {
		this.rPeakStr = rPeaksStr;
	}
	public void setPreBeatStr(String preBeatStr) {
		this.preBeatStr = preBeatStr;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
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
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getIllness() {
		return illness;
	}
	public void setIllness(String illness) {
		this.illness = illness;
	}
}
