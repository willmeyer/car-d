<%@ page language="java" import="java.util.*, com.willmeyer.card.web.*, com.willmeyer.card.*" pageEncoding="ISO-8859-1"%>
<jsp:include page="header.jsp">
	<jsp:param name="title" value="REFACTR"/>
</jsp:include>

<%
List<DeviceManager.DeviceState> devices = (List<DeviceManager.DeviceState>)request.getAttribute("devices");
String message = (String)request.getAttribute("message");
String netStat = (String)request.getAttribute("netstat");
Boolean inStandby = (Boolean)request.getAttribute("standby");
if (message != null) {
%>
<div style="border: 1px solid #444; padding: 10px; padding-top: 0px;">
<h1>System Response:</h1>
<div style="font-weight: bold; ">
<%= message %>
</div>
</div>
<%
}
%>
<h1>Management Tools</h1>
<p>
<% if (inStandby) {
%>
System in standby
<% 	
} else {
%>
System active
<%	
}
%>
<form action="/app" method="get" >
<input type="submit" value="Toggle" class="button"/>
<input type="hidden" name="a" value="a_ts"/>
</form>
</p>
<h2>Net Connection</h2>
<p>
<%= netStat %>
<form action="/app" method="get" >
<input type="submit" value="Check Connectivity" class="button"/>
<input type="hidden" name="a" value="a_un"/>
</form>
</p>
<h2>Devices</h2>
<p>
<table>
<% 
if ((devices != null) && devices.size() > 0) {
    for (DeviceManager.DeviceState dev : devices) {
    	String nameTxt = dev.interfaceName;
    	String onTxt = (dev.started ? "on" : "off");
%>
<tr><td><%= nameTxt %>:</td><td><b><%= onTxt  %></b></td></tr>
<%
    }
} else {
%>
No devices currently active.
<%
}
%>
</table>
</p>
<jsp:include page="footer.jsp"/>

