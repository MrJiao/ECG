package edu.tjlg.ecg_tester.common;

import java.util.Comparator;

import edu.tjlg.ecg_tester.domain.ECGFileInfo;

public class SortComparator implements Comparator<ECGFileInfo> {  

	public int compare(ECGFileInfo ecgf1, ECGFileInfo ecgf2) {  
		if(ecgf1.getLastModifiedTime() > ecgf2.getLastModifiedTime()){  
			return -1;  
		} else if(ecgf1.getLastModifiedTime() < ecgf2.getLastModifiedTime()) {  
			return 1;  
		}  
		return 0;  
	}  
}
