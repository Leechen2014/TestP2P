package com.im.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.im.FilesMgr;
import com.im.IFileTransObserver;
import com.im.ISocketOberser;
import com.im.TranferClient;
import com.im.TranferServer;
import com.im.util.IConstant;
import com.im.util.Utils;
import com.jtattoo.plaf.mcwin.McWinLookAndFeel;

/**
 * @author lee
 */
public class EasyTransferMainFrame extends JFrame implements ActionListener, ISocketOberser, IFileTransObserver, ITextAreaTask
{
	private static final long serialVersionUID = 3536190321813316730L;
	private int iwidth;
	private int iheight;
	private String title;
	
	private JCheckBox checkBoxServer;
	private JCheckBox checkBoxClient;
	private JTextField ipField;
	private JButton connBtn;
	private JTextPaneEx areaTrace;
	private JProgressBar progressBar;
	private JPanel topCenterPanel;
	private JTextArea inputArea;
	private JButton sendButton;
	
	private JTextField portFild ;
	
	
	private TranferServer server;
	private TranferClient client;

	public EasyTransferMainFrame()
	{
		iwidth = 500;
		iheight = 500;
		title = "P2P test v0.1";

		this.setSize(iwidth, iheight);
		this.setTitle(title);
		this.setResizable(false);
		this.setLocation(200, 200);
		
		
		
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void initView()
	{
		checkBoxServer = new JCheckBox(" 服务器  ");
		checkBoxClient = new JCheckBox(" 客户机 ");
		ipField = new JTextField(11);
		connBtn = new JButton("      链接      ");
		areaTrace = new TextAreaByMenu(this);
		progressBar = new JProgressBar();
		inputArea = new JTextArea();
		sendButton = new JButton("发送");
		
		this.setLayout(new BorderLayout());
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		{
			JPanel toptopPanel = new JPanel();
			toptopPanel.setBorder(BorderFactory.createTitledBorder("角色"));
			toptopPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			toptopPanel.add(checkBoxServer);
			toptopPanel.add(checkBoxClient);
			topPanel.add(toptopPanel, BorderLayout.NORTH);
		}
		{
			FlowLayout topCenterLayout = new FlowLayout(FlowLayout.LEFT);
			topCenterPanel = new JPanel(topCenterLayout);
			
			topCenterPanel.add(new JLabel("   IP 地址  "), BorderLayout.WEST);
			topCenterPanel.add(ipField, BorderLayout.CENTER);
			
			topCenterPanel.add(new JLabel(" 端口号 "), BorderLayout.WEST);
			portFild = new JTextField(6);
			portFild.setText(IConstant.PORT_SERVER_DEFAULT+"");
			
			topCenterPanel.add(portFild, BorderLayout.CENTER);
			
			topCenterPanel.add(connBtn, BorderLayout.EAST);
			topPanel.add(topCenterPanel, BorderLayout.CENTER);
		}
		
		this.add(topPanel, BorderLayout.NORTH);
		this.add(new JScrollPane(areaTrace), BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel(new GridLayout(2,1));
		bottomPanel.add(progressBar);
		JPanel bottomSouthPanel = new JPanel(new BorderLayout());
		bottomSouthPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
		bottomSouthPanel.add(sendButton, BorderLayout.EAST);
		bottomPanel.add(bottomSouthPanel, BorderLayout.SOUTH);
		this.add(bottomPanel, BorderLayout.SOUTH);
		
		
		checkBoxServer.setPreferredSize(new Dimension(100, 30));
		checkBoxClient.setPreferredSize(new Dimension(100, 30));
		topCenterPanel.setPreferredSize(new Dimension(WIDTH, 30));
		progressBar.setMaximum(100);
		progressBar.setForeground(new Color(0x0066FF));
		progressBar.setPreferredSize(new Dimension(WIDTH, 30));
		drag(areaTrace);
		
		connBtn.addActionListener(this);
		checkBoxServer.addActionListener(this);
		checkBoxClient.addActionListener(this);
		actionPerformed(new ActionEvent(checkBoxServer, 1, null));
		ipField.setText("127.0.0.1");
		sendButton.addActionListener(this);
		inputArea.setEnabled(false);
		sendButton.setEnabled(false);
	}
	
	private void printTrace(String info)
	{
		areaTrace.append(info, Color.blue);
		areaTrace.append(System.getProperty("line.separator"), Color.blue);
		scrollToBottom(areaTrace);
	}
	
	/***
	 *  拖动事件 
	 * @param panel
	 */
	public void drag(Component panel)
    {
        new DropTarget(panel, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter()
        {
            @SuppressWarnings("unchecked")
            @Override
            public void drop(DropTargetDropEvent dtde)
            {
                try
                {
                    if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
                    {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                        List<File> list =  (List<File>) (dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
                        
                        
                        /**
                         * 加入队列中
                         */
                    	if (FilesMgr.getInstance().pushFiles(list))
				        {
                    		printTrace("file size : " + list.size());
				        }else
				        {
				        	printTrace("file not trans finished, please wait");
				        }
                        
                        dtde.dropComplete(true);
                    }
                    else
                    {
                        dtde.rejectDrop();//
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		/**
		 * 点击连接之后响应的事件
		 */
		if(e.getSource() == connBtn)
		{
			String ip = ipField.getText().trim(); //去处空格
			String port = portFild.getText().trim();
			
			if(null == ip || ip.isEmpty())
			{
				printTrace("非法的ip.");
				return;
			}
			if(null == port || port.isEmpty())
			{
				printTrace("非法的端口号.");
				return;
			}
			
			int iport = Integer.parseInt(port);
			
			
			
			client = new TranferClient(this, this);
//			if(client.connect(ip))
//			{
//				connBtn.setEnabled(false);
//			}
			if(client.connect(ip, iport))
			{
				connBtn.setEnabled(false);
			}
			
		}else if(e.getSource() == checkBoxServer)
		{
			checkBoxClient.setSelected(!checkBoxServer.isSelected());
			serverStartOrDown(checkBoxServer.isSelected());
			clientStartOrDown(checkBoxClient.isSelected());
			
			
		}else if(e.getSource() == checkBoxClient)
		{
			checkBoxServer.setSelected(!checkBoxClient.isSelected());
			serverStartOrDown(checkBoxServer.isSelected());
			clientStartOrDown(checkBoxClient.isSelected());
		}else if(e.getSource() == sendButton)
		{
			String input = inputArea.getText();
			if(input != null && !input.trim().isEmpty())
			{
				List<File> msgList = new ArrayList<File>();
				File msgFile = new File(IConstant.MSG_FILE_IDENTIFIED + input);
				msgList.add(msgFile);
			// push file 
				FilesMgr.getInstance().pushFiles(msgList);
			}
		}
	}

	private void clientStartOrDown(boolean selected)
    {
		ipField.setEnabled(selected);
		connBtn.setEnabled(selected);
		if(selected)
		{
			if(null != client)
			{
				client.shutDown();
			}
			topCenterPanel.setVisible(true);
		}
    }

	private void serverStartOrDown(boolean selected)
    {
		if(null != server)
		{
			server.shutdown();
		}
		
		if(selected)
		{
			String strPort = this.portFild.getText();
			int port =Integer.parseInt(strPort);
			
			server = new TranferServer(this, this,port);
			server.start();
			topCenterPanel.setVisible(false);
		}
    }

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		Properties props = new Properties();
		props.put("logoString", "test");
		props.put("licenseKey", "test");
		McWinLookAndFeel.setCurrentTheme(props);
		UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
		EasyTransferMainFrame easyImMainFrame = new EasyTransferMainFrame();
		// 初始化视图
		easyImMainFrame.initView();
		// 添加菜单
		//easyImMainFrame.initMenu();
		//  变为可见的
		easyImMainFrame.setVisible(true);
	}

	@Override
    public void notifyConn(boolean isConnect)
    {
		printTrace("notify conn : " + isConnect);
		sendButton.setEnabled(isConnect);
		inputArea.setEnabled(isConnect);
    }

	@Override
    public void notifyDescript(String info)
    {
		printTrace(info);
    }

	@Override
    public void notifyProgress(int iTotal, int iIndex, double currProgress)
    {
		this.setTitle(title + iIndex + "/" + iTotal);
		progressBar.setValue((int)currProgress);
    }

	@Override
    public void notifyNextFile(String fileName)
    {
		printTrace("begin transf file: " + fileName);
    }
	
	@Override
    public void notifyException(String info)
    {
		printTrace("Have exception: " + info);
    }

	@Override
    public void notifyFinished(String info)
    {
	    JOptionPane.showMessageDialog(null, "translate completed " + info);
    }

	@Override
    public void transferFile(String basePath)
    {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(JFileChooser.APPROVE_OPTION ==chooser.showDialog(null, "Select"))
		{
			File file = chooser.getSelectedFile();
			File[] files = file.listFiles();
			List<File> targetFile = new ArrayList<File>();
			for (int i = 0; i < files.length; i++)
            {
	            if(files[i].isFile())
	            {
	            	targetFile.add(files[i]);
	            }
            }
			boolean isPushed = FilesMgr.getInstance().pushFiles(targetFile);
			if(isPushed)
			{
				printTrace("will transfer file, file size: " + targetFile.size());
			}else
			{
				printTrace("task not done, please wait minutes.");
			}
		}
    }

	@Override
    public void notifyMsg(String msg, boolean isRecive)
    {
		String displayMsg = packingMsg(msg, isRecive);
		if(isRecive)
		{
			areaTrace.append(displayMsg, Color.red);
		}else
		{
			inputArea.setText("");
			areaTrace.append(displayMsg, Color.green);
		}
		scrollToBottom(areaTrace);
    }
	
	private void scrollToBottom(JTextPaneEx area)
    {
		area.selectAll();
		area.setCaretPosition(areaTrace.getSelectionEnd());
    }

	private String packingMsg(String msg, boolean isRecive)
	{
		StringBuilder buff = new StringBuilder();
		buff.append(isRecive ? "from ":"send ");
		buff.append(Utils.getDate());
		buff.append(IConstant.SEPARATOR_LINE);
		buff.append(msg);
		buff.append(IConstant.SEPARATOR_LINE);
		return buff.toString();
	}
	
	/**
	 * 初始化 菜单
	 */
//	private void initMenu()
//	{
//		JMenuBar bar = new JMenuBar();
//		
//		JMenu menu = new JMenu("文   件");
//		JMenuItem loginItem = new JMenuItem("登  录(L)");
//		JMenuItem queryItem = new JMenuItem("查  询（Q）");
//		menu.add(loginItem);
//		menu.add(queryItem);
//		bar.add(menu);
//		this.setJMenuBar(bar);
//	}
	
	
	
}