package edu.tjlg.ecg_tester.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class CheckBoxClickListener implements OnClickListener{

	private Context mContext;
	private String[] areas;
	private boolean[] areaState;
	private ListView areaCheckListView;

	public CheckBoxClickListener(Context context, String[] areas,
			boolean[] areaState, ListView areaCheckListView){
		this.mContext = context;
		this.areas = areas;
		this.areaState = areaState;
		this.areaCheckListView = areaCheckListView;
	}

	@Override
	public void onClick(View v) {
		AlertDialog ad = new AlertDialog.Builder(mContext)
		.setTitle("ѡ������")
		.setMultiChoiceItems(areas,areaState,new DialogInterface.OnMultiChoiceClickListener(){
			public void onClick(DialogInterface dialog,int whichButton, boolean isChecked){
				//���ĳ������
			}
		}).setPositiveButton("ȷ��",new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog,int whichButton){
				String s = "��ѡ����:";
				for (int i = 0; i < areas.length; i++){
					if (areaCheckListView.getCheckedItemPositions().get(i)){
						s += i + ":"+ areaCheckListView.getAdapter().getItem(i)+ "  ";
					}else{
						areaCheckListView.getCheckedItemPositions().get(i,false);
					}
				}
				if (areaCheckListView.getCheckedItemPositions().size() > 0){
					Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
				}else{
					//û��ѡ��
				}
				dialog.dismiss();
			}
		}).setNegativeButton("ȡ��", null).create();
		areaCheckListView = ad.getListView();
		ad.show();
	}
}
