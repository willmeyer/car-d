package com.willmeyer.card;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import com.willmeyer.card.exception.*;

public class ControlServer {
	private ServerThread server = null;
	private int port = 0;
	private AttributeManager attrMgr = null;
	
	public ControlServer(int port) {
		this.port = port;
	}
	
	public void start(AttributeManager attrMgr) throws Exception {
		assert attrMgr != null;
		this.attrMgr = attrMgr;
		this.server = new ServerThread(port);
		this.server.start();
	}

	public void stop() {
		server.shutdown();
	}

	private class ServerThread extends Thread {
	
		private ServerSocket m_sock = null;
		private boolean m_accept = true;
		
		public ServerThread(int port) throws Exception {
			assert port > 0;
			System.out.print("Starting telnet server on port " + port + "...");
			m_sock = new ServerSocket();
			SocketAddress addr = new InetSocketAddress(port);
			m_sock.bind(addr);
			System.out.println("ok.");
		}
		
		public void run() {
			try {
				while (m_accept) {
					Socket sock = m_sock.accept();
					new ConnectionHandler(sock);
				}
			} catch (Exception e) {
				//System.out.println("ERROR accepting socket: " + e.getMessage());
			}

		}
		
		public void shutdown() {
			m_accept = false;
			try {
				m_sock.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private final class ConnectionHandler extends Thread {
		
		private Socket m_sock = null;
		
		public ConnectionHandler(Socket sock) {
			m_sock = sock;
			this.start();
		}
		
		/**
		 * @return boolean true if processing should continue, or false to quit
		 */
		public boolean processControlCommandInputLine(PrintStream out, String line) 
		{
			line = line.trim ();
			int idx = line.indexOf (" ");
			String cmdName = null;
			String params[] = null;
			if (idx < 0) {
				cmdName = line.toLowerCase();
				params = new String[0];
			} else {
				cmdName = line.substring(0, idx);
				line = line.substring (idx + 1);
				if ((params = line.split(" ")) == null)
					params = new String[0]; 	
			}
			return handleControlCommand(cmdName, params, out);
		}
		
		public boolean handleControlCommand(String cmdName, String[] params, PrintStream out) {
			assert cmdName != null;
			assert params != null;
			assert out != null;
			boolean keepProcessing = true;
			int responseCode = StatusCodes.ERR_ATTR_UNKNOWN;
			String responseDetail = "Unknown error";
			
			try {
				if (cmdName.equalsIgnoreCase("attr")) {
					String attrName = params[0];
					if (params.length > 1) {
	
						// This is a set
						String val = params[1];
						attrMgr.setAttributeValue(attrName, val);
						responseCode = StatusCodes.ERR_NONE;
						responseDetail = val;
					} else {
						
						// This is a get
						String val = attrMgr.getAttributeValue(attrName);
						responseCode = StatusCodes.ERR_NONE;
						responseDetail = val;
					}
				}
			} catch (CodedException e) {
				responseCode = e.getStatusCode();
				responseDetail = e.getMessage();
			}
			String responseStr = "[" + responseCode + "]" + responseDetail + "\n";
			out.print(responseStr);
			return keepProcessing;
		}
		

		/**
		 * @return true as long as processing of control commands should continue
		 */
		protected boolean acceptAndDispatchControlCommandLine(BufferedReader reader, PrintStream out) throws Exception {
			String line = reader.readLine();
			if (line != null)
				return processControlCommandInputLine(out, line);
			else
				return true;
		}

		public void run() {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(m_sock.getInputStream()));
				PrintStream out = new PrintStream(m_sock.getOutputStream());
				boolean acceptInput = true;
				while (acceptInput) {
					acceptInput = acceptAndDispatchControlCommandLine(reader, out);
				}
				m_sock.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
}
