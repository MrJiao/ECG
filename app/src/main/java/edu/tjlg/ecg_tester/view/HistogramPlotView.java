package edu.tjlg.ecg_tester.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class HistogramPlotView extends View{

	private float viewWidth, viewHeight;
	private float marginWidth;
	private float marginHeight = 10;
	private int[] heightHR;
	private float lowHR = -1;
	private float intervalHR = -1;
	private int maxHisHRpos = -1;

	private Paint mPaint = new Paint();
	private Paint tPaint = new Paint();
	private Paint fPaint = new Paint();

	public HistogramPlotView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public HistogramPlotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public HistogramPlotView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		mPaint.setColor(Color.BLACK);
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(2);
		mPaint.setStyle(Paint.Style.STROKE);

		tPaint.setColor(Color.BLACK);
		tPaint.setAntiAlias(true);
		tPaint.setStrokeWidth(3);
		tPaint.setTextSize(15);

		fPaint.setColor(Color.GRAY);
		fPaint.setAntiAlias(true);
		fPaint.setStrokeWidth(5);
		fPaint.setStyle(Paint.Style.FILL);

		//纵坐标
		canvas.drawLine(marginWidth, marginHeight-10, marginWidth, marginHeight+viewHeight,mPaint);
		//横坐标
		canvas.drawLine(marginWidth, marginHeight+viewHeight, marginWidth+viewWidth+10, marginHeight+viewHeight,mPaint);

		int coordXString = 0;
		int coordYString = 0;
		float coordX = 40;
		float coordY = viewHeight + marginHeight;
		//画出原点
		canvas.drawText(coordYString+"", coordX, coordY, tPaint);
		//画出Y轴坐标标量
		for(int i=1; i<6;i++){
			coordX=marginWidth;
			coordY = viewHeight + marginHeight-viewHeight/5*i;
			coordYString = (maxHisHRpos/5)*i;
			canvas.drawLine(coordX, coordY, marginWidth+8, coordY,mPaint);
			canvas.drawText(coordYString+"", coordX-30, coordY+5, tPaint);
		}


		for(int i = 0; i<21; i++){
			coordX = marginWidth + i*30;
			coordXString = (int)lowHR + i*(int)intervalHR;
			coordY = viewHeight + marginHeight;

			//画出X轴坐标标量
			canvas.drawText(String.valueOf(coordXString), coordX-10, coordY+20, tPaint);
			if(i<20){
				//画出直方图
				canvas.drawRect(coordX, coordY-heightHR[i], coordX+30-1, coordY, fPaint);
				canvas.drawRect(coordX, coordY-heightHR[i], coordX+30-1, coordY, mPaint);
			}
		}
	}
	public void setLinePoint(int[] heightHR,float widthPixels,float heightPixels,
			float marginWidthPixels, float intervalRR, float lowHR, int maxHisHRpos){
		this.heightHR = heightHR;
		this.viewWidth = widthPixels;
		this.viewHeight = heightPixels;
		this.marginWidth = marginWidthPixels;
		this.intervalHR = intervalRR;
		this.lowHR = lowHR;
		this.maxHisHRpos = maxHisHRpos;
		invalidate();
	}
}
