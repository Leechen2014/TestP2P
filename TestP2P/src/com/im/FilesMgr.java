package com.im;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * @author lee
 * 通过栈的方式管理文件
 * 
 */
public class FilesMgr
{
	private static FilesMgr transferFile = new FilesMgr();
	
	public static FilesMgr getInstance()
	{
		return transferFile;
	}
	
	private List<File> targetFile = new ArrayList<File>();
	
	private Object fileLock = new Object();

	public boolean pushFiles(List<File> file)
	{
		synchronized (fileLock)
        {
			if(targetFile.isEmpty())
			{
				targetFile.addAll(file);
				return true;
			}
        }
		return false;
	}
	
	public List<File> getFiles()
	{
		return targetFile;
	}
	
}
