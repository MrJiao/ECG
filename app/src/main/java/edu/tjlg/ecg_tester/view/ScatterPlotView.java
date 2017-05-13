package edu.tjlg.ecg_tester.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

public class ScatterPlotView extends View{

	private float viewWidth, viewHeight;
	private float marginWidth;
	private float marginHeight = 10;
	private float maxRR=-1;

	private float[] heartRR ;

	public ScatterPlotView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}


	public ScatterPlotView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}
	public ScatterPlotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}


	private Paint mPaint = new Paint();
	private Paint tPaint = new Paint();
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		int coordXString = 0;
		float coordX=40;
		float coordY = marginHeight+viewHeight;


		mPaint.setColor(Color.BLACK);
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(2);
		mPaint.setStyle(Paint.Style.FILL);
		//纵坐标
		canvas.drawLine(marginWidth, marginHeight-10, marginWidth, marginHeight+viewHeight,mPaint);
		//横坐标
		canvas.drawLine(marginWidth, marginHeight+viewHeight, marginWidth+viewWidth+10, marginHeight+viewHeight,mPaint);

		tPaint.setColor(Color.BLACK);
		tPaint.setAntiAlias(true);
		tPaint.setStrokeWidth(3);
		tPaint.setTextSize(15);

		canvas.drawText("(ms)", marginWidth, marginHeight, tPaint);
		canvas.drawText("(ms)",marginWidth+viewWidth+20, marginHeight+viewHeight+15, tPaint);
		//画出原点
		canvas.drawText(coordXString+"", coordX, coordY, tPaint);
		//根据最快心率大小，确定Y轴的最大值maxValueY
		int maxRRValue = 50;

		if (maxRR <= 50)
		{
			maxRRValue = 50;
		}
		else if (maxRR > 50 && maxRR <= 100)
		{
			maxRRValue = 100;
		}
		else if (maxRR > 100 && maxRR <= 150)
		{
			maxRRValue = 150;
		}
		else if (maxRR > 150 && maxRR <= 300)
		{
			maxRRValue = 300;
		}
		else if (maxRR > 300 && maxRR <= 500)
		{
			maxRRValue = 500;
		}
		else if (maxRR > 500 && maxRR <= 1000)
		{
			maxRRValue = 1000;
		}
		else if (maxRR > 1000 && maxRR <= 2000)
		{
			maxRRValue = 2000;
		}
		else if (maxRR > 2000 && maxRR <= 5000)
		{
			maxRRValue = 5000;
		}
		else if (maxRR > 5000 && maxRR <= 10000)
		{
			maxRRValue = 10000;
		}
		else if (maxRR > 10000 && maxRR <= 20000)
		{
			maxRRValue = 20000;
		}
		else if (maxRR > 20000 && maxRR <= 30000)
		{
			maxRRValue = 30000;
		}
		else if (maxRR > 30000 && maxRR <= 40000)
		{
			maxRRValue = 40000;
		}
		else if (maxRR > 40000 && maxRR <= 50000)
		{
			maxRRValue = 50000;
		}
		else
		{
			maxRRValue = 60000;
		}


		//画出心率X轴坐标标量

		for (int i = 1; i < 6; i++){
			coordX = marginWidth + i * (viewWidth / 5);
			coordXString = (maxRRValue / 5) * i;
			coordY = marginHeight + viewHeight;
			canvas.drawLine(coordX, coordY - 5, coordX, coordY,mPaint);
			canvas.drawText(coordXString+"", coordX -10, coordY+15,tPaint);
		}
		int coordYString = 0;
		//为画出Y轴坐标值的设置位置的变量,主要是解决数据为3位数字、4位数字和5位数字的显示长度
		int marginString = 20;
		if (maxRRValue < 1000)
		{
			marginString = 20;
		}
		else if (1000 < maxRRValue && maxRRValue < 10000)
		{
			marginString = 30;
		}
		else
		{
			marginString = 40;
		}
		//画出Y的坐标值

		for(int i=1; i<6;i++){
			coordX=marginWidth;
			coordY = viewHeight + marginHeight-viewHeight/5*i;
			coordYString = (maxRRValue/5)*i;

			canvas.drawLine(coordX, coordY, marginWidth+8, coordY,mPaint);
			canvas.drawText(coordYString+"", coordX-marginString, coordY+5, tPaint);
		}

		/*对Lorenz散点图（即R-R间期散点图）的赋值
		 *  横坐标：相邻的NN间期中前一个心搏的NN间期NNn
		 *  纵坐标：相邻的NN间期中后一个心搏的NN间期NNn+1*/

		//pHeartRR_DataX,pHeartRR_DataY分别表示点坐标所取的RR间期值变量
		float pHeartRR_DataX = 0;
		float pHeartRR_DataY = 0;
		float pointX = 0;
		float pointY = 0;
		if(heartRR!=null)
			for (int i = 0; i < heartRR.length - 1; i++){

				//Lorenz散点图坐标数据乘以4目的是与坐标的单位统一
				//心电数据测得数据的频率是250Hz,可得单位时间为4ms，所以乘以4相当于放大4倍，单位距离表示为1ms
				//MIT心电数据测得数据的频率是360Hz,可得单位时间为(float)1 / (float)360ms
				pHeartRR_DataX = heartRR[i] * 4;
				pHeartRR_DataY = heartRR[i + 1] * 4;

				pointX= marginWidth + (pHeartRR_DataX / maxRRValue) * (viewWidth);
				pointY = marginHeight + viewHeight - (pHeartRR_DataY / maxRRValue) * (viewHeight);

				canvas.drawCircle(pointX, pointY ,2, mPaint);
				//canvas.drawRect(pointX, pointY, pointX+5, pointY+5, mPaint);
			}

	}
	public void setLinePoint(float[] heartRR,float widthPixels,
			float heightPixels, float marginWidthPixels, float maxRR){
		this.heartRR = heartRR;
		this.viewWidth = widthPixels;
		this.viewHeight = heightPixels;
		this.marginWidth = marginWidthPixels;
		//maxRR*4目的是与坐标的单位统一
		//心电数据测得数据的频率是250Hz,可得单位时间为4ms，所以乘以4相当于放大4倍，单位距离表示为1ms
		//MIT心电数据测得数据的频率是360Hz,可得单位时间为(float)1 / (float)360ms
		this.maxRR = maxRR*4;
		invalidate();
	}
}
