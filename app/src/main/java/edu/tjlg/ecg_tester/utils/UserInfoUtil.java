package edu.tjlg.ecg_tester.utils;

import android.content.Context;

import java.util.Map;

import edu.tjlg.ecg_tester.database.DbOperate;
import edu.tjlg.ecg_tester.domain.TesterInfo;

/**
 * Created by jackson on 2017/4/16.
 */

public class UserInfoUtil {

    public static TesterInfo getTesterInfo(Context context){
        Map<String,String> mapUser = new DbOperate(context).viewList(1, "userlist");
        TesterInfo testerInfo = new TesterInfo();
        testerInfo.setName(mapUser.get("name"));
        testerInfo.setGender(mapUser.get("gender"));
        testerInfo.setPhoneNum(mapUser.get("phoneNum"));
        testerInfo.setIllness(mapUser.get("illness"));
        testerInfo.setAge(mapUser.get("age"));
        return testerInfo;
    }
}
