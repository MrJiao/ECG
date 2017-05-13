package edu.tjlg.ecg_tester.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.Toast;

public class ECGWaveImageView extends ImageView {
	private Context mContext;
	private Bitmap bitmap,bmp;
	private Canvas canvas;
	private Paint paint;
	private String bitName;
	// 心电数据
	// private float[] data ;
	// private float[] rPeak;
	private int bWidth, bHeight;
	private float[] pts;

	public ECGWaveImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

	}

	public ECGWaveImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ECGWaveImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void drawWave(float[] data, float[] rPeak) {

		System.out.println("创建画布-------------");
		// 创建画布
		bitmap = Bitmap.createBitmap(bWidth, bHeight, Bitmap.Config.ARGB_8888);

		canvas = new Canvas(bitmap);
		paint = new Paint();
		paint.setColor(Color.BLACK); // 颜色
		paint.setStrokeWidth((float) 3.0);
		if (data != null)
			if (data.length > 1) {
				pts = new float[data.length * 2];
				for (int i = 0; i < data.length * 2; i += 2) {
					pts[i] = i / 2;
					pts[i + 1] = bHeight - data[i / 2];
				}

				canvas.drawLines(pts, paint);
				canvas.drawLines(pts, 2, pts.length - 2, paint);
				//有选择绘制多条直线（pts：绘制直线的端点数组，每条直线占用4个数据。
                //offset：跳过的数据个数，这些数据将不参与绘制过程。
                //count：实际参与绘制的数据个数。
				//paint：绘制直线所使用的画笔。）
			} else {
				Toast.makeText(mContext, "没有数据！", Toast.LENGTH_LONG).show();
			}

		drawRPeakPoint(data, rPeak, canvas);
		canvas.save(Canvas.ALL_SAVE_FLAG);

		setImageBitmap(bitmap);

		// if(bitmap!=null && !bitmap.isRecycled())
		// bitmap.recycle();
	}
 public void xie(float[] data,String bitName){

	 bitmap = Bitmap.createBitmap(bHeight,bWidth, Bitmap.Config.RGB_565);
	 System.out.println("创建画布-------------"+bitName);
		canvas = new Canvas(bitmap);
	    canvas.drawColor(Color.WHITE);//白色背景
		paint = new Paint();
		paint.setColor(Color.BLACK); // 颜色
		paint.setStrokeWidth((float) 3.0);//线宽
		if (data != null)
			if (data.length > 1) {
				pts = new float[data.length * 2];
				for (int i = 0; i < data.length * 2; i += 2) {
					pts[i] = bHeight - data[i / 2];
					pts[i + 1] =i / 2 ;
				}

				canvas.drawLines(pts, paint);
				canvas.drawLines(pts, 2, pts.length - 2, paint);
			} else {
				Toast.makeText(mContext, "没有数据！", Toast.LENGTH_LONG).show();
			}
		canvas.save(Canvas.ALL_SAVE_FLAG);
		
	

		File f = new File("/sdcard/DCIM/"+bitName+".png");
		
        
        
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	 Matrix matrix = new Matrix();
	 matrix.postScale(-1, 1); // 镜像水平翻转
	 Bitmap convertBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getScaledWidth(canvas), bitmap.getScaledHeight(canvas), matrix, true);
	 convertBmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
	 bitmap.recycle();

		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	 
 }
 
	public void recycleBitmap() {
		System.out.println("调用-------------");
		if (bitmap != null && !bitmap.isRecycled()) {
			System.out.println("-----执行--------");
			bitmap.recycle();
		}
	}

	public void drawRPeakPoint(float[] data, float[] rPeak, Canvas canvas) {
		Paint paintRPeak = new Paint();
		paintRPeak.setColor(Color.GREEN); // 颜色
		paintRPeak.setStrokeWidth((float) 10.0);

		if (rPeak != null && rPeak.length > 0) {

			// for (int j = 0; j < data.length*2; j+=2){
			// int index = j/2;
			// if (IsRPeak(index) == true){
			// PointF rpeak = new PointF();
			// rpeak.x = pts[index];
			// rpeak.y = pts[index+1];
			// Log.i("---", "----"+rpeak.y);
			// canvas.drawPoint(rpeak.x, rpeak.y, paintRPeak);
			// }
			// }
			float[] pts = new float[rPeak.length * 2];
			for (int i = 0; i < rPeak.length * 2; i += 2) {
				pts[i] = rPeak[i / 2];
				pts[i + 1] = bHeight - data[(int) rPeak[i / 2]];
			}
			canvas.drawPoints(pts, paintRPeak);
		}
	}

	// @Override
	// protected void onDraw(Canvas canvas) {
	// // TODO Auto-generated method stub
	// super.onDraw(canvas);
	// Paint paint = new Paint();
	// paint.setColor(Color.BLACK); //颜色
	// paint.setStyle(Paint.Style.STROKE);
	// paint.setStrokeWidth(5);
	//
	// if(data.length>1){
	// float[] pts = new float[data.length*2];
	// for(int i= 0 ;i<data.length*2 ; i+=2){
	// pts[i] = i;
	// pts[i+1] = data[i/2];
	// }
	//
	// canvas.drawLines(pts, paint);
	//
	// }else{
	// Toast.makeText(mContext, "没有数据！", Toast.LENGTH_LONG).show();
	// }
	// }
	/*
	 * public void setWaveData(float[] data, float[] rPeak){ this.data = data;
	 * this.rPeak = rPeak; }
	 */
	public void setBitmapHeightWidth(int width, int height) {
		this.bWidth = width;
		this.bHeight = height;

	}
}
