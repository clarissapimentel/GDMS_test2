<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.*" %>
<html:html>
	<head>
		<title>GDMS</title>
		<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">
	</head>
	<body>
		<html:form action="/export.do" method="post">
		<div class="heading" align="center">Genotyping Data Retrieval</div><br><br>
			<%String[] maps=session.getAttribute("mapsSTR").toString().split(";;");%>
	  			
	  			<table width="65%" align="center" border=0 cellpadding=2 cellspacing=2>
	  			<tr class="displayText"><td>Please select the desired map for creating export format of CMTV</td></tr>
	  			<tr><td>
	  			<table align="left" width="80%" border=0>
	  			<%for(int m=0;m<maps.length;m++){
	  				String[] str=maps[m].split("!~!");
	  		%>
	  			<tr class="displayText"><td width="45%"><input type="checkbox" name="maps" value='<%=str[1]%>'>&nbsp;<%=str[1] %></td><td> Markers : <%=str[0] %></td><td>Map Length : <%=str[2] %> cM</td></tr>
	  		<%} 
	  			%>
	  			</table>
	  			</td>
	  			</tr>
	  			<tr><td>&nbsp;</td></tr>
	  			<tr><td>&nbsp;</td></tr>
	  			<tr><td>&nbsp;</td></tr>
	  			<tr align="center"><td><html:button property="export" value="Submit" onclick="sub('map')"/></td></tr>
	  			</table>
	  			<html:hidden property="selMaps"/>
		</html:form>
	
	</body>

</html:html>
<script>
function sub(type){

	
	if(type=="map"){
		var len=document.forms[0].maps.length;
		//alert(len);
		var temp="";
		c=0;	
		for(k=0;k<len;k++){
			//alert(document.forms[0].maps[k].checked);
			if(document.forms[0].maps[k].checked==true){
				c++;
				temp=temp+"'"+document.forms[0].maps[k].value+"',";
			}
		}
		document.forms[0].selMaps.value=temp;
		document.forms[0].submit();
		
	}
}







</script>


