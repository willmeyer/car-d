package com.willmeyer.card.web;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.DecimalFormat;

import javax.servlet.*;
import javax.servlet.http.*;

import org.mortbay.jetty.*;
import org.mortbay.jetty.servlet.*;
import org.mortbay.jetty.webapp.*;

import org.slf4j.*;

import com.willmeyer.card.*;
import com.willmeyer.card.exception.*;

public class WebInterface {

	// assumes that this directory contains .html and .jsp files
	// This is just a directory within your source tree, and can be exported as part of your normal .jar
	static final String WEBAPPDIR = ".";
	static final String PATH_CTX = "/";
	static final String PATH_VIEW = "/view";
	public static final String PATH_CONTROLLER = "/app";

	protected Server server; 
	
	protected final Logger logger = LoggerFactory.getLogger(WebInterface.class);
	
	public void stop(){
		try {
			this.server.stop();
		} catch (Exception e) {
			logger.error("Error stopping web interface: {}", e.getMessage());
		}
	}
	
	/**
	 */
	public WebInterface(int port) throws ComponentInitException {
		try {
			server = new Server(port);
			URL rsrcUrl;
			rsrcUrl = this.getClass().getClassLoader().getResource("webapp/");
			String path = rsrcUrl.toExternalForm();
			logger.info("Starting web server for resource path '" + path + "'...");
			Context ctx = new WebAppContext(path, PATH_CTX);
			ctx.addServlet(new ServletHolder(new TheController()), PATH_CONTROLLER);
			server.setHandler(ctx);
			
			// Start the server
			server.start();
		} catch (Exception e) {
			throw new com.willmeyer.card.exception.ComponentInitException("Unable to start web interface due to error: " + e.getMessage(), ComponentInitException.FailureMode.UNRECOVERABLE);
		}
	}
	
	@SuppressWarnings("serial")
	public final class TheController extends HttpServlet {

		public static final String PARAM_ACTION = "a";
		public static final String ACTION_HOME = "h";
		public static final String ACTION_TOOLS = "a";
		public static final String ACTION_SYSTEMSTATE = "t";
		public static final String ACTION_MAP = "l";
		public static final String ACTION_STATEDETAIL = "sd";
		public static final String ACTION_UPDATENET = "a_un";
		public static final String ACTION_TOGGLESTANDBY = "a_ts";
		
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			this.doPost(req, resp);
		}

		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			
			// What are we doing?
			String action = req.getParameter(PARAM_ACTION);
			if (action == null) {
				action = ACTION_HOME;
			}
			try {
				if (action.equalsIgnoreCase(ACTION_HOME)) {
					String path = WebInterface.PATH_VIEW + "/home.jsp";
					req.getRequestDispatcher(path).forward(req, resp);
				} else if (action.equalsIgnoreCase(ACTION_SYSTEMSTATE)) {
					String path = WebInterface.PATH_VIEW + "/state.jsp";
					req.getRequestDispatcher(path).forward(req, resp);
				} else if (action.equalsIgnoreCase(ACTION_TOOLS)) {
					this.gotoToolsPage(req, resp, null);
				} else if (action.equalsIgnoreCase(ACTION_UPDATENET)) {
					this.doUpdateNet(req, resp);
				} else if (action.equalsIgnoreCase(ACTION_TOGGLESTANDBY)) {
					this.doToggleStandby(req, resp);
				} else if (action.equalsIgnoreCase(ACTION_MAP)) {
					this.updateLocation(req);
					String path = WebInterface.PATH_VIEW + "/map.jsp";
					req.getRequestDispatcher(path).forward(req, resp);
				} else if (action.equalsIgnoreCase(ACTION_STATEDETAIL)) {
					this.updateTelemetry(req);
					String path = WebInterface.PATH_VIEW + "/statedetail.jsp";
					req.getRequestDispatcher(path).forward(req, resp);
				} else {
					
					// Huh?
					// show error on home screen
				}
			} catch (Exception e) {
				e.printStackTrace(); // TODO show error page
			}
		}

		private void doToggleStandby(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			CarD card = CarD.getInstance();
			if (card.inStandby()) {
				card.standby(false);
			} else {
				card.standby(true);
			}
			this.gotoToolsPage(request, response, null);
		}
		
		private void doUpdateNet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			String message = "Connectivity checked.";
			this.gotoToolsPage(request, response, message);
		}
		
		/**
		 * Dispatches to the tools page, with an optional message at the top.
		 */
		private void gotoToolsPage(HttpServletRequest request, HttpServletResponse response, String message) throws ServletException, IOException {
			
			// Basic page and header message
			String path = WebInterface.PATH_VIEW + "/tools.jsp";
			if (message != null) {
				request.setAttribute("message", message);
			}
			
			// Mode
			Boolean inStandby = new Boolean(CarD.getInstance().inStandby());
			request.setAttribute("standby", inStandby);

			// Device list
			List<DeviceManager.DeviceState> devices = CarD.getDeviceManager().getDeviceSummary();
			request.setAttribute("devices", devices);
			
			// Net check
			NetManager.IpInfo info = CarD.getNetManager().getAndUpdateIp();
			String netStat;
			if (info == null) {
				netStat = "It does NOT look like we have connectivity to the outside world.";
			} else {
				netStat = "We DO have outbound connectivity.<br/>External IP is " + info.remoteIp + ".";
			}
			request.setAttribute("netstat", netStat);

			// Forward
			request.getRequestDispatcher(path).forward(request, response);
		}
		
		private void updateTelemetry(HttpServletRequest req)  {
			HashMap<String, String> attrs = new HashMap<String, String>();
			AttributeManager attrMgr = CarD.getAttributeManager();
			String attrVal;
			
			// Speed
			attrVal = attrMgr.getAttributeValueSafe("/obdii/sae.mph");
			attrs.put("Curr. Speed", attrVal);

			// RPM
			attrVal = attrMgr.getAttributeValueSafe("/obdii/sae.rpm");
			attrs.put("Engine RPM", attrVal);

			// Coolant temp
			attrVal = attrMgr.getAttributeValueSafe("/obdii/sae.ect");
			attrs.put("Clnt. Temp", attrVal);

			// Coolant temp
			attrVal = attrMgr.getAttributeValueSafe("/obdii/sae.fuel");
			attrs.put("Fuel level", attrVal);

			// MAF
			attrVal = attrMgr.getAttributeValueSafe("/obdii/sae.maf");
			attrs.put("Air flow", attrVal);

			// Position
			String lat = attrMgr.getAttributeValueSafe("/gps/lat");
			String lon = attrMgr.getAttributeValueSafe("/gps/lon");
			try {
				double latD = Double.parseDouble(lat);
				double lonD = Double.parseDouble(lon);
				DecimalFormat df = new DecimalFormat("#.##");
				lat = df.format(latD);
				lon = df.format(lonD);
			} catch (Exception e) {
				logger.error("Bad coordinate formatting for {} {}", lat, lon);
			}
			attrs.put("Curr. Pos.", lat + ", " + lon);

			// Done
			req.setAttribute("attrs", attrs);
		}

		private void updateLocation(HttpServletRequest req) throws Exception {
			AttributeManager attrMgr = CarD.getAttributeManager();
			String lat = attrMgr.getAttributeValue("/gps/lat");
			String lon = attrMgr.getAttributeValue("/gps/lon");
			req.setAttribute("lat", lat);
			req.setAttribute("lon", lon);
		}
	}
	
}
