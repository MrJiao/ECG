package edu.tjlg.ecg_tester.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class FastLowPlotView extends View{

	private Paint mPaint = new Paint();
	private float[] maxHrWavePos, minHrWavePos;
	public FastLowPlotView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public FastLowPlotView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public FastLowPlotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		mPaint.setColor(Color.BLACK);
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(2);
		if(maxHrWavePos!=null)
			canvas.drawLines(maxHrWavePos, mPaint);
		if(minHrWavePos!=null)
			canvas.drawLines(minHrWavePos, mPaint);

	}

	public void setLinePoint(float[] maxHrWavePos, float[] minHrWavePos){
		this.maxHrWavePos = maxHrWavePos;
		this.minHrWavePos = minHrWavePos;
	}
}
