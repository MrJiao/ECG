package edu.tjlg.ecg_tester.database;

import java.util.Map;

import android.content.ContentValues;

public interface DbService {
public boolean createList(ContentValues values);
	
	public boolean addList(ContentValues values,String listname);
	
	public boolean deleteList(String whereClause, String[] whereArgs,String listname);

	public boolean updateList(ContentValues values, String whereClause,
			String[] whereArgs,String listname);
	
	public Map<String, String> viewList(String selection,
			String[] selectionArgs,String listname);

}

