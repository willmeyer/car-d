package com.willmeyer.card;

import org.slf4j.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

import com.willmeyer.util.PropertiesPlusPlus;

/**
 */
public class NetManager {

	protected HttpClient client;

	protected final Logger logger = LoggerFactory.getLogger(NetManager.class);
	
	NetManager(PropertiesPlusPlus props) {
		client = new HttpClient();
		this.getAndUpdateIp();
	}
	
	public IpInfo getAndUpdateIp() {
		
		// What we think our local address is
		//InetAddress addr = InetAddress.getLocalHost();
		//String localIp = addr.getHostAddress();
		
		// Ask the server for our actual external address
		HttpMethod method = new GetMethod("http://www.willmeyer.com/refactr/setip.php");
		try {
			client.executeMethod(method);
			int stat = method.getStatusCode();
			if (stat != 200) {
				throw new Exception ("HTTP status code " + stat);
			}
			String resp = method.getResponseBodyAsString();
			IpInfo ipInfo = this.parseResponse(resp);
			return ipInfo;
		} catch (Exception e) {
			logger.error("Unable to update IP address due to error: {}", e.getMessage());
			return null;
		}
	}
	
	/**
	 * Determines if we currently have external network connectivity, based on being able to do
	 * an HTTP level ping to a known location.
	 */
	public boolean haveConnectivity() {
		
		// Ping the server, see if we have connectivity outbound
		HttpMethod method = new GetMethod("http://www.willmeyer.com/refactr/ping.php");
		try {
			client.executeMethod(method);
			String resp = method.getResponseBodyAsString();
			if (resp.equals("OK")) return true;
		} catch (Exception e) {
			logger.error("Unable to establish outbound connectivity: {}", e.getMessage());
		}
		return false;
	}
	
	private IpInfo parseResponse(String response) {
		response = response.trim();
		String[] respItems = response.split(",");
		IpInfo info = new IpInfo();
		info.lastUpdateTime = Long.parseLong(respItems[1]);
		info.remoteIp = respItems[0];
		return info;
	}
	
	public static class IpInfo {
		public String remoteIp;
		public long lastUpdateTime;
	}

}
