package edu.tjlg.ecg_tester;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.tjlg.ecg_tester.application.ECGApplication;
import edu.tjlg.ecg_tester.common.RPeakDetection;
import edu.tjlg.ecg_tester.database.DbOperate;
import edu.tjlg.ecg_tester.filter.DigitalFilter;
import edu.tjlg.ecg_tester.filter.LowPassFilter;
import edu.tjlg.ecg_tester.utils.ECGStaticPage;
import edu.tjlg.ecg_tester.utils.ECGWaveImageView;
import edu.tjlg.ecg_tester.utils.ObservableScrollView;
import edu.tjlg.ecg_tester.utils.ScrollViewListener;
import edu.tjlg.ecg_tester.utils.JsonUtils;
import edu.tjlg.ecg_tester.filter.MovingAverageFilter;
import edu.tjlg.ecg_tester.domain.TesterInfo;

public class ViewWaveActivity extends Activity implements ScrollViewListener{

	private String fileStr;
	private ECGStaticPage mECGStaticPage;
	private RelativeLayout ECGStaticPage;
	private ECGWaveImageView mECGWaveIamgeView ;
	private ObservableScrollView mScrollView;
	private TextView hearRateTextView, beginTimeTv ;
	private RelativeLayout sendBtn, reportBtn ,diagnosisBtn;
	
	private TextView mUserNameText;
	private TextView mUserSexText;
	private TextView mUserAgeText;
	private TextView mUserPromptText;

	private String ECGBuleToothFileStr;
	private File ECGBuletoothFile;
	private int scrWidth, scrHeight;
	//	private List<Float> dataList = new ArrayList<Float>();
	private float[] dataFiltered;
	private double[] data;
	private double[] dataInit;
	//�ֶλ�����������
	private float[] dataDrawWave;
	//private ArrayList<Byte> dataBufList = new ArrayList<Byte>();
	private static final int StartFlag = 0xFC;
	private static final int EndFlag = 0xFD;
	private static final int EscapeFlag = 0xEF;
	private static final int EscapeValue = 0x20;
	private static final float saticDistance = 110;

