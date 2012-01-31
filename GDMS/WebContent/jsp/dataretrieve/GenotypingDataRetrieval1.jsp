<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.*" %>
<html:html>
	<head>
		<title>GDMS</title>
		<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">

		<script>
			function pageRefresh(){
				document.forms[0].qtlData.checked=false;
				document.forms[0].trait.checked=false;
			}
			function msg(){
				<%
				
				String strValue = "";				
				String strResult = (String)request.getSession().getAttribute("indErrMsg");
				String strResult1 = request.getQueryString();
				if(strResult1.equals("ErrMsg")){
					strValue = (String)	session.getAttribute("indErrMsg");
					strResult = "Error : "+strValue +".";
				}
				session.removeAttribute("indErrMsg");
				if(strResult == null)
					strResult = "";
				
					
				if(strResult.equals("ErrMsg")){
					strValue = (String)	session.getAttribute("indErrMsg");
					strResult = "Error : "+strValue +".";
				}
				%>
				if(document.forms[0].elements['hResult'].value != ""){
					alert(document.forms[0].elements['hResult'].value);					
					document.forms[0].qtl.focus();
					document.forms[0].qtl.value="";
				}
				<%
				
				session.removeAttribute("indErrMsg");
				%>
			}
		</script>
	</head>
	<body onload="msg();pageRefresh();">
		<html:form action="/dataretrieval.do" method="post" enctype="multipart/form-data">
			<div class="heading" align="center">Genotyping Data Retrieval</div>
			<br><br>
			<center>
				<table width="70%" align="center" border=0>
					<%--<tr style="font-size: medium;font-weight: bold;"><td colspan=3>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Retrieve</td></tr>
					<tr><td>&nbsp;</td></tr>--%>
					<tr class="displayText">
						<td width="35%" align="right"><html:radio property="retrievalType" value="Output" onclick="retrieveOPData(this)">Genotyping Matrix</html:radio></td>
						<td width="35%" align="center"><html:radio property="retrievalType" value="polymorphic" onclick="retrieveOPData(this)">Polymorphic Markers</html:radio></td>
						<td width="30%" align="left"><html:radio property="retrievalType" value="QTL/Map" onclick="retrieveOPData(this)">Map/QTL Data</html:radio></td>
						
					</tr>
				</table>
				
				<input type=hidden name="hResult" value='<%=strResult %>'>
				<%if(request.getQueryString().equals("QTL")){%>
				<br>
				<br>
				<html:errors/>
				<br>
				<br>
				
				<table border=0 width="45%">
					<tr class="displayText" align="center">
						<td>Search by &nbsp;&nbsp;&nbsp;
							<select name="maps" id="maps" onclick="onClickOption(this.name)">
								<option value="maps">Map Name</option>
								<option value="QTLName">QTL Name</option>
								<option value="Trait">Trait</option>								
							</select>
							&nbsp;&nbsp;&nbsp;<html:text property="qtl" value="" style="COLOR:#666;"/>
						</td>
					</tr>
						
					
					<tr><td>&nbsp;</td></tr>
					<tr><td>&nbsp;</td></tr>
					<tr class="displayText" align="center">
						<td align="center"><html:submit property="qtlsub" value="Submit" onclick="return sub('qtl')"/></td>
					</tr>
				</table>
				<input type=hidden name="retType" value="">
				
				<%} 
				if(request.getQueryString().equals("lines")){				
				%>
				<br>
				<logic:notEmpty name="listValues">
				<br>
				<table border=0 width="70%" align="center">
					<tr class="displayText">
						<td width="50%" align="right">Please select the Lines :</td><td>&nbsp;</td>					
					</tr>
					<tr><td>&nbsp;</td></tr>
					<tr class="displayText" align="center">
						<td colspan=2 align="center"><html:select property="linesO" size="5">
							<logic:iterate name="listValues" id="lines" type="java.lang.String">
								<html:option value="<%=lines%>"/>
							</logic:iterate>					
							</html:select> &nbsp; and &nbsp; 
							<html:select property="linesT" size="5">
								<logic:iterate name="listValues" id="lines" type="java.lang.String">
									<html:option value="<%=lines%>" />
								</logic:iterate>					
							</html:select>
						</td> 
					</tr>
					<tr><td>&nbsp;</td></tr>
					<tr><td>&nbsp;</td></tr>
					
					<tr class="displayText" align="center">
						<td colspan="2" align="center"><html:submit property="linesSub" value="Submit" onclick="return sub('lines')"/></td>
					</tr>
				</table>
				</logic:notEmpty>
				<%} 
				if((request.getQueryString().equals("files"))||(request.getQueryString().equals("mupl"))||(request.getQueryString().equals("gupl"))||(request.getQueryString().equals("dset"))){
				%>
				<br><br>
				<table border=0 width="45%" align="center">
					<tr class="displayText">
						<td width="25%" align="center"><font color="black">Retrieve using :</font></td>					
						<td width="20%" align="center"><html:radio property="selection" value="gids" onclick="selOpt(this)"/>GIDs</td>
						<td width="25%" align="center"><html:radio property="selection" value="markers" onclick="selOpt(this)"/>Markers</td>
						<td width="25%"><html:radio property="selection" value="dataset" onclick="selOpt(this)"/>Dataset</td> 
			 												
					</tr>
				</table>
				<%}
				if(request.getQueryString().equals("gupl")){
				%>
				<br>
					 <br>
					<table width=45% align="center" border=0>
						<tr><th>Upload the text file with desired gids</th><td width="5%" align="left">:</td><td align="left"><html:file property="txtNameM"/></td></tr>
						<tr><td>&nbsp;</td></tr>
						<tr><td align="center" colspan="3"><a href="<%=request.getContextPath()%>/jsp/dataretrieve/GIDs.txt" target="new">Sample File</a></td></tr>
						
						<tr><td colspan="3" align="center"><html:submit property="markersButton" value="Submit" onclick="return sub('Get Markers')"/> </td></tr>
				 	</table>
				 	<br>	
				 <%}
				if(request.getQueryString().equals("mupl")){
				%>	
				<br><br>
					<table width=45% align="center" border=0>
						<tr><th>Upload the text file with desired markers</th><td width="5%" align="left">:</td><td align="left"><html:file property="txtNameL"/></td></tr>
						<tr><td>&nbsp;</td></tr>
						<tr><td align="center" colspan="3"><a href="<%=request.getContextPath()%>/jsp/dataretrieve/Markers.txt" target="new">Sample File</a></td></tr>
						
						<tr><td colspan="3" align="center"><html:submit property="linesButton" value="Submit" onclick="return sub('Get Lines')"/></td></tr>
				 	</table>
				 	<br>
				<%}
				if(request.getQueryString().equals("dset")){
				%>
				<br><br>
				 	<center>	 	
						<table width=50% align="center" border=0>
							<tr class="displayText"><td align="left" width="25%">Select the Dataset</td><td width="5%" align="left">:</td><td align="left">
								<html:select property="dataset" onchange='retrieveSize(this.options[this.selectedIndex].value, this.name)'>
									<html:option value=""/>
									<logic:iterate name="dataSetList" id="dataset" type="java.lang.String">
									<html:option value="<%=dataset %>" />
									</logic:iterate>					
								</html:select>
							</td></tr>
					 	</table>
					 	<br>
					 	<table border=0 align=center width=50%><tr><td class="displayBoldText" align=left>Choose Data Export Format You Would Like to View</td></tr></table>
						   	 <table align="center" width=50% border=0>
						    	 <tr>
							    	 <td width=1%><html:radio  property="FormatcheckGroup" value="Genotyping X Marker Matrix" onclick="selOpt1(this)"/></td>
							    	 <td class="QrytextColor" align=left>Genotyping X Marker Matrix</td>
						    	 </tr>
						    	 					    	 
							     <tr>
							    	 <td width=1%><html:radio property="FormatcheckGroup" value="Flapjack" onclick="selOpt1(this)"/></td>
							    	 <td class="QrytextColor" align="left">Flapjack</td>
							     </tr>   
							    
						    	 <tr align="left">
							    	 <td colspan=2>
							    	<%
							    	ArrayList maps=(ArrayList)request.getSession().getAttribute("mapList");	    	
							    	
							    	%>
							    	 <span id="map" style="visibility: hidden;">
									 	
											<table width=100% align="left" border=0>
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
														<th width="40%" colspan="3" align="center"><font color="red">NO Maps!!!</font> <font color="black">Please upload Map data to create Export formats for Flapjack</font></th>
													<%} %>
												</tr>
										 	</table>									 	
										
									</span>
							    	 </td>
						    	 </tr> 	 
								</table><br>
							</tr>
						</table></td>
					</tr>
					</table>
					 	<br>	
					 	<html:hidden property="opType" value="dataset"/> 	
					 	<html:button property="export" value="Next" onclick="subExport(this)"/>	 	
					</center>
				</span>		
				
				
				<%} %>
				<html:hidden property="op"/>
				<html:hidden property="retrieveOP"/>
			</center>
		</html:form>
	</body>
