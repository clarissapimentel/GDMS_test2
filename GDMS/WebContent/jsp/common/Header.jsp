<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>GDMS</title>

</head>
<body>
<form method="post">
<center>
<%if(session.getAttribute("user")==null){
//System.out.println("In Header.jsp USER="+session.getAttribute("user"));
%> 
	<img src="../Images/GDMS.gif" border=0 usemap="#Map2">
		<map name="Map2">
			<area shape="rect" coords="13,90,60,109" href="../../" alt="Home" target="_top">
		 	<area shape="rect" coords="88,90,142,109" href="../../jsp/common/GDMSLayout.jsp?str=upload" alt="Upload" target="_top">
		  	<area shape="rect" coords="172,90,235,109" href="../../jsp/common/GDMSLayout.jsp?str=retrieve" alt="Retrieve" target="_top">
		  	<area shape="rect" coords="260,90,320,109" href="../../jsp/common/GDMSLayout.jsp?str=delete" alt="Retrieve" target="_top">
		</map>
<%}else{%>
	<img src="../Images/GDMSL.gif" border=0 usemap="#Map3">
		<map name="Map3">
		 	<area shape="rect" coords="13,90,60,109" href="../../" alt="Home" target="_top">
		 	<area shape="rect" coords="88,90,142,109" href="../../jsp/common/GDMSLayout.jsp?str=upload" alt="Upload" target="_top">
		  	<area shape="rect" coords="172,90,235,109" href="../../jsp/common/GDMSLayout.jsp?str=retrieve" alt="Retrieve" target="_top">
		  	<area shape="rect" coords="260,90,320,109" href="../../jsp/common/GDMSLayout.jsp?str=delete" alt="Retrieve" target="_top">
	  		<area shape="rect" coords="750,88,830,110" href="../../jsp/common/GDMSLayout.jsp?str=logout" alt="Logout" target="_top">	
	  	</map>

<%} %>

	</center>
	</form>
</body>
</html>