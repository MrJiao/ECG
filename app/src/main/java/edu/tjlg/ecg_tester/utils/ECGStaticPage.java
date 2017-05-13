package edu.tjlg.ecg_tester.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ECGStaticPage{
	public ECGStaticPage()
	{
		
	}
	public Bitmap createStaicECGPage( int width, int height ) 
	{ 
		
		int marginTop = height/4;
		Bitmap mBitmap = Bitmap.createBitmap( width, height, Config.ARGB_8888 );//创建一个新的和SRC长度宽度一样的位图 
		Canvas canvas = new Canvas( mBitmap ); 

		Paint paintBigGrid = new Paint();
		paintBigGrid.setColor(Color.parseColor("#FFDEAD"));   //颜色  
		paintBigGrid.setStrokeWidth((float) 3.0);

		Paint paintSmallGrid = new Paint();
		paintSmallGrid.setColor(Color.parseColor("#FFDEAD"));   //颜色  
		paintSmallGrid.setStrokeWidth((float) 1.0);

		//绘制心电图，大格子为50*50像素，小格子为10*10像素
		float startX = 0; 
		float startY = 0; 
		float stopX = 0;
		float stopY = 0;
		//绘制大格子横线
		for(int i = 0; i < 9; i++)
		{
			startY = 10 + 50*i+ marginTop;
			stopY = 10 + 50*i + marginTop;
			stopX = width;
			canvas.drawLine(startX, startY, stopX, stopY, paintBigGrid);
		}
		//绘制小格子横线
		for(int j = 0; j <8*5; j++)
		{
			startY = 10 + 10*j+ marginTop;
			stopY = 10 + 10*j+ marginTop;
			stopX = width;
			canvas.drawLine(startX, startY, stopX, stopY, paintSmallGrid);
		}
		//绘制大格子纵线
		for(int m = 0; m <width/50+1; m++)
		{
			startY = 10 + marginTop;
			stopY = 10 + 50*8+ marginTop;
			startX =  m*50;
			stopX =   m*50;
			canvas.drawLine(startX, startY, stopX, stopY, paintBigGrid);
		}

		for(int n = 0; n <width/10; n++)
		{
			startY = 10 + marginTop;
			stopY = 10 + 50*8+ marginTop;
			startX =  n*10;
			stopX =   n*10;
			canvas.drawLine(startX, startY, stopX, stopY, paintSmallGrid);
		}
		
		
		
		
		//save all clip 
		canvas.save( Canvas.ALL_SAVE_FLAG );//保存 
		//store 
		canvas.restore();//存储 
		return mBitmap; 
	} 
	
}
