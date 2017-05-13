package edu.tjlg.ecg_tester.jackson.collection;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import edu.tjlg.ecg_tester.ViewWaveActivity;

import edu.tjlg.ecg_tester.common.CommonManage;
import edu.tjlg.ecg_tester.jackson.event.BluetoothEvent;

public class GetBlueToothECGDataTask2 extends AsyncTask<String, Integer,String>{
	private final BluetoothSocket btSocket;
	private Context context;
	private CommonManage cm;

	private File fi = null;
	private ProgressBar progressBarHorizontal;

	private InputStream btInput = null;// 蓝牙数据输入流
	private FileOutputStream fos = null;//
	private final int dataBytes = 25000;//一次蓝牙传输的字节总数

	public GetBlueToothECGDataTask2(Context context, ProgressBar progressBarHorizontal,BluetoothSocket btSocket){
		this.context = context;
		this.progressBarHorizontal = progressBarHorizontal;
		this.btSocket = btSocket;
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
			if(BluetoothManager.getInstance().isConnecting()){
				btInput = BluetoothManager.getInstance().getBtSocket().getInputStream();
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
				btSocket.close();
				EventBus.getDefault().post(new BluetoothEvent(BluetoothEvent.DISCONNECT));

				fos=new FileOutputStream(fi.getAbsolutePath(),true);  
				//output file  
				fos.write(b,0,readBytes);
				//flush this stream  
				fos.flush();  
				//close this stream  
				fos.close(); 
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
		super.onPostExecute(result);
		EventBus.getDefault().post(new BluetoothEvent(BluetoothEvent.COLLECT_FINISHED));
		if(result!=null && result.length()>0){
			fi = new File(result);
			if(fi.exists()){
				//打开波形图实例
				Intent intent = new Intent();
				intent.putExtra("filestr", result);
				intent.setClass(context, ViewWaveActivity.class);
				context.startActivity(intent);
			}else{
				Toast.makeText(context, "文件已被移除", Toast.LENGTH_LONG).show();
			}
		}
	}
}