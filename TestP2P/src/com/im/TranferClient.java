package com.im;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import com.im.util.IConstant;
/**
 * @author lee
 * public boolean connect(String ip,int port)
 * 重写新的方法, 使得程序可以自定义端口号
 * 
 */
public class TranferClient
{
	private ISocketOberser iSocketOberser;
	private IFileTransObserver fileTranObserver;
	private boolean isConn = false;
	
	private SocketChannel socketChannel;
	private FileSendTask fileSendTask;
	private FileReciveTask fileReciveTask;
	
	public TranferClient(ISocketOberser iSocketOberser, IFileTransObserver fileTranObserver)
    {
	    super();
	    this.iSocketOberser = iSocketOberser;
	    this.fileTranObserver = fileTranObserver;
    }
	
	public boolean connect(String ip)
	{
		try
		{
		 	socketChannel = SocketChannel.open();
	        socketChannel.configureBlocking(true);
	        isConn = socketChannel.connect(new InetSocketAddress(ip,IConstant.PORT_SERVER_DEFAULT));
	        fileSendTask = new FileSendTask(fileTranObserver, socketChannel);
	        fileSendTask.start();
	        fileReciveTask = new FileReciveTask(fileTranObserver, socketChannel);
	        fileReciveTask.start();
	        iSocketOberser.notifyDescript("connect to " + ip + " success.");
	        iSocketOberser.notifyConn(true);
        } catch (IOException e)
        {
	        e.printStackTrace();
	        isConn = false;
	        iSocketOberser.notifyDescript("connect fail: " + e.getMessage());
        }
		
		return isConn;
	}
	
	/**
	 * 重写连接方法
	 * by lee
	 * @param ip
	 * @param port
	 * @return
	 */
	public boolean connect(String ip,int port)
	{
		try
		{
		 	socketChannel = SocketChannel.open();
	        socketChannel.configureBlocking(true);
	        isConn = socketChannel.connect(new InetSocketAddress(ip,port));
	        fileSendTask = new FileSendTask(fileTranObserver, socketChannel);
	        fileSendTask.start();
	        fileReciveTask = new FileReciveTask(fileTranObserver, socketChannel);
	        fileReciveTask.start();
	        iSocketOberser.notifyDescript("connect to " + ip + " success.");
	        iSocketOberser.notifyConn(true);
        } catch (IOException e)
        {
	        e.printStackTrace();
	        isConn = false;
	        iSocketOberser.notifyDescript("connect fail: " + e.getMessage());
        }
		
		return isConn;
	}

	public void shutDown()
	{
		iSocketOberser.notifyDescript("begin close client connect.");
		if(null != socketChannel)
		{
			try
            {
	            socketChannel.close();
            } catch (IOException e)
            {
	            e.printStackTrace();
            }
		}
		if(null != fileSendTask)
		{
			fileSendTask.shutDown();
		}
		if(null != fileReciveTask)
		{
			fileReciveTask.shutDown();
		}
		iSocketOberser.notifyDescript("end close client connect.");
	}
}
