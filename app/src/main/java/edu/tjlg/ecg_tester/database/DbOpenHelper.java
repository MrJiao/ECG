package edu.tjlg.ecg_tester.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper  extends SQLiteOpenHelper {

	private static String name = "ECGlist.db";// ��ʾ���ݿ������
	private static int version = 2;// ��ʾ���ݿ�İ汾����

	public DbOpenHelper(Context context) {
		super(context, name, null, version);
	}

	// �����ݿⴴ����ʱ���ǵ�һ�α�ִ��,��ɶ����ݿ�ı�Ĵ���
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//֧�ֵ��������ͣ��������ݣ��ַ������ͣ��������ͣ������Ƶ��������ͣ�
		String sql1 = "create table userlist(id integer primary key autoincrement," +
				"name text,gender text,age text,phoneNum text,illness text)";
		db.execSQL(sql1);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}