	private float[] rPeak = null;
	private RPeakDetection mRPeakDetection = new RPeakDetection();
	private TesterInfo testInfo = new TesterInfo();
	private RelativeLayout zoomInBtn, zoomOutBtn, lastWaveBtn, nextWaveBtn;
	private float zoomScale = 1.5f;
	private static final int DrawWaveWidth = 3000;
	private int allWaveNum = 1;
	private int numWave = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wave_layout);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		scrWidth = dm.widthPixels;
		scrHeight = dm.heightPixels;
		
		beginTimeTv = (TextView)findViewById(R.id.begintime_tv);
		hearRateTextView = (TextView)findViewById(R.id.heartrate_tv);
		sendBtn = (RelativeLayout)findViewById(R.id.send_rl);
		reportBtn = (RelativeLayout)findViewById(R.id.report_rl);
		diagnosisBtn = (RelativeLayout)findViewById(R.id.diagnosis_rl);
		zoomInBtn = (RelativeLayout)findViewById(R.id.zoom_in_wave);
		zoomOutBtn = (RelativeLayout)findViewById(R.id.zoom_out_wave);
		lastWaveBtn = (RelativeLayout)findViewById(R.id.last_wave);
		nextWaveBtn = (RelativeLayout)findViewById(R.id.next_wave);
		//mUserNameText = (TextView) findViewById(R.id.user_name_textView);
		//mUserSexText = (TextView) findViewById(R.id.user_sex_textView);
		//mUserAgeText = (TextView) findViewById(R.id.user_age_textView);
		mUserPromptText = (TextView) findViewById(R.id.user_prompt_textView);

		if(ECGApplication.getInstance().getBtSocketConnectFlag())
			try {
				CollectDataActivity.btSocket.close();
				CollectDataActivity.mApplication.setBtSocketConnectFlag(false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		mECGStaticPage = new ECGStaticPage();
		Bitmap bgBitmap = mECGStaticPage.createStaicECGPage(scrWidth,scrHeight);
		Drawable bgDrawable =new BitmapDrawable(this.getResources(),bgBitmap);
		ECGStaticPage = (RelativeLayout)findViewById(R.id.ecg_static_page);
		ECGStaticPage.setBackgroundDrawable(bgDrawable);
		data = getECGData();
		if(data!=null && data.length>0){
			//�˲�
			data = filterWave(data);

			dataFiltered= new float[data.length];
			for(int i = 0; i <data.length; i++){
				//���ѻ�׼��
				dataFiltered[i] = (float)data[i];
			}
			//�������������ֵ��ƽ��ֵ
			Arrays.sort(data);
			float bufMinData = 0;
			float bufMaxData = 0;

			for(int i = 0; i <20; i++){
				bufMinData += data[i];
			}
			bufMinData = bufMinData/20;
			for(int i = data.length-1; i >data.length-22; i--){
				bufMaxData += data[i];
			}
			bufMaxData = bufMaxData/20;

			float drawHeight = (bufMaxData-bufMinData)*100/55000+1;

			float bufDataMax_Min = bufMaxData-bufMinData+1;//��1��ʾ��ʼֵΪ1
			//������ֵ����Сֵ֮��С��0����ʾ��������
			if(bufDataMax_Min<0)
				Toast.makeText(this, "���������޷���ʾ�����Σ�", Toast.LENGTH_LONG).show();
			for(int i = 0; i <data.length; i++){
				//��׼��׼��
				dataFiltered[i] = (float)dataFiltered[i]-bufMinData;
				dataFiltered[i] = (float)dataFiltered[i]*((float)drawHeight)/(bufDataMax_Min) + saticDistance;
			}
			data=null;

			rPeak = mRPeakDetection.RPeakRecognize(dataFiltered);
			//			for(int i = 0; i<rPeak.length; i++)

			mScrollView = (ObservableScrollView)findViewById(R.id.wave_scrollView);
			mScrollView.setVerticalScrollBarEnabled(false);
			mScrollView.setScrollViewListener(this);

			mECGWaveIamgeView = (ECGWaveImageView)findViewById(R.id.wave_view);
			allWaveNum = dataFiltered.length/DrawWaveWidth+
					((dataFiltered.length%DrawWaveWidth)==0?0:1);

			System.out.println(dataFiltered.length+"-----allWaveNum-----"+allWaveNum);
			oprateWave();
			mECGWaveIamgeView.recycleBitmap();//����Bitmap,�����ڴ����;(���Գ��Զ��߳�ȥ����)
			mECGWaveIamgeView.setBitmapHeightWidth(dataFiltered.length, scrHeight);
			mECGWaveIamgeView.drawWave(dataFiltered, rPeak);
			System.out.print(fileStr);
			String hehe=fileStr.substring(26, 40);
			mECGWaveIamgeView.xie(dataFiltered,hehe);
			
		}else{
			Toast.makeText(this, "����û�вɼ��ɹ��������²ɼ�", Toast.LENGTH_LONG).show();
		}
		/**
		 * �ĵ�ͼ�Ŵ������ť--����
		 */
		zoomInBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//mECGWaveIamgeView.setScaleX(zoomScaleX*=1.5f);
				for(int i=0; i<dataFiltered.length; i++){
					dataFiltered[i] *= zoomScale;
				}
				mECGWaveIamgeView.recycleBitmap();
				mECGWaveIamgeView.setBitmapHeightWidth(dataFiltered.length, scrHeight);
				mECGWaveIamgeView.drawWave(dataFiltered, rPeak);
			}
		});

		/**
		 * �ĵ�ͼ��С������ť--����
		 */
		zoomOutBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//mECGWaveIamgeView.setScaleX(zoomScaleX/=1.5f);
				for(int i=0; i<dataFiltered.length; i++){
					dataFiltered[i] /= zoomScale;
				}
				mECGWaveIamgeView.recycleBitmap();
				mECGWaveIamgeView.setBitmapHeightWidth(dataFiltered.length, scrHeight);
				mECGWaveIamgeView.drawWave(dataFiltered, rPeak);
			}
		});
		sendBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				intent.setType("text/*");
				Uri uri;
				if(fileStr!=null){
					uri = Uri.fromFile(new File(fileStr));
				}else{
					uri = Uri.fromFile(new File(fileStr));
				}

				//intent.setData(uri);
				intent.putExtra(Intent.EXTRA_STREAM, uri);
				Intent.createChooser(intent, "�����ĵ�����");
				startActivity(intent);
				finish();
			}
		});
		//		waveMianLayout=new LinearLayout(this);  
		//		waveMianLayout.setLayoutParams(new LinearLayout.LayoutParams(-1,-1)); 
		//		mECGWaveIamgeView = new ECGWaveIamgeView(ViewWaveActivity.this,dataFiltered);
		//		mECGWaveIamgeView.setLayoutParams(new LinearLayout.LayoutParams(-1,-2)); 
		//
		//		waveMianLayout.addView(mECGWaveIamgeView);//���iv  
		//		setContentView(waveMianLayout);//��ʾmanLayout 
		//dataFiltered =null;


	/**
	 * ���Ͱ�ť����
	 */
	sendBtn.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.setType("text/*");
			Uri uri;
			if(fileStr!=null){
				uri = Uri.fromFile(new File(fileStr));
			}else{
				uri = Uri.fromFile(ECGBuletoothFile);
			}

			//intent.setData(uri);
			intent.putExtra(Intent.EXTRA_STREAM, uri);
			Intent.createChooser(intent, "�����ĵ�����");
			startActivity(intent);

			//				finish();
		}
	});

	/**
	 * �ĵ籨�水ť����
	 */
	reportBtn.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			/**
			//�˴�ֱ��newһ��Dialog�����������ʵ������ʱ��������
			Dialog dialog = new Dialog(ViewWaveActivity.this, R.style.TesterInfoDialog);
			//��������ContentView
			View contentView = LayoutInflater.from(ViewWaveActivity.this).inflate(R.layout.tester_info_layout, null);
			dialog.setContentView(contentView);

			testNameTv = (TextView)contentView.findViewById(R.id.tester_name_tv);
			testGenderTv = (TextView)contentView.findViewById(R.id.tester_gender_tv);
			testAgeTv = (TextView)contentView.findViewById(R.id.tester_age_tv);
			testPhoneNumTv = (TextView)contentView.findViewById(R.id.tester_phoneNum_tv);
			testBeginTimeTv = (TextView)contentView.findViewById(R.id.tester_begintime_tv);
			testIllnessTv = (TextView)contentView.findViewById(R.id.tester_illness_tv);

			testNameTv.setText("������"+testInfo.getName());
			testGenderTv.setText("�Ա�"+testInfo.getGender());
			testAgeTv.setText("���䣺"+testInfo.getAge());
			testPhoneNumTv.setText("�绰��"+testInfo.getPhoneNum());
			testBeginTimeTv.setText("����ʱ�䣺"+testInfo.getBeginTime());;
			testIllnessTv.setText("��ʷ��"+testInfo.getIllness());

			dialog.show();	
			 */		
			getHeartRR(rPeak);
			float[] maxRRWave = getPointsFWave(dataFiltered,maxRRPointX1, maxRRPointX2, (float)scrHeight*3/4/2);
			float[] minRRWave = getPointsFWave(dataFiltered,minRRPointX1, minRRPointX2, (float)scrHeight*3/4/2);

			Intent intent = new Intent();
			intent.setClass(ViewWaveActivity.this, ReportActivity.class);
			Bundle bundle = new Bundle();
			bundle.putFloatArray("_rpeak", rPeak);
			bundle.putFloatArray("_maxRRWave", maxRRWave);
			bundle.putFloatArray("_minRRWave", minRRWave);
			bundle.putString("_testName", testInfo.getName());
			bundle.putString("_testIllness", testInfo.getIllness());
			intent.putExtras(bundle);
			startActivity(intent);
		}
	});


	/**
	 * ��ϰ�ť����
	 */
	diagnosisBtn.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(ViewWaveActivity.this, DiagnosisActivity.class);
			intent.putExtra("beginTime", testInfo.getBeginTime());
			intent.putExtra("phoneNum", testInfo.getPhoneNum());
			startActivity(intent);
		}
	});

	/**
	 * ��ǰ�鿴��һ�β���
	 */
	lastWaveBtn.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if(numWave>1){
				numWave--;//�鿴���α�ż�һ
				oprateWave();
				//����ScrollViewλ��
				mScrollView.scrollTo(DrawWaveWidth, 0);
			}
		}
	});

	/**
	 * ���鿴��һ�β���
	 */
	nextWaveBtn.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(numWave<allWaveNum){
				System.out.println(numWave+"-----------��һ��-----------"+allWaveNum);
				numWave++;	
				oprateWave();
				mScrollView.scrollTo(0, 0);
			}
		}
	});

	//		waveMianLayout=new LinearLayout(this);  
	//		waveMianLayout.setLayoutParams(new LinearLayout.LayoutParams(-1,-1)); 
	//		mECGWaveIamgeView = new ECGWaveIamgeView(ViewWaveActivity.this,dataFiltered);
	//		mECGWaveIamgeView.setLayoutParams(new LinearLayout.LayoutParams(-1,-2)); 
	//
	//		waveMianLayout.addView(mECGWaveIamgeView);//���iv  
	//		setContentView(waveMianLayout);//��ʾmanLayout 
	//dataFiltered =null;
}

