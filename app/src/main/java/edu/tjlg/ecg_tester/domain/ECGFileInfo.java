package edu.tjlg.ecg_tester.domain;

public class ECGFileInfo {

	private String fileName;
	private String userName;
	private String filePath;
	private String createFileTime;
	private long lastModifiedTime;

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getCreateFileTime() {
		return createFileTime;
	}
	public void setCreateFileTime(String createTime) {
		this.createFileTime = createTime;
	}
	public long getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
}
