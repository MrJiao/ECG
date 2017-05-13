package edu.tjlg.ecg_tester.fragment;

import edu.tjlg.ecg_tester.R;
import edu.tjlg.ecg_tester.view.HistogramPlotView;
import edu.tjlg.ecg_tester.view.ScatterPlotView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class HistogramPlotFragment extends Fragment{
	private View mView;
	private DisplayMetrics dm;
	private HistogramPlotView mHistogramPlotView;
	private static final int Samplerate = 250;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		float[] heartRR = bundle.getFloatArray("_heartRR");
		float maxRR= bundle.getFloat("_maxRR");
		float minRR = bundle.getFloat("_minRR");

		//����RR���ڼ������������
		float[] heartRate = new float[heartRR.length];
		for(int i=0; i<heartRR.length; i++ ){
			heartRate[i] = Samplerate*60/heartRR[i];
		}
		//�������
		float fastHeartRate = Samplerate*60/minRR;
		//��������
		float lowHeartRate = Samplerate*60/maxRR;
		//�����RR���ں���СRR���ڷֳ�20�Σ��ж�ÿ���ңҼ��ڷֲ����
		int[] histogramHRpos = new int[20];

		//intervalRRֱ��ͼ���Ϊ(float)(maxRR - minRR) / (float)20;
		float intervalHR = (float)(fastHeartRate - lowHeartRate) / (float)20;
		for (int i = 0; i < heartRate.length - 1; i++){
			for (int j = 0; j < 20; j++){
				if (heartRate[i] > (lowHeartRate + intervalHR * j) && 
						heartRate[i] <= (lowHeartRate + intervalHR * (j + 1))){
					histogramHRpos[j]++;
					break;
				}
			}

		}
		//�������ķֶ�
		int maxHisHRpos = histogramHRpos[0];
		for (int i = 0; i < histogramHRpos.length; i++)
		{
			if (maxHisHRpos < histogramHRpos[i])
			{
				maxHisHRpos = histogramHRpos[i];
			}
		}

		mView = inflater.inflate(R.layout.report_histogramplot_layout,container, false);
		dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams
				(dm.widthPixels, dm.heightPixels*3/4);
		param.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		param.topMargin=80;
		mHistogramPlotView = (HistogramPlotView)mView.findViewById(R.id.histogram_plot);
		mHistogramPlotView.setLayoutParams(param);

		//ת���ɿ���ʾ�����������߶�
		int[] heightHR = new int[20];
		float viewHeight = dm.heightPixels*3/4-100;
		for(int i = 0; i<20; i++){
			//��20��RR��������ת������ʾ�߶�
			heightHR[i] = (int)((double)(viewHeight) * 
					(double)histogramHRpos[i] / (double)maxHisHRpos);
		}

		mHistogramPlotView.setLinePoint(heightHR, dm.widthPixels*3/4, viewHeight,
				dm.widthPixels/12, intervalHR, lowHeartRate, maxHisHRpos);
		return mView;
	}

}