/**
 * ���ĵ����ݷֶκ󣬽���Ԥ����
 */
public void oprateWave(){
	
	if(numWave>0 && numWave<=allWaveNum){
		/*
		 * �鿴���ݷֶε����һ�����ֶ����ݳ�ʼ������ΪdataFiltered.length-(numWave-1)*maxDrawWaveWidth
		 * �����ֶ����ݣ��ֶ����ݳ�ʼ������ΪmaxDrawWaveWidth
		 */
		if(numWave == allWaveNum){
			dataDrawWave = new float[dataFiltered.length-(numWave-1)*DrawWaveWidth];
		}else{
			dataDrawWave = new float[DrawWaveWidth];
		}
		for(int i=0; i<DrawWaveWidth && i<dataDrawWave.length; i++){
			dataDrawWave[i] = dataFiltered[i+(numWave-1)*DrawWaveWidth];
		}
		/*
		 * �������������ֵ��ƽ��ֵ,ת���ɿ�����ʾ�����ݴ�С
		 */
		dataDrawWave = datumLine(dataDrawWave, scrHeight/2, saticDistance);
		//��ͼ
		drawEcgWave(DrawWaveWidth,dataDrawWave, scrHeight, rPeak);
		
		//numWave���1�����һ��
		if(numWave == 1)
			Toast.makeText(this, "��һ�β���", Toast.LENGTH_LONG).show();
		if(numWave == allWaveNum)
			Toast.makeText(this, "���һ�β���", Toast.LENGTH_LONG).show();
	}else{
		Toast.makeText(this, "", Toast.LENGTH_LONG).show();
	}
}
/**
 * �������������ֵ��ƽ��ֵ,ת���ɿ�����ʾ�����ݴ�С
 * @param dataDrawWave
 * @param imageHeight
 * @param saticDistance
 * @return
 */
