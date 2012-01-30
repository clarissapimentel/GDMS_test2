<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<html:html>
<head>
	<title>GDMS</title>
	<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">
</head>
<%String op=request.getParameter("op");%>
		<center>
		<% if(request.getSession().getAttribute("user")==null){ %>
	<body>

	<html:form method="post" action="/login.do">
		<!-- <div class="heading" align="center">Login</div>-->
		<br><br><br><br><br>
		
			<table cellspacing=5 border="0">
				<tr>
					<td nowrap valign=top class="displayText">Username:</td>
					<td width=5></td>
					<td align=left>
						<html:text property="uname" value="" />		
					</td>
				</tr>			
				<tr>
					<td nowrap class="displayText">Password:</td>
					<td width=5></td>
					<td align=left><html:password property="password" value=""/></td>				
				</tr>				
			</table>			
			<html:hidden property="menuOp" value='<%=request.getParameter("op")%>'/>
			<br><br><br>
			<html:submit value="Submit" />
			<html:reset value="Clear" />&nbsp;&nbsp;
			</center> 
	</html:form>
</body>
		<%}
		 if(request.getSession().getAttribute("user")!=null){
		%>
		<body onload="func_call('<%=op%>')">
    <form name="call" method="post">
    </form>
</body>

		<%} %>
		
</html:html>


<script>
function func_call(str)
{
	//alert(str);
	document.call.action="../../"+str+".do";
	//alert(document.call.action);
	document.call.submit();
}
</script>