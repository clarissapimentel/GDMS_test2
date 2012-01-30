<%@ taglib uri="/WEB-INF/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic" prefix="logic" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>GDMS</title>
<script>

function refresh(){
document.forms[0].action="export.do";
document.forms[0].submit();
}
</script>
</head>
<body onload="refresh()">

<form action="/export" method="post">
 <center><br><font class="pageTitle"></font></center>
 <br><br>
 
<br><br><br><br><br><br>
<center>
<font class="sessionDataLabels"><b>retrieving data..</b></font><br><br>
<table border=0 cellspacing=0 width="25%">
	<%--<tr><td><img src="<%=request.getContextPath() %>/img/progressbar1.gif"/></td></tr>--%>
	<tr><td align="center"><img src="<%=request.getContextPath() %>/jsp/Images/progressbar2.gif"/></td></tr>
</table>

</center>
</form>
</body>
</html>