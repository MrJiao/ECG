package edu.tjlg.ecg_tester.adapter;

import java.io.File;
import java.util.ArrayList;

import edu.tjlg.ecg_tester.R;
import edu.tjlg.ecg_tester.ViewWaveActivity;
import edu.tjlg.ecg_tester.domain.ECGFileInfo;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RecordListAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	private ArrayList<ECGFileInfo> list;
	private Context context;

	public final class ListItemView{               
		public TextView itemTime;   
		public TextView itemPhoneNum;
		public TextView itemUserName; 
		public TextView itemFileName;
		public RelativeLayout deletelistItem;
		public RelativeLayout displaylistItem;
		public RelativeLayout sendlistItem;
	}    

	public  RecordListAdapter(Context context,ArrayList<ECGFileInfo> listItem){
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.list = listItem;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ListItemView listItemView = null;
		if (convertView == null) { 
			listItemView = new ListItemView();
			convertView = mInflater.inflate(R.layout.record_list_item, null); 

			listItemView.itemTime = (TextView)convertView.findViewById(R.id.record_date_time_tv);
			listItemView.itemPhoneNum = (TextView)convertView.findViewById(R.id.record_phoneNum_tv);
			//			listItemView.itemUserName = (TextView)convertView.findViewById(R.id.record_user_name_tv);
			listItemView.sendlistItem = (RelativeLayout)convertView.findViewById(R.id.record_send_rl);
			listItemView.itemFileName = (TextView)convertView.findViewById(R.id.record_file_name_tv);
			listItemView.deletelistItem = (RelativeLayout)convertView.findViewById(R.id.record_delete_rl);
			listItemView.displaylistItem = (RelativeLayout)convertView.findViewById(R.id.record_display_rl);
			convertView.setTag(listItemView);

		}else {   
			listItemView = (ListItemView)convertView.getTag();   
		} 

		listItemView.itemTime.setText(list.get(position).getCreateFileTime());
		//		listItemView.itemUserName.setText((String)list.get(position).getUserName());

		String fileAllName = list.get(position).getFileName();
		int i = fileAllName.indexOf("_");
		Log.i("i--", "---     "+i);
		if(i > 0){
			listItemView.itemPhoneNum.setText("姓名：" + fileAllName.substring(0, i));
			listItemView.itemFileName.setText("文件：" + fileAllName.substring(i+1));
		}else{
			listItemView.itemPhoneNum.setText("姓名： 暂时无");
			listItemView.itemFileName.setText("文件：" + fileAllName);
		}

		listItemView.deletelistItem.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//删除确定对话框
				showDeleteDialog(position);
			}
		});

		listItemView.displaylistItem.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDetailInfo(list.get(position).getFilePath());
			}
		});
		
		listItemView.sendlistItem.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				intent.setType("text/*");
				Uri uri = Uri.fromFile(new File(list.get(position).getFilePath()));
				//intent.setData(uri);
				intent.putExtra(Intent.EXTRA_STREAM, uri);
				Intent.createChooser(intent, "发送心电数据");
				context.startActivity(intent);
			}
			
		});
		listItemView.displaylistItem.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDetailInfo(list.get(position).getFilePath());
			}
		});
		return convertView;
	}

	//跳转到波形查看活动类
	public void showDetailInfo(String filePath){
		Intent intent = new Intent();
		intent.setClass(context, ViewWaveActivity.class);
		intent.putExtra("filestr", filePath);
		context.startActivity(intent);
	}

	private void showDeleteDialog(final int position){
		Builder builder = new Builder(context);  
		//		builder.setIcon(R.drawable.toast_icon);
		builder.setTitle("心电监测系统");  
		builder.setMessage("确定删除此条数据？");  
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int which) {
				File file = new File(list.get(position).getFilePath());
				deleteFile(file);
				list.remove(position);
				notifyDataSetChanged();
			}  
		});  
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int which) {  
			}  
		});  
		builder.create();  
		builder.show(); 
	}

	//删除SD卡中的文件
	public void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			}else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			//			file.delete();
		} else {
			Toast.makeText(context, "文件已被删除或移除！", Toast.LENGTH_LONG).show();
		}
	}

}
