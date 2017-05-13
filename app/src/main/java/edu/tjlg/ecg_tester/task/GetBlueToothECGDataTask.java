package edu.tjlg.ecg_tester.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import edu.tjlg.ecg_tester.CollectDataActivity;
import edu.tjlg.ecg_tester.R;
import edu.tjlg.ecg_tester.ViewWaveActivity;
import edu.tjlg.ecg_tester.application.ECGApplication;
import edu.tjlg.ecg_tester.common.CommonManage;
import edu.tjlg.ecg_tester.common.CustomProgressDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GetBlueToothECGDataTask extends AsyncTask<String, Integer,String>{
	private Context context;
	private CommonManage cm;
	//	private String filestr ;
	private File fi = null;
	private ProgressBar progressBarHorizontal;

	private boolean getDataFlag = false;
	private InputStream btInput = null;// 蓝牙数据输入流
	private FileOutputStream fos = null;//
	private final int dataBytes = 25000;//一次蓝牙传输的字节总数

	public GetBlueToothECGDataTask(Context context, ProgressBar progressBarHorizontal){
		this.context = context;
		this.progressBarHorizontal = progressBarHorizontal;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Integer... progresses) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(progresses);
		progressBarHorizontal.setProgress(progresses[0]);
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		//		filestr = params[0];
		cm = new CommonManage(context);
		String filestr = cm.creatECGFile(fos, fi);
		fi = new File(filestr);
		try {
			if(ECGApplication.getInstance().getBtSocketConnectFlag()){
				btInput = CollectDataActivity.btSocket.getInputStream();
				//btInput.mark(0);
				//				btInput.reset();
				byte[] b = new byte[dataBytes];  
				int readBytes = 0;  

				while (readBytes < dataBytes) {  
					int read = btInput.read(b, readBytes, dataBytes - readBytes);  
					System.out.println(read);  
					if (read == -1) {  
						break;  
					}  
					readBytes += read; 
					//调用publishProgress公布进度,最后onProgressUpdate方法将被执行  
					publishProgress((int) ((readBytes / (float) dataBytes) * 100)); 
				}  
				//关闭输入流接口和蓝牙连接接口
				btInput.close();
				CollectDataActivity.btSocket.close();
				CollectDataActivity.mApplication.setBtSocketConnectFlag(false);

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

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		TextView collectTextView = (TextView)((Activity)context).findViewById(R.id.collect_textview);
		TextView connectTv = (TextView)((Activity)context).findViewById(R.id.connect_ecg_tv);
		RelativeLayout measureECGBtn = (RelativeLayout)((Activity)context).findViewById(R.id.measure_ecg_rl);

		if(result!=null&& result.length()>0){
			collectTextView.setText("采集完毕！");

			fi = new File(result);
			if(fi.exists()){
				//打开波形图实例
				Intent intent = new Intent();
				intent.putExtra("filestr", result);
				intent.setClass(context, ViewWaveActivity.class);
				context.startActivity(intent);
				connectTv.setText("点击重新连接");
				collectTextView.setText("蓝牙连接已断开！");
				measureECGBtn.setVisibility(View.GONE);
				//				Activity activity = (Activity) context;
				//				activity.finish();
			}else{
				Toast.makeText(context, "文件已被移除", Toast.LENGTH_LONG).show();
			}
		}
	}
}

