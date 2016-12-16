package com.im;
/**
 * @author lee
 * 
 */
public interface ISocketOberser
{
	void notifyConn(boolean isConnect);
	void notifyDescript(String info);
}
