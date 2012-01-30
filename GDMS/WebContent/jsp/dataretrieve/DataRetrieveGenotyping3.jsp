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
			<% 
				ArrayList dataList=null;
				int totalItemcount=0;
				if(request.getQueryString().equalsIgnoreCase("first")){ 
					String opType=session.getAttribute("type").toString();
				
					if(opType.equals("markers")){				
						dataList=(ArrayList)session.getAttribute("markerList");
						totalItemcount=Integer.parseInt(session.getAttribute("MarkerCount").toString());
					}else{
						dataList=(ArrayList)session.getAttribute("AccListFinal"); 
						totalItemcount=Integer.parseInt(session.getAttribute("GenoCount").toString());
					}
					int genoCount=0;
					int colCount=0;
					String str="";
						
					String option=session.getAttribute("op").toString();
					if(option.equalsIgnoreCase("Get Markers"))
						str="Markers";
					else
						str="GermplasmName";
					
					genoCount=(totalItemcount/30);
					if(genoCount*30<totalItemcount){
						genoCount=genoCount+1;	
					} 
					colCount=1;
					
					%>
				<table border=0 width="50%" cellpadding=0 align="center">
					<tr>
						<td align="left" class="displaysmallText" colspan=2><html:checkbox property="chk" onclick="checkAllAcc();" > Select All </html:checkbox></td>
						<td align="left" class="displayText" colspan=2>  Select from the list of <b><%=totalItemcount%></b>  </td>						
					</tr>
				</table>
				<table border=0 width="80%" align="center" cellpadding=10 cellspacing=0 >
				<tr><td align="center">
					
			  	<br>
					<Table border="0" cellpadding="0" cellspacing="0" align="left">
						
						<tr bgcolor="white">
						<%
							String data="";
							int rowCount=0;
							String[] args=null;
							for (int j = 0;j<totalItemcount;j++){
								data=dataList.get(j).toString();
								if(!(opType.equals("markers"))){	
									args=data.split(",");
								}
								if(rowCount==genoCount){
									rowCount=0;
									%>
									</tr><tr class='reportsBody'>
								<%}	
								if(opType.equals("markers")){%>	
									<td nowrap class="displaysmallText" ><input type="checkbox" name="McheckGroup" value="<%=dataList.get(j)%>"><%=dataList.get(j)%></td>
								<%}else{ %>	
									<td nowrap class="displaysmallText" ><input type="checkbox" name="McheckGroup" value="<%=args[1]%>"><%=args[0]%></td>
				 				<%} %>
								<%rowCount++;
							} %>
						</tr>
				  </table>
					</td>
				  </tr>
				  <tr>
				  
				  	<br>
				 	<table border=0 align=center width=77%><tr><td class="displayBoldText" align=left>Choose Data Export Format You Would Like to View</td></tr></table>
					   	 <table align="center" width=77% border=0>
					    	 <tr>
						    	 <td width=1%><html:radio  property="FormatcheckGroup" value="Genotyping X Marker Matrix" onclick="selOpt(this)"/></td>
						    	 <td class="QrytextColor" align=left>Genotyping X Marker Matrix</td>
					    	 </tr>
					    	  <tr>
						    	 <td width=1%><html:radio property="FormatcheckGroup" value="Flapjack" onclick="selOpt(this)"/></td>
						    	 <td class="QrytextColor" align="left">Flapjack</td>
						     </tr>  					    	 
						     
					    	 <tr align="left">
						    	 <td colspan=2>
						    	<%
						    	ArrayList maps=(ArrayList)request.getSession().getAttribute("mapList");
						    	
						    	
						    	%>
						    	 <span id="map" style="visibility: hidden;">
								 	<center>
										<table width=60% align="left" border=0>
											<tr>
											<%
											//System.out.println("%%%%%% "+maps);
											if(!(maps.isEmpty())){%>
												<th width="40%">Please select the map</th>
												<td width="5%" align="left">:</td>
												<td align="left"><select name="maps" id="maps" >
														<option value=""></option>
													<%for(int i=0;i<maps.size();i++){%>
														<option value="<%=maps.get(i)%>"><%=maps.get(i)%></option>				
													<%} %>
												</select></td>
												<%}else{ %>
													<th width="40%" colspan="3" align="center"><font color="red">NO Maps!!!</font> <font color="black">Please upload Mapping data to create Export formats for Flapjack...</font></th>
												<%} %>
											</tr>
									 	</table>
									 	
									</center>
								</span>
						    	 </td>
					    	 </tr> 	 
							</table><br>
						</tr>
					</table></td>
				</tr>
				</table>
				<html:hidden property="opType" value="nonDataset"/> 	
				<center><html:submit property="export" value="Submit" onclick="sub('<%=str%>')"/></center>
				<input type="hidden" name="selectListN"> 	
		  		<input type="hidden" name="markersSel">
		  		
	  		<%}else if(request.getQueryString().equalsIgnoreCase("second")){  %>
	  			<table border=0 cellpadding=0 cellspacing=0 width="75%" align="center">
						<tr>
							<td>
								<table border=0 width="75%" bordercolor="black" cellpadding=2 cellspacing=2 bgcolor="white" align=center>
									<tr>
									
								<%
								String path1="";
								String label1="";
								String label2="";
								String path="";
								String ExportFormats=session.getAttribute("exportFormat").toString();
								if(ExportFormats.equals("Flapjack")) {%>
									<br><br>					
									<center><html:submit value="Run Flapjack" property="flapjack" onclick="funcFlapjack(this.value)"/></center>
					
								<%	//System.out.println("Flapjack");
								}else if(ExportFormats.equals("Genotyping X Marker Matrix")){
								%>			
									<%if(session.getAttribute("op").toString().equalsIgnoreCase("dataset")){ %>
										<td colspan=2 align="center"  class="displayBoldText">
											<font color=red><%=session.getAttribute("genCount")%></font>
											 Germplasm ID(s)   
											 <font color=red> <%=session.getAttribute("mCount")%></font> 
											 Marker(s)										 
										</td>	
									<%}else{ %>
										<td colspan=2 align="center"  class="displayBoldText">
											<font color=red><%=session.getAttribute("genCount")%></font>
											 Germplasm ID(s)   
											 <font color=red> <%=session.getAttribute("mCount")%></font> 
											 Marker(s) selected 										 
										</td>
									<%} %>							
										<tr>
											<td  colspan="2" align="center">&nbsp;</td>
										</tr>
										<tr>
											<td colspan="2" align="center" class="displayBoldText" bgcolor="lightgrey">
											Data Export Formats
											</td>
										</tr>
										<%
										
										//for(int l=0;l<ExportFormats.length;l++){
											if(ExportFormats.equals("Genotyping X Marker Matrix")){
												path="./jsp/analysisfiles/matrix"+session.getAttribute("msec")+".xls";
											}else if(ExportFormats.equals("Flapjack")){
												label1="Flapjack data file";
												label2="Flapjack Map file";
												path="../.././tempfiles/Flapjack"+session.getAttribute("msec")+".dat";	
												path1="../.././tempfiles/Flapjack"+session.getAttribute("msec")+".map";	
											}
										%>
										<tr>
											<td colspan="2">&nbsp;</td>
										</tr>
										<tr>
											<td width="15%" align=right>
												<img src="jsp/Images/bullet2.gif"  border=0 >
											</td>
											<td align="left"> 												
												<a href=<%=path %> target="_blank" class="link2">												
													<b>&nbsp;&nbsp;<%=ExportFormats%></b>													
												</a>													
											</td>
										</tr>								
										
									<tr><td>&nbsp;</td></tr>						
									<tr align="center">
										<td colspan="2"><input type="button" name="Back" value=" Back "  styleClass="button" onclick="funcBack()"/></td>
									</tr>
								</table>
							</td>
							
						</tr>
						<%}else{
							//int c=Integer.parseInt(session.getAttribute("count").toString());
							String[] files1=session.getAttribute("f1").toString().split(";;");%>
							<tr>
								<td colspan="2" align="center" class="displayBoldText" bgcolor="lightgrey">Data Export Formats</td>
							</tr>
							<%for(int f=0;f<files1.length;f++){
								String[] files=files1[f].split("!~!");
							%>							
								<%path="./jsp/analysisfiles/"+files[1]+"CMTV.txt";%>
								<tr><td colspan="2">&nbsp;</td></tr>
								<tr>
									<td width="15%" align=right>
										<img src="jsp/Images/bullet2.gif"  border=0 >
									</td>
									<td align="left"> 												
										<a href=<%=path %> target="_blank" class="link2">												
											Download <b>&nbsp;&nbsp;<%=ExportFormats%> </b>	&nbsp;input file of map <b><%=files[0]%></b>												
										</a>													
									</td>
								</tr>								
							<%}%>	
							<tr><td>&nbsp;</td></tr>	
							<tr><td>&nbsp;</td></tr>					
							<tr align="center">
								<td colspan=2><input type=submit value="View in CMTV" onclick="funcFlapjack(this.value)">&nbsp;&nbsp;<input type="button" name="Back" value=" Back " onclick="funcBack()"/></td>
							</tr>
						<%} %>
					</table>	  		
	  		<%}else if(request.getQueryString().equalsIgnoreCase("third")){  
	  			String[] maps=session.getAttribute("mapsSTR").toString().split(";;");%>
	  			
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
	  			<tr align="center"><td><html:submit property="export" value="Submit" onclick="sub('map')"/></td></tr>
	  			</table>
	  			<html:hidden property="selMaps"/>
	  		<%}%>
	  		</center>
		</html:form>	
	</body>
