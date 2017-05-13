package edu.tjlg.ecg_tester.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class HeartRatePlotView extends View{

	private float viewWidth, viewHeight;
	private float marginWidth;
	private float marginHeight = 20;

	private Paint mPaint = new Paint();
	private Paint tPaint = new Paint();
	private float[] heartRatePos;
	private int maxValueY;
	public HeartRatePlotView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public HeartRatePlotView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public HeartRatePlotView(Context context, AttributeSet attrs) {
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

		tPaint.setColor(Color.BLACK);
		tPaint.setAntiAlias(true);
		tPaint.setStrokeWidth(3);
		tPaint.setTextSize(15);
		//纵坐标
		canvas.drawLine(marginWidth, marginHeight-10, marginWidth, marginHeight+viewHeight,mPaint);
		//横坐标
		canvas.drawLine(marginWidth, marginHeight+viewHeight, marginWidth+viewWidth+10, marginHeight+viewHeight,mPaint);

		float coordX=40;
		float coordY = marginHeight+viewHeight;
		int coordYString = 0;
		//画出原点
		canvas.drawText(coordYString+"", coordX, coordY, tPaint);
		//画出Y的坐标值
		for(int i=1; i<6;i++){
			coordX=marginWidth;
			coordY = viewHeight + marginHeight-viewHeight/5*i;
			coordYString = (maxValueY/5)*i;

			canvas.drawLine(coordX, coordY, marginWidth+8, coordY,mPaint);
			canvas.drawText(coordYString+"", coordX-30, coordY+5, tPaint);
		}
		if(heartRatePos!=null)
			canvas.drawLines(heartRatePos, mPaint);
	}

	public void setLinePoint(float[] heartRatePos, int maxValueY,float widthPixels,
			float heightPixels, float marginWidthPixels){
		this.heartRatePos = heartRatePos;
		this.maxValueY = maxValueY;
		this.viewWidth = widthPixels;
		this.viewHeight = heightPixels;
		this.marginWidth = marginWidthPixels;
	}
}
