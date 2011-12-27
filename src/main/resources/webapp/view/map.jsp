<%@ page language="java" import="java.util.*, com.willmeyer.card.web.*" pageEncoding="ISO-8859-1"%>
<jsp:include page="header.jsp">
	<jsp:param name="title" value="REFACTR"/>
</jsp:include>
<%
String lat = (String)request.getAttribute("lat");
String lon = (String)request.getAttribute("lon");
%>

<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=true"></script>
<script type="text/javascript">

function onPageLoad() {
    detectBrowser();
    setupMap();
}

$(document).ready(function() {
    onPageLoad()
    });

function setupMap() {

    var carPos = new google.maps.LatLng(<%= lat%>, <%= lon %>);
    var mapOptions = {
      zoom: 12,
      center: carPos,
      mapTypeId: google.maps.MapTypeId.HYBRID
    };
    var theMap = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);

    var marker = new google.maps.Marker({
        position: carPos, 
        map: theMap, 
        title:"REFACTR"
    });  
}

function detectBrowser() {
	  var useragent = navigator.userAgent;
	  var mapdiv = document.getElementById("map_canvas");
	    
	  if (useragent.indexOf('iPhone') != -1 || useragent.indexOf('Android') != -1 ) {
	    mapdiv.style.width = '250px';
	    mapdiv.style.height = '250px';
	  } else {
	    mapdiv.style.width = '80%';
	    mapdiv.style.height = '80%';
	  }
	}


</script>

<!-- 
<h1>Location</h1>
<p>
REFACTR's current location:
</p>
 -->
<div id="map_canvas" style="position: relative; width: 250px; height: 250px; margin-left: auto; margin-right: auto;" >
<jsp:include page="footer.jsp"/>