</html:html>
<script>
function checkAllAcc(){
	var len=document.forms[0].McheckGroup.length;
	var temp="";
	c=0;
	if(document.forms[0].chk.checked == true){
		document.forms[0].chk.value="checked";
		for(i=0;i<len;i++){
			document.forms[0].McheckGroup[i].checked=true;
			c++;
			//alert("Marker at "+k+"="+document.retDisp.McheckGroup[k].value);
			temp=temp+document.forms[0].McheckGroup[i].value+";;";
		}	 
	}else{
		for(i=0;i<len;i++){
	 		document.forms[0].McheckGroup[i].checked=false;
	 	}
	}	
	document.forms[0].markersSel.value=temp;
}
function selOpt(opt){
	var check=opt.value;
	if(check=="Flapjack"){				
		document.getElementById('map').style.visibility='visible';		
	}else{
		document.getElementById('map').style.visibility='hidden';		
	}	
}
function funcFlapjack(a){
	//alert(a);
	document.forms[0].action="jsp/dataretrieve/Flapjack.jsp?str="+a;	
}
function funcBack(){
	document.forms[0].action="genotypingpage.do?second";
	document.forms[0].submit();	
}
function sub(type){

	var selType="";
	var mapName="";
	if(type=="map"){
		var len=document.forms[0].maps.length;
		alert(len);
		var temp="";
		c=0;	
		for(k=0;k<len;k++){
			alert(document.forms[0].maps[k].checked);
			if(document.forms[0].maps[k].checked==true){
				c++;
				temp=temp+"'"+document.forms[0].maps[k].value+"',";
			}
		}
		document.forms[0].selMaps.value=temp;
		
	}else{
		var len=document.forms[0].McheckGroup.length;
		var temp="";
		c=0;	
		for(k=0;k<len;k++){
			if(document.forms[0].McheckGroup[k].checked==true){
				c++;
				temp=temp+document.forms[0].McheckGroup[k].value+";;";
			}
		}
		//alert(temp);
		if(temp==null){
			alert("Please Select " + type);
			return false;
		}
	
		/*selType=document.retDisp.selectList.value;
		//alert("selType="+selType);
		if(selType.substring(0, selType.length-4).match("Flapjack")){
			mapName=document.forms[0].maps.value;
			strC=temp+"~!~"+selType+"~!~"+mapName;
		}else{
			strC=temp+"~!~"+selType;
		}*/
		//alert("strC="+strC);
		document.forms[0].markersSel.value=temp;
		//alert("###########  "+document.getElementById('markersSel').value); 
		//document.retDisp.action="AccMarkSelDispl.jsp";
		//document.retDisp.submit();			
		//}
	}	
}



</script>