</html:html>
<script language="javascript">
var httpRequest;
function retrieveSize(selecteddataset,textboxname){
	//alert(selecteddataset)
	if(selecteddataset !=""){
		var url='exportCheck.do?ChkDataSets='+encodeURIComponent(selecteddataset);
		if (window.ActiveXObject){ 
			httpRequest = new ActiveXObject("Microsoft.XMLHTTP"); 
		}else if (window.XMLHttpRequest){ 
			httpRequest = new XMLHttpRequest(); 
		} 
		httpRequest.open("GET", url , true); 
		//httpRequest.onreadystatechange = function() { processRequest(textboxname); } ;
		httpRequest.send(null);
	}
}
/** 
	* This is the call back method 
	* If the call is completed when the readyState is 4 
	* and if the HTTP is successfull when the status is 200 
	* update the profileSection DIV 
*/
function processRequest(textboxname){
	if (httpRequest.readyState == 4) {
		if (httpRequest.status == 200) {       
			// AJax Plain Text Response
			var msgTEXT=httpRequest.responseText;
			alert(msgTEXT);
			if(msgTEXT ==1){       		
				alert('<%=request.getSession().getAttribute("size")%>');       		 	
				var num=noofPlates;
				var tempplate="";
				/*for(i=0;i<num;i++){
					tempplate=document.frmex.elements["psid0"+ i].name;
					if(textboxname == tempplate){
						//document.frmex.elements["psid0"+ i].value="";
						document.frmex.elements["psid0"+ i].focus();
					}
				}*/
			}       		     		  
		}
	}      
}
function retrieveOPData(a){
	//alert(a.value);
	if(a.value=="QTL/Map"){
		document.forms[0].elements["op"].value=a.value;	
		//document.getElementById('QTLSpan').style.visibility='visible';
		document.forms[0].elements["retrieveOP"].value="first";
		document.forms[0].action="dataretrieval.do";
		document.forms[0].submit();
	}else if(a.value=="polymorphic"){
		document.forms[0].elements["op"].value=a.value;	
		document.forms[0].action="genotypingpage.do?poly";		
		document.forms[0].submit();
	}else if(a.value=="Output"){		
		document.forms[0].action="genotypingpage.do?out";		
		document.forms[0].submit();
	}		
}
function sub(s){
	//alert(s);
	var selValue="";
	var obj1;
	//alert(s);		
	if(s=="qtl"){	
		//alert(document.forms[0].opType.value);
		//var retT=document.forms[0].retType.value;
		if(document.forms[0].maps.value=="QTLName" && document.forms[0].qtl.value==""){			
			alert("Please provide the QTL name");
			return false;
		}else if(document.forms[0].maps.value=="Trait" && document.forms[0].qtl.value==""){
			alert("Please provide the trait");
			return false;
		}else if(document.forms[0].maps.value=="maps" && document.forms[0].qtl.value==""){
			alert("Please provide the Map name");
			return false;
		}
		//alert(document.forms[0].maps.value);
		document.forms[0].elements["retType"].value=document.forms[0].maps.value;	
		/*if((retT=="")){
			//alert("Select option QTL name/Trait name");
			alert("Please select any of the options");
			return false;
		}
		if(retT=="qtlData" || retT=="trait"){
			for(i=0;i<document.forms[0].elements.length;i++){
				obj1=document.forms[0].elements[i];
				if(obj1.type=="checkbox" && obj1.checked){
					 selValue=obj1.value;
					 break;
				}
			}
			//alert("selValue;"+selValue);
			
			//alert(selValue);
			if(document.forms[0].elements['qtl'].value==""){
				if(selValue=="QTLName")
					alert("Please Enter QTL Name");
				else if(selValue=="Trait")
					alert("Please Enter Trait Name");
				return false;
				document.forms[0].elements['qtl'].value="";
				document.forms[0].elements['qtl'].focus();
				//document.forms[0].elements['qtl'].focus();
			}
		}*/
		
		document.forms[0].elements["retrieveOP"].value="GetInfo";	
	}else if(s=="lines"){		
		if(document.forms[0].linesO.value==document.forms[0].linesT.value){
			alert("The selected lines should be different");
			return false;
		}
		document.forms[0].elements["retrieveOP"].value="Submit";	
	}else if(s=="Get Markers"){
		if(document.forms[0].txtNameM.value.indexOf(".txt")== -1){
		 	alert("Check the file, it has to be in text format");		 	
		 	return false;		
		 }
		
		document.forms[0].elements["retrieveOP"].value=s;
	}else if(s=="Get Lines"){
		if(document.forms[0].txtNameL.value.indexOf(".txt")== -1){
		 	alert("Check the file, it has to be in text format");
		 	return false;		
		 }
		document.forms[0].elements["retrieveOP"].value=s;
	}
	
}