public float[] datumLine(float[] dataDrawWave, int imageHeight, float saticDistance){
	//���ݻ��棬���ڼ���
	float[] dataTemp = new float[dataDrawWave.length];
	for(int i=0 ; i<dataDrawWave.length; i++)
		dataTemp[i] = dataDrawWave[i];
	//����
	Arrays.sort(dataTemp);
	//��ֵ
	float bufMinData = 0;
	float bufMaxData = 0;
	//ȡ��ֵ��ǰ20����ƽ��ֵ����Ϊ��ֵ��׼
	for(int i = 0; i <20; i++){
		bufMinData += dataTemp[i];
	}
	bufMinData = bufMinData/20;
	for(int i = dataTemp.length-1; i >dataTemp.length-22; i--){
		bufMaxData += dataTemp[i];
	}
	bufMaxData = bufMaxData/20;
	//��ֵ֮��
	float bufDataMax_Min = bufMaxData-bufMinData+1;//��1��ʾ��ʼֵΪ1
	//������ֵ����Сֵ֮��С��0����ʾ��������
	if(bufDataMax_Min<0)
		Toast.makeText(this, "���������޷���ʾ�����Σ�", Toast.LENGTH_LONG).show();
	for(int i = 0; i <dataTemp.length; i++){
		//��׼��׼��
		dataDrawWave[i] = (float)dataDrawWave[i]-bufMinData;
		//�ñ���ת�ɿ���ʾ������
		dataDrawWave[i] = (float)dataDrawWave[i]*((float)imageHeight)/(bufDataMax_Min) + saticDistance;
	}

	return dataDrawWave;
}

