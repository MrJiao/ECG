package edu.tjlg.ecg_tester;

import java.util.ArrayList;
import java.util.List;

import edu.tjlg.ecg_tester.adapter.FragmentAdapter;
import edu.tjlg.ecg_tester.application.ECGApplication;
import edu.tjlg.ecg_tester.fragment.FastLowHRFragment;
import edu.tjlg.ecg_tester.fragment.HeartRatePlotFragment;
import edu.tjlg.ecg_tester.fragment.HistogramPlotFragment;
import edu.tjlg.ecg_tester.fragment.ScatterPlotFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

public class ReportActivity extends FragmentActivity{

	private ViewPager mViewPager;
	private FragmentAdapter adapter;
	private RelativeLayout tabScatterPlot,tabFastLowHR,tabHistogramPlot,tabHeartRatePlot;
	private DisplayMetrics dm;
	private int currIndex = 0;
	private int position_one;

	private ScatterPlotFragment  mScatterPlotFg= new ScatterPlotFragment();
	private FastLowHRFragment mFastLowHRfg = new FastLowHRFragment();
	private HistogramPlotFragment mHistogramPlotFg = new HistogramPlotFragment();
	private HeartRatePlotFragment mHeartRatePlotFg = new HeartRatePlotFragment();

	private float[] rPeak;
	float[] heartRR;
	float minRR = -1;
	float maxRR = -1;
	float sumHeartRate = 0;

	//最大最小RR坐标标识,注意RR间期与心率是成反比的
	float maxRRPointX1 = 0;
	float minRRPointX1 = 0;
	float maxRRPointX2 = 0;
	float minRRPointX2 = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.report_layout);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		rPeak = bundle.getFloatArray("_rpeak");
		float[] maxRRWave = bundle.getFloatArray("_maxRRWave");
		float[] minRRWave = bundle.getFloatArray("_minRRWave");
		String testName = bundle.getString("_testName");
		String testIllness = bundle.getString("_testIllness");
		getHeartRR(rPeak);

		Bundle bundleScatter = new Bundle();
		bundleScatter.putFloatArray("_heartRR", heartRR);
		bundleScatter.putFloat("_maxRR", maxRR);
		bundleScatter.putFloat("_minRR", minRR);
		bundleScatter.putFloat("_avgHR", sumHeartRate/heartRR.length);
		bundleScatter.putString("_testName", testName);
		bundleScatter.putString("_testIllness", testIllness);
		mScatterPlotFg.setArguments(bundleScatter);

		Bundle bundleFastLow = new Bundle();
		bundleFastLow.putFloatArray("_maxRRWave", maxRRWave);
		bundleFastLow.putFloatArray("_minRRWave", minRRWave);
		mFastLowHRfg.setArguments(bundleFastLow);

		Bundle bundleHistogram = new Bundle();
		bundleHistogram.putFloatArray("_heartRR", heartRR);
		bundleHistogram.putFloat("_maxRR", maxRR);
		bundleHistogram.putFloat("_minRR", minRR);
		mHistogramPlotFg.setArguments(bundleHistogram);

		Bundle bundleHeartRate = new Bundle();
		bundleHeartRate.putFloatArray("_heartRR", heartRR);
		bundleHeartRate.putFloat("_minRR", minRR);
		mHeartRatePlotFg.setArguments(bundleHeartRate);

		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		position_one = dm.widthPixels/4;

		mViewPager = (ViewPager) findViewById(R.id.report_viewpager);

		List<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(mScatterPlotFg);
		fragments.add(mFastLowHRfg);
		fragments.add(mHistogramPlotFg);
		fragments.add(mHeartRatePlotFg);

		tabScatterPlot = (RelativeLayout)findViewById(R.id.tab_scatterplot_iv);
		tabFastLowHR = (RelativeLayout)findViewById(R.id.tab_fastlowhr_iv);
		tabHistogramPlot= (RelativeLayout)findViewById(R.id.tab_historamplot_iv);
		tabHeartRatePlot= (RelativeLayout)findViewById(R.id.tab_heartrateplot_iv);
		
		adapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
		mViewPager.setAdapter(adapter);
		mViewPager.setCurrentItem(0);
		setTabBackgroundColor("#4F94CD","#436EEE","#436EEE","#436EEE");
		mViewPager.setOffscreenPageLimit(4);

		tabScatterPlot.setOnClickListener(new TabOnClickListener(0));
		tabFastLowHR.setOnClickListener(new TabOnClickListener(1));
		tabHistogramPlot.setOnClickListener(new TabOnClickListener(2));
		tabHeartRatePlot.setOnClickListener(new TabOnClickListener(3));

		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	public void getHeartRR(float[] rPeak){

		heartRR = new float[rPeak.length - 1];
		//最大最小RR间期赋值为第一个RR间期值，避免比较时出现错误
		maxRR = rPeak[1] - rPeak[0];
		minRR = rPeak[1] - rPeak[0];

		for(int i = 0; i<rPeak.length-1;i++){
			heartRR[i] = rPeak[i+1]-rPeak[i];
			sumHeartRate = sumHeartRate+ECGApplication.Smaplerate*60/heartRR[i];
			if (heartRR[i] > maxRR)
			{
				maxRR = heartRR[i];
				maxRRPointX1 = rPeak[i];
				maxRRPointX2 = rPeak[i + 1];
			}
			if (heartRR[i] < minRR)
			{
				minRR = heartRR[i];
				minRRPointX1 = rPeak[i];
				minRRPointX2 = rPeak[i + 1];
			}

		}
	}
	public class TabOnClickListener implements View.OnClickListener {
		private int index = 0;

		public TabOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mViewPager.setCurrentItem(index);
		}
	};
	
	//设置Tab按键背景颜色变换
	public void setTabBackgroundColor(String tabScatterColor, String tabFastLowHRColor,
			String tabHistogramColor, String tabHeartRateColor){
		tabScatterPlot.setBackgroundColor(Color.parseColor(tabScatterColor));
		tabFastLowHR.setBackgroundColor(Color.parseColor(tabFastLowHRColor));
		tabHistogramPlot.setBackgroundColor(Color.parseColor(tabHistogramColor));
		tabHeartRatePlot.setBackgroundColor(Color.parseColor(tabHeartRateColor));
	}
	
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;

			//animation = new TranslateAnimation(position_one*currIndex, position_one * arg0, 0, 0);
			switch (arg0) {
			case 0:
				animation = new TranslateAnimation(position_one*currIndex, 0, 0, 0);
				setTabBackgroundColor("#4F94CD","#436EEE","#436EEE","#436EEE");
				break;
			case 1:
				animation = new TranslateAnimation(position_one*currIndex, position_one, 0, 0);
				setTabBackgroundColor("#436EEE","#4F94CD","#436EEE","#436EEE");
				break;
			case 2:
				animation = new TranslateAnimation(position_one*currIndex, position_one*2, 0, 0);
				setTabBackgroundColor("#436EEE","#436EEE","#4F94CD","#436EEE");
				break;
			case 3:
				animation = new TranslateAnimation(position_one*currIndex, position_one*3, 0, 0);
				setTabBackgroundColor("#436EEE","#436EEE","#436EEE","#4F94CD");
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
		}

		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

}
