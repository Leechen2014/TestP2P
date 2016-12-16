package com.im;
/**
 * @author lee
 * 
 */
public interface IFileTransObserver
{
	void notifyProgress(int iTotal, int iIndex, double currProgress);
	void notifyNextFile(String fileName);
	void notifyException(String info);
	void notifyFinished(String info);
	void notifyMsg(String msg, boolean isRecive);
}
