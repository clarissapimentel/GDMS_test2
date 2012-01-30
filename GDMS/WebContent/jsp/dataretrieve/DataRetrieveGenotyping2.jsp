<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg" %>
<%@ page import="java.util.*" %>
<html:html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>GDMS</title>
		<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">		
	</head>
	<body>
		<html:form action="/dataretrieval.do">
			<logic:notEmpty name="result">
			<center>
				<table border=0 width="60%" cellpadding=0>		
					<tr><td align="center" class="displayText" colspan=2> <b>'<%=session.getAttribute("recCount")%>'</b> markers are polymorphic between <b><%=session.getAttribute("selLines") %></b> </td>
						<td align="left" class="displaysmallText" colspan=2> </td>
					</tr>
				</table>
	  			<br>
	  			<%
	  				int missingCount=0;
	  				
	  				int mcount=0;
		  			int totalItemcount=Integer.parseInt(session.getAttribute("recCount").toString());
		  			int missCount=Integer.parseInt(session.getAttribute("missingCount").toString());
	  				int genoCount=0;
	  				int colCount=0;
	  				int rowCount=0;
	  				genoCount=(totalItemcount/30);
	  				if(genoCount*30<totalItemcount){
	  					genoCount=genoCount+1;	
	  				} 
	  				
	  				missingCount=(missCount/30);
	  				if(missingCount*30<missCount){
	  					missingCount=missingCount+1;
	  				}
	  				
	  				colCount=1;
	  				String map=session.getAttribute("map").toString();
	  				System.out.println("map="+map);
		  			String[] mD=null;
		  			String mapData="";
		  			if(map.equalsIgnoreCase("true")){
		  				mapData=(String)session.getAttribute("map_data");
		  				mD=mapData.split("~!!~");
		  				System.out.println("mD.length="+mD.length);
		  				mcount=mD.length;
		  			}
		  			if(map.equalsIgnoreCase("false")){
		  				mcount=totalItemcount;
		  			}
	  				
	  			%>
	  			<% %>
	  			<table width="50%" border=0>
					<tr valign="top" style="font-size: small;">
						<td width="50%" >
							<table border="1" width="50%" align="center"><tr bgcolor="#006633">	
								<%if(map.equalsIgnoreCase("true")){
									for(int p=0;p<genoCount;p++){ %>					
										<td nowrap align="center" class="displaysmallwhiteboldText">Marker</td>
										<td nowrap align="center" class="displaysmallwhiteboldText">Chromosome</td>
										<td nowrap align="center" class="displaysmallwhiteboldText">Position</td>									
									<%} %>	
									</tr>								
									<%
									for (int j = 0;j<mcount;j++){	
										String[] strMdata=mD[j].split("!~!");
										//System.out.println("j value="+strMdata[0]);								
										if(rowCount==genoCount){
											rowCount=0;%>
											</tr><tr class="displaysmallText">
										<%}	%>															
										<td nowrap class="displaysmallText" ><%=strMdata[0]%></td>				
										<td nowrap class="displaysmallText" ><%=strMdata[2]%></td>
										<td nowrap class="displaysmallText" ><%=strMdata[1]%></td>										
										<%rowCount++;	
									}
								}else if(map.equalsIgnoreCase("false")){
									for(int p=0;p<genoCount;p++){ %>					
										<td nowrap align="center" class="displaysmallwhiteboldText">Marker</td>
									<%} %>	
									</tr>	
									<%
										ArrayList markers=(ArrayList)session.getAttribute("result");										
										for (int j = 0;j<totalItemcount;j++){
											if(rowCount==genoCount){
												rowCount=0;%>
												</tr><tr class="displaysmallText">
											<%}	%>															
											<td class="displaysmallText"><%=markers.get(j)%></td>											
											<%rowCount++;										
										}
								}%>									
								</tr>		
							</table>
						</td>					
					</tr>
				</table>
				<br>
				<logic:notEmpty name="MissingData">
					<table border=0>
						<tr bgcolor="#006633" ><td nowrap align="center" class="displaysmallwhiteboldText">Markers with missing data(<%=session.getAttribute("missingCount")%>)</td></tr>
									
						<tr><td><table border=1 align="center" width="100%">
						
									<%
										ArrayList missingMarkers=(ArrayList)session.getAttribute("MissingData");										
										for (int j = 0;j<missCount;j++){
											if(rowCount==missingCount){
												rowCount=0;%>
												<tr class="displaysmallText">
											<%}	%>															
											<td class="displaysmallText"><%=missingMarkers.get(j)%></td>											
											<%rowCount++;										
										}
								//}%>			
							
						</tr>
						</table>
						</td></tr>
					</table>
				</logic:notEmpty>
				<br><br>
			</logic:notEmpty>	
			<center>
 				<html:button property="backButton" value="Back" onclick="javascript:history.back()"/>
 			</center>
		</html:form>
	</body>
</html:html>

