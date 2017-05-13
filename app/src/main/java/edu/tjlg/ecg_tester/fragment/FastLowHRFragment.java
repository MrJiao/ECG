package edu.tjlg.ecg_tester.fragment;

import edu.tjlg.ecg_tester.R;
import edu.tjlg.ecg_tester.view.FastLowPlotView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class FastLowHRFragment extends Fragment {
	private View mView;
	private FastLowPlotView fastPlotView;
	private float marginWidth;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.report_fastlowplot_layout,container, false);
		Bundle bundle = getArguments();
		float[] maxRRWave = bundle.getFloatArray("_maxRRWave");
		float[] minRRWave = bundle.getFloatArray("_minRRWave");

		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams
				(dm.widthPixels, dm.heightPixels*3/4);
		param.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		param.topMargin=40;
		fastPlotView = (FastLowPlotView)mView.findViewById(R.id.fastlowHR_plot);
		fastPlotView.setLayoutParams(param);
		
		marginWidth = 10;
		fastPlotView.setLinePoint(getDrawHRWavePos(maxRRWave, 0, marginWidth), getDrawHRWavePos(minRRWave, dm.heightPixels*3/4/2, marginWidth));
		
		return mView;
	}
	
	/**
	 * 将最快/最慢心率的波形转化成可用于画图显示float[]数组
	 * @param mRRWave
	 * @param yPos:Y轴上两个心电波形的差距
	 * @param marginWidth
	 * @return
	 */
	public float[] getDrawHRWavePos(float[] mRRWave, int yPos, float marginWidth){
		float[] drawHRWave = new float[mRRWave.length*4];
		float startX = 0;
		float startY = 0;
		float stopX = 0;
		float stopY = 0;
		int count ;
		int interval = 1;
		for (int i =0 ; i<mRRWave.length-1; i++){
			startX = i*interval+marginWidth;
			startY = mRRWave[i]/2 + yPos;
			stopX = (i+1)*interval+marginWidth;
			stopY =  mRRWave[i+1]/2+yPos;

			count = 4*i;
			drawHRWave[count] = startX ;
			drawHRWave[count+1] = startY ;
			drawHRWave[count+2] = stopX ;
			drawHRWave[count+3] = stopY ;

		}
		return drawHRWave;
	}

}
