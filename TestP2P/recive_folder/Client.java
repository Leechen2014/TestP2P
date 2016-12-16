package com.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
	static Socket server;

	public static void main(String[] args) throws Exception {

		server = new Socket("192.168.127.151", 5679);

		BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));

		PrintWriter out = new PrintWriter(server.getOutputStream());

		BufferedReader wt = new BufferedReader(new InputStreamReader(System.in));

		while (true) {

			String str = wt.readLine();

			out.println("192.168.127.151:"+str);

			out.flush();

			if (str.equals("end")) {
				break;
			}
			System.out.println(in.readLine());

		}
		server.close();

	}
	
	

}
