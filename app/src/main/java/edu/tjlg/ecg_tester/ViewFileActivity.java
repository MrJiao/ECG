package edu.tjlg.ecg_tester;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import edu.tjlg.ecg_tester.adapter.RecordListAdapter;
import edu.tjlg.ecg_tester.common.SortComparator;
import edu.tjlg.ecg_tester.domain.ECGFileInfo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

public class ViewFileActivity extends Activity{

	private ListView ecgrecordListView ;
	private String ECGFielPath  ="sdcard/ECGBlueToothFile/";
	private ArrayList<ECGFileInfo> recordList = new ArrayList<ECGFileInfo>();;
	private SimpleDateFormat simpleDateFormat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_ecgfile);

		ecgrecordListView = (ListView)findViewById(R.id.ecg_files);
		//日期格式
		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		getECGFileName(ECGFielPath);
		
		if(recordList.size()!=0 && recordList!=null){
			//按日期进行排序
			Collections.sort(recordList, new SortComparator());  
			ecgrecordListView.setAdapter(new RecordListAdapter(this, recordList));
		}else{
			Toast.makeText(this, "没有心电数据！", Toast.LENGTH_LONG).show();
		}
	}

	private void getECGFileName(String path){   
		// get file list where the path has   
		File file = new File(path);   
		// get the folder list   
		File[] array = file.listFiles();   
		Date date ;
		for(int i=0;i<array.length;i++){   
			if(array[i].isFile()){   
				//将lastModified()获取long型的时间转化为date
				date = new Date(array[i].lastModified());

				ECGFileInfo mECGFileInfo = new ECGFileInfo();
				mECGFileInfo.setUserName(" ");
				mECGFileInfo.setFileName(array[i].getName());
				mECGFileInfo.setFilePath(array[i].getAbsolutePath());
				mECGFileInfo.setCreateFileTime(simpleDateFormat.format(date));
				mECGFileInfo.setLastModifiedTime(array[i].lastModified());
				
//				HashMap<String, Object> map = new HashMap<String, Object>();  
//				map.put("userName", " ");  
//				map.put("fileName", array[i].getName());  
//				map.put("filePath", array[i].getAbsolutePath()); 
//				map.put("createTime", simpleDateFormat.format(date)); 
//				map.put("lastModifiedTime", array[i].lastModified());
//				recordList.add(map);
				recordList.add(mECGFileInfo);
			}else if(array[i].isDirectory()){   
				getECGFileName(array[i].getPath());   
			}   
		} 
	}   

}
