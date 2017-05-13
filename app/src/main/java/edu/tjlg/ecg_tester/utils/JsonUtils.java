package edu.tjlg.ecg_tester.utils;

import org.json.JSONException;
import org.json.JSONObject;

import edu.tjlg.ecg_tester.domain.TesterInfo;


public class JsonUtils {

	//	TesterInfo
	public static TesterInfo getTesterInfo(String jsonString) {

		try {
			JSONObject testerJsonObject = new JSONObject(jsonString);
			if (testerJsonObject != null) {
				TesterInfo tester = new TesterInfo();
				tester.setName(testerJsonObject.getString("name"));
				tester.setGender(testerJsonObject.getString("gender"));
				tester.setAge(testerJsonObject.getString("age"));
				tester.setBeginTime(testerJsonObject.getString("beginTime"));
				tester.setPhoneNum(testerJsonObject.getString("phoneNum"));
				tester.setIllness(testerJsonObject.getString("illness"));
				//tester.setrPeakStr(testerJsonObject.getString("rPeak"));
				//tester.setPreBeatStr(testerJsonObject.getString("preBeat"));
				
				return tester;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
