<%@ page language="java" import="java.util.*, com.willmeyer.card.web.*" pageEncoding="ISO-8859-1"%>
<jsp:include page="header.jsp">
	<jsp:param name="title" value="REFACTR"/>
</jsp:include>

<script type="text/javascript">

function updateStateDetail() {
    var url = "/app?a=sd";
    $("#state_detail").html("Loading recent telemetry data...please wait.");
    $("#state_detail").load(url);
}

function onPageLoad() {
    updateStateDetail();	
}

$(document).ready(function() {
	onPageLoad()
    });

</script>



<h1>Vehicle State</h1>
<p>
<div id="state_detail">
Fill in...
</div>
</p>
<form>
<input type="submit" onclick="updateStateDetail(); return false;" value="refresh" class="button"/>
</form>
</p>
<jsp:include page="footer.jsp"/>

