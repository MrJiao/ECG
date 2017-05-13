package edu.tjlg.ecg_tester.database;

import java.util.Map;
import edu.tjlg.ecg_tester.R;
import edu.tjlg.ecg_tester.fragment.CollectionFragment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class DbOperate {
	private Context context;
	private Map<String,String> map;

	public DbOperate(Context context){
		this.context = context;
	}

	//创建数据库mylist.db	
	public void createListDb() {
		DbService service = new DbDataLead(context);
		ContentValues values = new ContentValues();// 类似map的属性
		boolean flag = service.createList(values);
		System.out.println("createList------------------->>" + flag);
	}

	//添加用户信息
	public void addUserInfo(String tablename,String name,String gender,String age,String phoneNum,String illness){
		DbService service = new DbDataLead(context);
		ContentValues values = new ContentValues();// 类似map的属性
		values.put("name", name);
		values.put("gender", gender);
		values.put("age", age);
		values.put("phoneNum", phoneNum);
		values.put("illness", illness);
		boolean flag = service.addList(values,tablename);
		getToastFlag(flag);
		System.out.println("addList"+tablename+"--->>" + flag);
	}

	//修改用户信息
	public void updateList(int id,String tablename,String name,String gender,String age,String phoneNum,String illness){
		DbService service = new DbDataLead(context);
		ContentValues values = new ContentValues();// 类似map的属性
		values.put("name", name);
		values.put("gender", gender);
		values.put("age", age);
		values.put("phoneNum", phoneNum);
		values.put("illness", illness);
		boolean flag = service.updateList(values, " id = ? ", new String[]{""+id},tablename);
		getToastFlag(flag);
		System.out.println("update"+tablename+"--->>" + flag);
	}

	//查询用户信息
	public Map<String,String> viewList(int id,String tablename){
		DbService service = new DbDataLead(context);
		map = service.viewList(" id = ? ", new String[]{""+id},tablename);
		return map;
	}

	private void getToastFlag(boolean flag){
		if(flag){
			showCollectDataDialog();
		}else{
			Toast.makeText(context, "填写或修改个人信息失败，请重新填写！", Toast.LENGTH_LONG).show();
		}
	}
	private void showCollectDataDialog(){
		Builder builder = new Builder(context);  
		//		builder.setIcon(R.drawable.toast_icon);
		builder.setTitle("ECG测量");  
		builder.setMessage("您的个人信息已填写或修改成功，请点击确定，开始测量心电数据！");  
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
			@SuppressLint("NewApi")
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				FragmentManager fragmentManager = ((Activity)context).getFragmentManager();
				FragmentTransaction transaction = fragmentManager.beginTransaction();
				transaction.replace(R.id.replace_fragmentLayout, new CollectionFragment());
				transaction.commit();
//				Intent intent = new Intent();
//				intent.setClass(context, CollectDataActivity.class);
//				context.startActivity(intent);
//				Activity activity = (Activity) context;
//				activity.finish();
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
}