function selOpt(opt){
	
	var check=opt.value;			
	if(check=="gids"){			
		document.forms[0].action="genotypingpage.do?gidsDirecting";
		document.forms[0].submit();		
		
	}else if(check=="markers"){
		document.forms[0].action="genotypingpage.do?markersDirecting";
		document.forms[0].submit();
		
	}else if(check=="dataset"){
		document.forms[0].action="genotypingpage.do?datasetRet";
		document.forms[0].submit();
		
	}	
}

function selOpt1(opt){
	var check=opt.value;	
	if(check=="Flapjack"){				
		document.getElementById('map').style.visibility='visible';			
	}else{			
		document.getElementById('map').style.visibility='hidden';
	}	
}

/*function subExport(){
	document.forms[0].action="export.do";	
}*/
function subExport(){
	//alert(document.forms[0].elements['dataset'].value);
	if(document.forms[0].elements['dataset'].value==""){
		alert("Please select the dataset");		
		return false;
		document.forms[0].elements['dataset'].focus();
	}
	var msg;
	var size='<%=session.getAttribute("size")%>';
	//alert(size);
	msg= "   Downloading through dataset would take time. \n          Do you want to continue? ";
	var agree=confirm(msg);
	//alert("");
	if (agree){	
		document.forms[0].action="exportStatus.do";
		document.forms[0].submit();	
	}else{
		return false;
	}
}
function onClickOption(a){	
	//alert(a);
	document.forms[0].elements["retType"].value=a;	
}


function errorCountFunc(){	
	var countn=0;
	var obj;
	for(var i=0;i<document.forms[0].elements.length; i++){
		obj=document.forms[0].elements[i];		
		if (obj.type=="checkbox" && obj.checked) 
			countn++;	   
	
		
		if(countn>1){
			alert("Not Allowed");
			obj.checked=false;
			return false;
		}
	}
	
}

</script>