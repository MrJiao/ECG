package edu.tjlg.ecg_tester.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import edu.tjlg.ecg_tester.R;
import edu.tjlg.ecg_tester.adapter.RecordListAdapter;
import edu.tjlg.ecg_tester.common.SortComparator;
import edu.tjlg.ecg_tester.domain.ECGFileInfo;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class HistoryFragment extends Fragment{

	private TextView mTitleText;//标题
	private ListView mEcgrecordListView ;

	private String ECGFielPath  ="sdcard/ECGBlueToothFile/";
	private ArrayList<ECGFileInfo> recordList = new ArrayList<ECGFileInfo>();;
	private SimpleDateFormat simpleDateFormat;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.view_ecgfile, null);
		
		bindView(view);
		initView();
		
		return view;
	}
	
	private void bindView(View view) {
		mTitleText = (TextView) view.findViewById(R.id.title_textView);
		mEcgrecordListView = (ListView) view.findViewById(R.id.ecg_files);
	}
	
	private void initView() {
		mTitleText.setText("历史记录");
		
		//日期格式
		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		getECGFileName(ECGFielPath);
		
		if (recordList.size()!=0 && recordList!=null){
			//按日期进行排序
			Collections.sort(recordList, new SortComparator());  
			mEcgrecordListView.setAdapter(new RecordListAdapter(getActivity(), recordList));
		} else {
			Toast.makeText(getActivity(), "没有心电数据！", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void getECGFileName(String path) {   
		File file = new File(path);   
		File[] array = file.listFiles();   
		Date date ;
		for (int i = 0; i < array.length; i++) {   
			if (array[i].isFile()) {   
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
			} else if (array[i].isDirectory()){   
				getECGFileName(array[i].getPath());   
			}   
		}
	}   
}