/**
 * mECGWaveIamgeView
 * @param maxDrawWaveWidth
 * @param dataDrawWave
 * @param scrHeight
 * @param rPeak
 */
public void drawEcgWave(int maxDrawWaveWidth, float[] dataDrawWave, int scrHeight, float[] rPeak){
	mECGWaveIamgeView.recycleBitmap();//����Bitmap,�����ڴ����;(���Գ��Զ��߳�ȥ����)
	if(dataDrawWave.length<maxDrawWaveWidth){
		mECGWaveIamgeView.setBitmapHeightWidth(dataDrawWave.length, scrHeight );
	}else{
		mECGWaveIamgeView.setBitmapHeightWidth(maxDrawWaveWidth, scrHeight );
	}

	List<Float> rPeakListTemp1 = new ArrayList<Float>();
	
	for(int i = 0;i<rPeak.length;i++){
		if(rPeak[i] >= (numWave-1)*maxDrawWaveWidth && rPeak[i] <= numWave*maxDrawWaveWidth){
			rPeakListTemp1.add(rPeak[i]-(numWave-1)*maxDrawWaveWidth);
		}
	}
	float[] rPeakDraw = new float[rPeakListTemp1.size()];
	for(int i = 0;i<rPeakListTemp1.size();i++){
		rPeakDraw[i] = rPeakListTemp1.get(i);
	}
	mECGWaveIamgeView.drawWave(dataDrawWave, rPeakDraw);
}

//�����СRR�����ʶ,ע��RR�����������ǳɷ��ȵ�
int maxRRPointX1 = 0;
int minRRPointX1 = 0;
int maxRRPointX2 = 0;
int minRRPointX2 = 0;
/**
 * ����RR���ڡ����/��СRR����λ��
 * @param rPeak
 */
