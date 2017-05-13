package edu.tjlg.ecg_tester.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper  extends SQLiteOpenHelper {

	private static String name = "ECGlist.db";// 表示数据库的名称
	private static int version = 2;// 表示数据库的版本号码

	public DbOpenHelper(Context context) {
		super(context, name, null, version);
	}

	// 当数据库创建的时候，是第一次被执行,完成对数据库的表的创建
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//支持的数据类型：整型数据，字符串类型，日期类型，二进制的数据类型，
		String sql1 = "create table userlist(id integer primary key autoincrement," +
				"name text,gender text,age text,phoneNum text,illness text)";
		db.execSQL(sql1);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}

