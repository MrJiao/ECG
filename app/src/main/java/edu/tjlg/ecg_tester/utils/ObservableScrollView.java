package edu.tjlg.ecg_tester.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

public class ObservableScrollView extends HorizontalScrollView{

	private ScrollViewListener scrollViewListener = null;  
	public ObservableScrollView(Context context) {  
		super(context);  
	}  

	public ObservableScrollView(Context context, AttributeSet attrs,  
			int defStyle) {  
		super(context, attrs, defStyle);  
	}  

	public ObservableScrollView(Context context, AttributeSet attrs) {  
		super(context, attrs);  
	}  

	public void setScrollViewListener(ScrollViewListener scrollViewListener) {  
		this.scrollViewListener = scrollViewListener;  
	}  

	@Override
	public void scrollTo(int x, int y) {
		// TODO Auto-generated method stub
		super.scrollTo(x, y);
	}

	@Override  
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {  
		super.onScrollChanged(x, y, oldx, oldy);  
		if (scrollViewListener != null) {  
			scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);  
			Log.i("", x+","+ y+"  ----  " +oldx+","+oldy);
		}  
	}  
}
