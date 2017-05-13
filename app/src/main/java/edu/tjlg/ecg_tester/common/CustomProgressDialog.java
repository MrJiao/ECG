package edu.tjlg.ecg_tester.common;

import edu.tjlg.ecg_tester.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomProgressDialog extends Dialog {

	private Context context = null;
	private static CustomProgressDialog customProgressDialog = null;

	public CustomProgressDialog(Context context){
		super(context);
		this.context = context;
	}
	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
	}
	public static CustomProgressDialog createDialog(Context context){
		customProgressDialog = new CustomProgressDialog(context,R.style.CustomProgressDialog);
		customProgressDialog.setContentView(R.layout.custom_progress_dialog);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		return customProgressDialog;
	}
	public void onWindowFocusChanged(boolean hasFocus){
		if (customProgressDialog == null){
			return;
		}
		ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loading_image);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
		animationDrawable.start();
	}
	public CustomProgressDialog setTitile(String strTitle){
		return customProgressDialog;
	}
	public CustomProgressDialog setMessage(String strMessage){
		TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.loading_msg);
		tvMsg.setText("������");
		if (tvMsg != null){
			tvMsg.setText(strMessage);
		}
		return customProgressDialog;
	}
}

