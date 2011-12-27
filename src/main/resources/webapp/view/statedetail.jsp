<%@ page language="java" import="java.util.*, com.willmeyer.card.web.*" pageEncoding="ISO-8859-1"%>
<%
HashMap<String, String> attrs = (HashMap<String, String>)request.getAttribute("attrs");
Set<String> attrNames = attrs.keySet();
%>
<!-- 
Should be injected into a detail container
 -->
<table>
<%
for (String attrName : attrNames) {
	String val = attrs.get(attrName);
%>
<tr><td><%= attrName %>:</td><td><b><%= val %></b></td></tr>
<%
}
%>
</table>