public void getHeartRR(float[] rPeak){

	float[] heartRR = new float[rPeak.length - 1];
	//�����СRR���ڸ�ֵΪ��һ��RR����ֵ������Ƚ�ʱ���ִ���
	float maxRR = rPeak[1] - rPeak[0];
	float minRR = rPeak[1] - rPeak[0];

	for(int i = 0; i<rPeak.length-1;i++){
		heartRR[i] = rPeak[i+1]-rPeak[i];
		if (heartRR[i] > maxRR)
		{
			maxRR = heartRR[i];
			maxRRPointX1 = (int)rPeak[i];
			maxRRPointX2 = (int)rPeak[i + 1];
		}
		if (heartRR[i] < minRR)
		{
			minRR = heartRR[i];
			minRRPointX1 = (int)rPeak[i];
			minRRPointX2 = (int)rPeak[i + 1];
		}

	}
}


	/**
	 * ���ݲ�������ȡX1��X2�Ĳ���
	 * @param dataECG--�ĵ�����
	 * @param mRRPointX1--��ʼ��
	 * @param mRRPointX2--�ս��
	 * @param pHeight--�ؼ��߶�
	 * @return--X1��X2�Ĳ�������
	 */
	public float[] getPointsFWave(float[] dataECG, int mRRPointX1, int mRRPointX2, float pHeight)
	{
		int firstPoint = 0;
		float maxWaveY = dataECG[mRRPointX1];
		float minWaveY = dataECG[mRRPointX1];
		//RR���ڵ�һ������ǰȡ��
		if (mRRPointX1 < dataECG.length){
			if (mRRPointX1 - 100 > 0){
				firstPoint = (int)mRRPointX1 - 100;
			}else if (mRRPointX1 - 100 <= 0 && mRRPointX1 - 50 > 0){
				firstPoint = (int)mRRPointX1 - 50;
			}else if (mRRPointX1 - 50 <= 0 && mRRPointX1 - 20 > 0){
				firstPoint = (int)mRRPointX1 - 20;
			}else{
				firstPoint = (int)mRRPointX1;
			}
		}
		//RR���ڵ�һ�������ȡ��
		int pointsFWaveLength = 150;
		if (dataECG.length - (int)mRRPointX2 < pointsFWaveLength){
			pointsFWaveLength = dataECG.length - (int)mRRPointX2;
		}
		//ȡ������ߺ���͵�
		for (int i = firstPoint; i < (int)mRRPointX2 + pointsFWaveLength; i++){
			if (i < dataECG.length){
				if (maxWaveY < dataECG[i]){
					maxWaveY = dataECG[i];
				}
				if (minWaveY > dataECG[i])
				{
					minWaveY = dataECG[i];
				}
			}
			else{
				break;
			}
		}
		float[] pointsFWave = new float[(int)(mRRPointX2 + pointsFWaveLength - firstPoint)];
		float maxWaveHeight = maxWaveY - minWaveY;
		for (int i = firstPoint; i < (int)mRRPointX2 + pointsFWaveLength; i++){
			if (i < dataECG.length ){
				pointsFWave[(int)(i - firstPoint)] = (float)(pHeight - (dataECG[i] - minWaveY) * (pHeight / maxWaveHeight));

			}else{
				break;
			}
		}
		return pointsFWave;

	}
	
	
	/**
	 * ��ȡ����
	 * @return
	 */
	private double[] getECGData(){

		ArrayList<Byte> dataBufList = new ArrayList<Byte>();
		List<Float> dataList = new ArrayList<Float>();
		fileStr = getIntent().getStringExtra("filestr");

		try {
			FileInputStream fin = new FileInputStream(fileStr); 
			int length = fin.available();
			byte [] buffer = new byte[length];   
			fin.read(buffer);  
			fin.close();
			//iΪ��ȡ�ֽ��±�
			int i = 0;
			/*
			 * ����"##"��Ϊ���λ������Ϣ�������ĵ����ݷ���
			 * "#"ASCII��ʮ�����Ʊ�ʾΪ0x23
			 * �˰汾û�ж�ȡ������Ϣ
			 */
			//Ϊ�˱�������������ֽ��黺���СΪlength
			byte [] testerInfobuffer = new byte[length];
			for(; i<buffer.length; i++){
				testerInfobuffer[i] = buffer[i];
				if(((buffer[i]&0xFF) == 0x23) && ((buffer[i+1]&0xFF) == 0x23))
					break;
			}
			testInfo = getTestInfo(testerInfobuffer, i);
			i+=2;
			for(; i<buffer.length; i++)
				if((buffer[i]&0xFF) == StartFlag)
					break;
			for(; i<buffer.length; i++){
				if((buffer[i]&0xFF) == StartFlag){
					continue;
				}else if((buffer[i]&0xFF) == EndFlag){
					if(dataBufList.size()<10)
						if(dataBufList.size()==3){
							if((dataBufList.get(0))>0){
								dataList.add((float)((dataBufList.get(0)&0xFF)<<16|(dataBufList.get(1)&0xFF)<<8|(dataBufList.get(2)&0xFF)));
							}else{
								dataList.add((float)((0xff << 24)|(dataBufList.get(0)&0xFF)<<16|(dataBufList.get(1)&0xFF)<<8|(dataBufList.get(2)&0xFF)));
							}

							dataBufList.clear();
						}
					continue;
				}else{
					if((buffer[i]&0xFF) == EscapeFlag){
						dataBufList.add((byte)((buffer[i++]&0xFF)^EscapeValue));
					}else{
						dataBufList.add(buffer[i]);
					}
				}
			}
			buffer = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
		double[] data = new double[dataList.size()];
		for(int j = 0; j<dataList.size(); j++){
			data[j]=dataList.get(j);
		}
		//		Log.i("data.length", "-- "+data.length);
		//		data = deleteData(data,data.length-1000,data.length-1);
		//		Log.i("data.length", "-- "+data.length);
		dataBufList.clear();
		dataList.clear();
		return data;
	}

	/**
	 * �����˲�
	 * @param data
	 * @return
	 */
	private double[] filterWave(double[] data){
		//�����˲���ȥ����ƫ��
		double[] datatemp = new double[data.length];
		double k = 0.7;
		int samplerate = 250;
		double fc = 0.8 / samplerate;
		double[] a = new double[2];
		double[] b = new double[2];
		double alpha = (1 - k * Math.cos(2 * Math.PI * fc) - Math.sqrt(2 * k * (1 - Math.cos(2 * Math.PI * fc)) - Math.pow(k, 2) * Math.pow(Math.sin(2 * Math.PI * fc), 2))) / (1 - k);
		a[0] = 1 - alpha;
		a[1] = 0;
		b[0] = 1;
		b[1] = -1 * alpha;
		DigitalFilter digitalFilter = new DigitalFilter(a, b, data, 1d);
		datatemp = digitalFilter.zeroFilter();

		for(int i= 0; i<data.length; i++){
			data[i] = data[i]-datatemp[i];
		}

		//��ͨ�˲�
		LowPassFilter lowPassFilter = new LowPassFilter();
		data = lowPassFilter.lvboFilter(data);
		//����ƽ���˲�
		//		MovingAverageFilter movingAverageFilter = new MovingAverageFilter();
		//		data = movingAverageFilter.filter(data);

		return data;

	}

	public double[] deleteData(double[] data,int startNum,int lastNum ){
		List<Double> dataList = new ArrayList<Double>();
		for(int i = 0; i<data.length; i++){
			dataList.add(data[i]);
		}
		for(int j = lastNum;j>startNum;j--){
			dataList.remove(j);
		}

		double[] newdata = new double[dataList.size()];
		for(int j = 0; j<dataList.size(); j++){
			newdata[j] = dataList.get(j);
		}
		return newdata;

	}

	/**
	 * ��ȡ˲ʱ����
	 * @param offsetbiao
	 * @param rpeaks
	 * @return
	 */
	private int getEcgRate(float offsetbiao, float[] rpeaks){

		int rate = 0;
		int i = 1;

		for (; i < rpeaks.length; i++){
			if (rpeaks[i] >= offsetbiao){
				if (i + 5 <= rpeaks.length - 1){
					// MessageBox.Show("hello!");
					rate = (int)((250 * 60 * 5f) / (rpeaks[i + 5] - rpeaks[i]));

					if (rate < 10 || rate > 200) 
						rate = 0;
					break;
				}
				else{
					int number = rpeaks.length - i - 1;
					//   MessageBox.Show("hello2!");
					if (number > 6){
						rate = (int)((250 * 60 * number) / (rpeaks[rpeaks.length - 1] - rpeaks[i]));
						if (rate < 10 || rate > 200) rate = 0;
						break;
					}
					else
						break;
				}
			}

		}
		return rate;

	}

	@Override
	public void onScrollChanged(ObservableScrollView scrollView, int x, int y,
			int oldx, int oldy) {
		// TODO Auto-generated method stub

		int heartRate = getEcgRate(x, rPeak);
		
		if (heartRate > 80) {
			mUserPromptText.setVisibility(View.VISIBLE);
		} else {
			mUserPromptText.setVisibility(View.INVISIBLE);
		}
		
		hearRateTextView.setText("" + heartRate);
	}

	//	//���̼���
	//		public boolean onKeyDown(int keyCode, KeyEvent e){
	//			if(keyCode == KeyEvent.KEYCODE_BACK){
	//
	//				Intent intent = new Intent();
	//				intent.setClass(ViewWaveActivity.this, CollectDataActivity.class);
	//				startActivity(intent);
	//				finish();
	//				return true;
	//			}
	//
	//			return false;
	//		}
	public static TesterInfo getTestInfo(byte [] testerInfobuffer, int i){
		//������Ϣ�ַ�����JSON��ʽ����Ҫת��
		String testerInfoStr = null;
		//���̲�������Ϣ����ȡ���õ���Ϣ��ȥ�����õ��ֽ�
		byte [] testerInfoBytes = new byte [i];

		TesterInfo testerInfo = new TesterInfo();
		for(int j = 0; j<i; j++){
			testerInfoBytes[j] = testerInfobuffer[j];
		}
		if(testerInfobuffer != null){
			testerInfoStr = hexStr2Str(byte2HexStr(testerInfoBytes));
			testerInfo = JsonUtils.getTesterInfo(testerInfoStr);
		}
		return testerInfo;
	}
	//	 bytesת����ʮ�������ַ���
	public static String byte2HexStr(byte[] b) {
		String hs="";
		String stmp="";
		for (int n=0;n<b.length;n++) {
			stmp=(Integer.toHexString(b[n] & 0XFF));
			if (stmp.length()==1) hs=hs+"0"+stmp;
			else hs=hs+stmp;
		}
		return hs.toUpperCase();
	}
	//	 ʮ������ת���ַ��� 
	public static String hexStr2Str(String hexStr) {  
		String str = "0123456789ABCDEF";  
		char[] hexs = hexStr.toCharArray();  
		byte[] bytes = new byte[hexStr.length()/2];  
		int n;  
		for (int i = 0; i < bytes.length; i++) {  
			n = str.indexOf(hexs[2 * i]) * 16;  
			n += str.indexOf(hexs[2 * i + 1]);  
			bytes[i] = (byte) (n & 0xff);  
		}  
		return new String(bytes);  
	}  

	/**
	 * �����ļ�
	 * @param oldPath
	 * @param newPath
	 */
	public void copyFile(String oldPath, String newPath) {   
		try {   
			int bytesum = 0;   
			int byteread = 0;   
			File oldfile = new File(oldPath);   
			if (oldfile.exists()) { //�ļ�����ʱ   
				InputStream inStream = new FileInputStream(oldPath); //����ԭ�ļ�   
				FileOutputStream fs = new FileOutputStream(newPath);   
				byte[] buffer = new byte[1444];   
				while ( (byteread = inStream.read(buffer)) != -1) {   
					bytesum += byteread; //�ֽ��� �ļ���С   
					System.out.println(bytesum);   
					fs.write(buffer, 0, byteread);   
				}   
				inStream.close();  
				fs.close();
			}   
		}   
		catch (Exception e) {   
			System.out.println("���Ƶ����ļ���������");   
			e.printStackTrace();   

		}   

	}   

	public void fileChannelCopy(File s, File t) {
		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;

		try {
			fi = new FileInputStream(s);
			fo = new FileOutputStream(t);
			in = fi.getChannel();//�õ���Ӧ���ļ�ͨ��
			out = fo.getChannel();//�õ���Ӧ���ļ�ͨ��
			in.transferTo(0, in.size(), out);//��������ͨ�������Ҵ�inͨ����ȡ��Ȼ��д��outͨ��
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fi.close();
				in.close();
				fo.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mECGWaveIamgeView.recycleBitmap();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//mECGWaveIamgeView.recycleBitmap();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}


}


