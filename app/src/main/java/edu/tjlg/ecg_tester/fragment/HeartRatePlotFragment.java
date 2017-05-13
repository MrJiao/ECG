package edu.tjlg.ecg_tester.fragment;

import edu.tjlg.ecg_tester.R;
import edu.tjlg.ecg_tester.view.FastLowPlotView;
import edu.tjlg.ecg_tester.view.HeartRatePlotView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class HeartRatePlotFragment extends Fragment{

	private View mView;
	private HeartRatePlotView heartRatePlotView;
	private float marginWidth;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.report_heartrateplot_layout,container, false);
		Bundle bundle = getArguments();
		float[] heartRR = bundle.getFloatArray("_heartRR");
		float minRR = bundle.getFloat("_minRR");
		
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams
				(dm.widthPixels, dm.heightPixels*3/4);
		param.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		param.topMargin=40;
		heartRatePlotView = (HeartRatePlotView)mView.findViewById(R.id.hr_plot);
		heartRatePlotView.setLayoutParams(param);

		int maxValueY = getMaxValueY((int)(250*60/minRR));
		float viewHeight = dm.heightPixels*3/4-100;
		marginWidth = dm.widthPixels/12;
		heartRatePlotView.setLinePoint(getDrawHRWavePos(heartRR, maxValueY, viewHeight, marginWidth),
				maxValueY, dm.widthPixels*3/4, viewHeight, marginWidth);
		return mView;
	}

	public int getMaxValueY(int maxHeartRate){
		int maxValueY = 50;
		if (maxHeartRate <= 50)
        {
            maxValueY = 50;
        }
        else if (maxHeartRate > 50 && maxHeartRate <= 100)
        {
            maxValueY = 100;
        }
        else if (maxHeartRate > 100 && maxHeartRate <= 150)
        {
            maxValueY = 150;
        }
        else if (maxHeartRate > 150 && maxHeartRate <= 200)
        {
            maxValueY = 200;
        }
        else
        {
            maxValueY = 250;
        }

		return maxValueY;
	}
	public float[] getDrawHRWavePos(float[] mRRWave, int maxValueY, float viewHeight, float marginWidth){
		float[] drawHRWave = new float[mRRWave.length*4];
		float startX = 0;
		float startY = 0;
		float stopX = 0;
		float stopY = 0;
		int count ;
		int interval = 1;
		
		for (int i =0 ; i<mRRWave.length-1; i++){
			startX = i*interval+marginWidth;
			startY = viewHeight - viewHeight / maxValueY*(250*60/mRRWave[i]);
			stopX = (i+1)*interval+marginWidth;
			stopY =  viewHeight - viewHeight / maxValueY*(250*60/mRRWave[i+1]);

			count = 4*i;
			drawHRWave[count] = startX ;
			drawHRWave[count+1] = startY ;
			drawHRWave[count+2] = stopX ;
			drawHRWave[count+3] = stopY ;

		}
		return drawHRWave;
	}
}
