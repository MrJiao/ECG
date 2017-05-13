package edu.tjlg.ecg_tester.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import edu.tjlg.ecg_tester.CollectDataActivity;
import edu.tjlg.ecg_tester.domain.TesterInfo;
import edu.tjlg.ecg_tester.utils.UserInfoUtil;

import android.content.Context;
import android.util.Log;



public class CommonManage {
	private Context context;
//	private boolean getDataFlag = false;
//	private InputStream btInput = null;// 蓝牙数据输入流
//	private FileOutputStream fos = null;//
//	private File fi = null;//
//	private final int dataBytes = 50000;//一次蓝牙传输的字节总数
//
//	private Calendar mCalendar;
//	private String fileId;
//	private SimpleDateFormat simpleDateFormat;
	private String beginTime;
//	String filestr = "";

	//private File fidirectory = null;//

	public CommonManage(Context context){
		this.context = context;
	}

	/**
	public String GetBuleToothECGStearm(){
		creatECGFile();
		try {
			if(CollectDataActivity.mApplication.getBtSocketConnectFlag()){
				btInput = CollectDataActivity.btSocket.getInputStream();
				byte[] b = new byte[dataBytes];  
				int readBytes = 0;  

				while (readBytes < dataBytes) {  
					int read = btInput.read(b, readBytes, dataBytes - readBytes);  
					System.out.println(read);  
					if (read == -1) {  
						break;  
					}  
					readBytes += read; 
				}  
				//调用publishProgress公布进度,最后onProgressUpdate方法将被执行  
				publishProgress((int) ((count / (float) total) * 100));
				//关闭输入流接口和蓝牙连接接口
//				btInput.close();
				//CollectDataActivity.btSocket.close();

				fos=new FileOutputStream(fi.getAbsolutePath(),true);  
				//output file  
				fos.write(b,0,readBytes);
				//flush this stream  
				fos.flush();  
				//close this stream  
				fos.close(); 

				getDataFlag = true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{

		}

		return filestr;
	}
	*/

	public String creatECGFile(FileOutputStream fos, File fi){
		//创建文件
		File fidirectory = new File("sdcard/ECGBlueToothFile/");
		if(!fidirectory.exists()){
			fidirectory.mkdir();
		}
		Calendar mCalendar = Calendar.getInstance();
		//日期格式
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		//用时间命名文件名
		String fileId = ""+mCalendar.get(Calendar.YEAR)+(mCalendar.get(Calendar.MONTH)+1)+
				+mCalendar.get(Calendar.DAY_OF_MONTH)+ mCalendar.get(Calendar.HOUR_OF_DAY)+
				+mCalendar.get(Calendar.MINUTE)+mCalendar.get(Calendar.SECOND);
		beginTime = simpleDateFormat.format(mCalendar.getTime()).toString();


		TesterInfo testerInfo = UserInfoUtil.getTesterInfo(context);

		String filestr = "sdcard/ECGBlueToothFile/" +testerInfo.getName();
		filestr += "_";
		filestr += fileId ;
		filestr+= ".txt";
		fi = new File(filestr);
		if(!fi.exists()){
			System.out.println("creating it");
			try{
				fi.createNewFile();
			}
			catch(IOException e){
				e.printStackTrace();
				System.out.println(e.toString());
			}
		}

		//将用户信息数据以及测量时间封装成JSON数据格式写入文件
		String testInfoStr = getJsonStr();
		System.out.println("----  "+testInfoStr);
		testInfoStr += "##";
		try {
			byte[] testInfoBytes = testInfoStr.getBytes();
			fos=new FileOutputStream(fi.getAbsolutePath(),true);
			//output file  
			fos.write(testInfoBytes);
			//flush this stream  
			fos.flush();  
			//close this stream  
			fos.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return filestr;
	}

	private String getJsonStr(){
		TesterInfo testerInfo = UserInfoUtil.getTesterInfo(context);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Calendar mCalendar = Calendar.getInstance();
		String beginTime = simpleDateFormat.format(mCalendar.getTime()).toString();
		JSONObject testInfoJSONObject = new JSONObject();
		try {
			testInfoJSONObject.put("name",testerInfo.getName());
			testInfoJSONObject.put("gender",testerInfo.getGender());
			testInfoJSONObject.put("age", testerInfo.getAge());
			testInfoJSONObject.put("phoneNum",testerInfo.getPhoneNum());
			testInfoJSONObject.put("illness", testerInfo.getIllness());
			testInfoJSONObject.put("beginTime", beginTime);
			//testInfoJSONObject.put("rPeakStr", "");
			//testInfoJSONObject.put("preBeatList", "");
			return testInfoJSONObject.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}

