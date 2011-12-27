<%@ page language="java" import="java.util.*, com.willmeyer.card.web.*" pageEncoding="ISO-8859-1"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String msg = (String)request.getAttribute("com.clearspring.oexchange.registry.admin.error");
%>

<jsp:include page="header.jsp">
	<jsp:param name="title" value="Error"/>
</jsp:include>
  
  <body>
    <h1>Sorry, an error occurred...</h1>
    <h3>Error Description</h3>
    <p>
    <%= msg %>
    </p>

<jsp:include page="footer.jsp"/>
