package edu.tjlg.ecg_tester.fragment;

import edu.tjlg.ecg_tester.R;
import edu.tjlg.ecg_tester.application.ECGApplication;
import edu.tjlg.ecg_tester.view.ScatterPlotView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ScatterPlotFragment extends Fragment{
	private View mView;
	private DisplayMetrics dm;
	private ScatterPlotView mScatterPlotView;
	private TextView nameTv, illnessTv, numsRWavetTv, avgHRTv, fastHRTv, lowHRTv;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle bundle = getArguments();
		float[] heartRR = bundle.getFloatArray("_heartRR");
		float maxRR= bundle.getFloat("_maxRR");
		float minRR= bundle.getFloat("_minRR");
		float avgHR = bundle.getFloat("_avgHR");
		
		String testName = bundle.getString("_testName");
		String testIllness = bundle.getString("_testIllness");
		
		mView = inflater.inflate(R.layout.report_scatterplot_layout,container, false);

		nameTv = (TextView)mView.findViewById(R.id.test_name_tv);
		illnessTv= (TextView)mView.findViewById(R.id.test_illness_tv);
		numsRWavetTv= (TextView)mView.findViewById(R.id.rWave_nums_tv);
		avgHRTv = (TextView)mView.findViewById(R.id.avg_hr_tv);
		fastHRTv = (TextView)mView.findViewById(R.id.fast_hr_tv);
		lowHRTv = (TextView)mView.findViewById(R.id.low_hr_tv);
		
		nameTv.setText("姓名: "+testName);
		illnessTv.setText("病情: "+testIllness);
		numsRWavetTv.setText("R波数量: "+heartRR.length+" 个");
		avgHRTv.setText("平均心率: "+(int)avgHR+" 次/分钟");
		fastHRTv.setText("最快心率: "+(int)(ECGApplication.Smaplerate*60/minRR)+" 次/分钟");
		lowHRTv.setText("最快心率: "+(int)(ECGApplication.Smaplerate*60/maxRR)+" 次/分钟");
		dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams
				(dm.widthPixels/2, dm.heightPixels*3/4);

		param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		param.topMargin=80;
		mScatterPlotView = (ScatterPlotView)mView.findViewById(R.id.scatter_plot);
		mScatterPlotView.setLayoutParams(param);

		mScatterPlotView.setLinePoint(heartRR, dm.heightPixels*3/4-140, dm.heightPixels*3/4-140,
				dm.widthPixels/12, maxRR);
		return mView;
	}

}
