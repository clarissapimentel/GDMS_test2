<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>GDMS</title>
<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">

</head>
<body>

	<html:form action="/genotypingpage.do?second">
	
		<%if((session.getAttribute("user")==null)||(session.getAttribute("user")=="null")||(session.getAttribute("user")=="")){%>
			<br><br><br>
			<center><font color="blue" face="verdana" size="3px">Please Login to upload/retrieve data</font></center>
		<%} %>
		<logic:notEmpty name="user">
	
			<br>
			<%
				String str=request.getParameter("str"); 
				String path=request.getSession().getServletContext().getRealPath("//");	
				if(str.equals("ret")){
			%>
			
				<Table border=0 width="60%" cellpadding=5 cellspacing=2 align="center">
					<tr style="font-size: medium;font-weight: bold;">
						<%--<td colspan=2>&nbsp;&nbsp;<img src="<%=path%>" height="15px" width="15px">&nbsp;&nbsp;&nbsp;
							Retrieve </td>--%>
						<td colspan=2>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Retrieve </td>
					</tr>
					<tr><td colspan=2>&nbsp;</td></tr>
					<tr class="displayText">
						<td colspan=2>
							<table width=70% border=0 align="center">
								<tr class="displayText">
									<td align="center"><html:radio property="reportType" value="genotyping" onclick='nextPage(this)'>Genotyping Data</html:radio></td>
									<td align="center"><html:radio property="reportType" value="marker" onclick='nextPage(this)'>Marker Information</html:radio></td>
								</tr>
							</table>
						</td>
					</tr>
				</Table>
				
				<%}
				if(str.equalsIgnoreCase("second")){ %>
					<Table border=0 width="60%" cellpadding=5 cellspacing=2 align="center">
					<tr style="font-size: medium;font-weight: bold;">
						<%--<td colspan=2>&nbsp;&nbsp;<img src="<%=path%>" height="15px" width="15px">&nbsp;&nbsp;&nbsp;
							Retrieve </td>--%>
						<td colspan=2>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Retrieve </td>
					</tr>
					<tr><td colspan=2>&nbsp;</td></tr>
					<tr class="displayText">
						<td colspan=2>
							<table width=70% border=0 align="center">
								<tr class="displayText">
									<td align="center"><html:radio property="reportType" value="genotyping" onclick='nPage(this)'>Genotyping Data</html:radio></td>
									<td align="center"><html:radio property="reportType" value="marker" onclick='nPage(this)'>Marker Information</html:radio></td>
								</tr>
							</table>
						</td>
					</tr>
				</Table>
					<br>
					<logic:notEmpty name="listValues">
						<table border="0" width="40%" align="center">					
							<tr class="displayText">
								<td width="35%" align="right">Please Select Crop</td><td width="5%" align="center">:</td>
								<td><html:select property="crop">
									<logic:iterate name="listValues" id="crops" type="java.lang.String">
									<html:option value="<%=crops %>" />
									</logic:iterate>
									</html:select>
								</td>
							</tr>	
						</table>
						<br><br>
						<center>							
							<html:submit property="next" value="Next"/>
						</center>
					</logic:notEmpty>
					<logic:empty name="listValues">
						<table border="0" width="40%" align="center">					
						<tr class="errorMsgs">
							<td colspan="3" align="center">No data submitted.. </td>
						</tr>	
						<tr><td>&nbsp;</td></tr>
							
					</table>
					</logic:empty>
					
				<%} %>
			<br>
			</logic:notEmpty>

	</html:form>
</body>
</html:html>
<script>
function nextPage(opt){
	//alert(p);
	var path='<%=request.getSession().getServletContext().getRealPath("//")%>';
	//alert('<%=request.getSession().getServletContext().getRealPath("//")%>');
	document.forms[0].elements['reportType'].value="opt.value";
	var check=opt.value;
	//alert("check="+check);
	if(check=="genotyping"){
		document.forms[0].action="../../genotypingpage.do?first";	
		//alert(document.forms[0].action);	
		document.forms[0].submit();
	}else{		
		document.forms[0].action="../../markerpage.do";
		//alert(document.forms[0].action);
		document.forms[0].submit();
	}
}
function nPage(opt){
	//alert(p);
	var path='<%=request.getSession().getServletContext().getRealPath("//")%>';
	//alert('<%=request.getSession().getServletContext().getRealPath("//")%>');
	document.forms[0].elements['reportType'].value="opt.value";
	var check=opt.value;
	//alert("check="+check);
	if(check=="genotyping"){
		document.forms[0].action="././genotypingpage.do?first";	
		//alert(document.forms[0].action);	
		document.forms[0].submit();
	}else{		
		document.forms[0].action="././markerpage.do";
		//alert(document.forms[0].action);
		document.forms[0].submit();
	}
}


</script>
	