<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<html:html>
<head>
<title>GDMS</title>
<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">
<script>
function chkFile(){
	var selValue="";
	if (document.forms[0].fileuploads.value == ""){
		alert("Upload the Template file");
		document.forms[0].fileuploads.focus();
		return false;
	}
		
	for(i=0;i<document.forms[0].radios.length;i++)
		if(document.forms[0].radios[i].checked==true){
			//alert(document.forms[0].radios[i].value);
			if((document.forms[0].radios[i].value=="SSRGenotype")||(document.forms[0].radios[i].value=="DArt")||(document.forms[0].radios[i].value=="Mapping")||(document.forms[0].radios[i].value=="QTL")||(document.forms[0].radios[i].value=="SSRMarker")||(document.forms[0].radios[i].value=="SNPMarker")){
			 	 if(document.forms[0].fileuploads.value.indexOf(".xls")== -1){
				 	alert("Check the input file, it has to be in excel format");
				 	document.forms[0].fileuploads.value="";		 	
				 	document.forms[0].fileuploads.focus();
				 	return false;	
				 }
			}else if(document.forms[0].radios[i].value=="SNPGenotype"){
				if(document.forms[0].fileuploads.value.indexOf(".txt")== -1){
				 	alert("Check the input file, it has to be in tab delimited text format");
				 	document.forms[0].fileuploads.value="";		 	
				 	document.forms[0].fileuploads.focus();
				 	return false;		
				 }
			}
			 selValue=document.forms[0].radios[i].value;
			 break;
		 }
		 
		
		if(selValue==""){
			alert("Please select upload template type");
			return false;
		}
	}
function msg(){
	<% //System.out.println("test"+request.getQueryString());
	String strValue = "";
	String strValue1 = "";
	String strValue2 = "";
	String strConc = "";
	String strColPosi = "";
	String strResult = request.getQueryString();
	if(strResult == null)
		strResult = "";


		
	//Session time out message
	if(strResult.equals("SeTimeOut")){
		strResult="You have to re-login to upload the template.";
	}

	//Common messages for all templates

	/////Message will be displayed when the sheet name not found in the uploaded template.
	if(strResult.equals("SheetNameNotFound"))
		strResult = "Error : Required sheet name(s) not found. Please verify with the sample template.";

	/////Message will be displayed when the column name should not be ordered as sample template.
	if(strResult.equals("ColumnNameNotFound")){
		strResult = "Error : Column Name should be ";
		strValue = (String) session.getAttribute("colMsg");
	    strValue1 = (String) session.getAttribute("colMsg1");
	    strValue2 = " in "+(String) session.getAttribute("sheetName") +" sheet";
	    
	    strColPosi=(String) session.getAttribute("colposition");
	    String strCP = "";
	    if(strColPosi!=null)
	    		strCP = ".\n            Please delete if not required.\n            The row position is "+strColPosi +".";
	    		
	    
	    strConc = strValue1 + " not " + strValue + strValue2 + strCP;
	}

	

	if(strResult.equals("ReqFields")){
		strValue = (String) session.getAttribute("fieldName");
		strValue1 = (String) session.getAttribute("colposition");
		strValue2 = (String) session.getAttribute("sheetName") +" sheet";
		strResult = "Error : "+strValue +" value should not be empty in the "+strValue2 +".\n            The column/row position is "+strValue1 +".";
		
	}

	///all the individual messages will be displayed.
	if(strResult.equals("ErrMsg")){
		strValue = (String)	session.getAttribute("indErrMsg");
		strResult = "Error : "+strValue +".";
	}
	//Fields accepts only numberic values.
	if(strResult.equals("NumericValue")){
		
		strResult = "";
		strValue = (String) session.getAttribute("fieldName");
	    strValue1 = (String) session.getAttribute("sheetName");

	    
	    strConc = "The "+strValue + " column name accepts only numeric values (or) two special characters (?,-) in " + strValue1 + " sheet";
	}

	//Field length messages.
	if(strResult.equals("FieldLength")){
		strValue = (String) session.getAttribute("fieldName");
		strValue1 = (String) session.getAttribute("sheetName");
	strResult = strValue +" value should not exceed 75 characters in "+strValue1 +" sheet.";
	}
	//Field name messages.
	if(strResult.equals("FieldName")){
		strValue = (String) session.getAttribute("sheetName");
		strValue1 = (String) session.getAttribute("colposition");
		strResult = "Error : Column name should not be empty in " + strValue + " sheet.\n            Please delete if not required.\n            The cell position is "+strValue1+".";
		
	}
	///message will be displayed when user uploaded filed size is more than datatype size.
	if(strResult.equals("ColLengthError")){
		strValue = (String) session.getAttribute("dlength");
		strValue1 = (String) session.getAttribute("sheetName");
		strValue2 = (String) session.getAttribute("colposition");
		strResult = "Column length should not exceed more than " + strValue + " in " + strValue1 + " sheet.\n The column position is "+strValue2 +".";

	}
	
	if(strResult.equals("infoRequired")){
		//strValue1 = (String) session.getAttribute("colpositionNull");
		strValue2 = (String) session.getAttribute("sheetNameNull") +" sheet.";
		strResult = "Error : Please provide map info in " + strValue2;
	}

	

	//Message for empty rows deletion in SSR_Genotyping Template
	if(strResult.equals("DelEmptyRows")){
		strValue1 = (String) session.getAttribute("colposition");
		strValue2 = (String) session.getAttribute("sheetName") +" sheet.";
		strResult = "Error : Please delete empty row in " + strValue2 +".\nThe row position is "+strValue1+".";
	}
	//Message for empty cols deletion in SSR_Genotyping Template
	if(strResult.equals("DelEmptyColumns")){
		strValue1 = (String) session.getAttribute("colposition");
		strValue2 = (String) session.getAttribute("sheetName") +" sheet.";
		strResult = "Error : Please delete empty column in " + strValue2 +".\nThe column position is "+strValue1+".";
	}
	//insertion messages
	if (strResult.equals("inserted")){
		strResult = "Data has been inserted into the database.";
		
	}
	if(strResult.equals("error"))
		strResult = "Found Errors while uploading the Excel Sheet";


	if(strConc.equals(" not "))
		strConc = "";

	//eeeeeeeeeeeeeeeeeee


	%>
	if(document.forms[0].elements['hResult'].value != "")
	alert(document.forms[0].elements['hResult'].value)

	//go to login page when default session timeout is over.

	if(document.forms[0].elements['hResult'].value=="You have to re-login to upload the template."){
		//document.forms[0].action="Login.jsp";
		document.forms[0].submit();
	}
	<%
	//remove the content from session variables   	
	session.removeAttribute("colMsg");
	session.removeAttribute("colMsg1");
	session.removeAttribute("sheetName");
	session.removeAttribute("dlength");
	session.removeAttribute("colposition");
	session.removeAttribute("fieldName");
	session.removeAttribute("indErrMsg");
	%>
	}

</script>
</head>
<body onload="msg()">
<html:form method="post" action="/dataupload.do"  enctype="multipart/form-data">
<logic:notEmpty name="user">
	<div class="heading" align="center">Data Uploading</div>
	<center>
	<br>
	<html:errors/>
	<br>
	
	<div align="center" class="displayText">(Data can be uploaded using provided templates.<br> To upload, select button, browse & upload template containing data.)</div>
	<br>
	<div align="center" class="displayText">Please upload Marker Information before uploading Genotyping Data</div>
	<br><br>
	<table border=0 cellpadding=3 cellspacing=1 width="70%"  bgcolor="white">		
		<tr><td width="10%" align=right class="displayText"><html:radio property="radios" value="SSRMarker"/></td><td width="15%" class="displayText">SSR Marker</td><td width="35%" class="displayText"><a href="<%=request.getContextPath()%>/jsp/dataupload/SSR_Marker.xls" target="new">SSR Marker Sample Template</a>&nbsp;</td></tr>
		<tr><td width="10%" align=right class="displayText"><html:radio property="radios" value="SNPMarker"/></td><td width="15%" class="displayText">SNP Marker</td><td width="35%" class="displayText"><a href="<%=request.getContextPath()%>/jsp/dataupload/SNP_Marker.xls" target="new">SNP Marker Sample Template</a>&nbsp;</td></tr>
	
		<tr><td width="10%" align=right class="displayText"><html:radio property="radios" value="SSRGenotype"/></td><td width="15%" class="displayText">SSR Genotype</td><td width="35%" class="displayText"><a href="<%=request.getContextPath()%>/jsp/dataupload/SSR_GenotypingTemplate.xls" target="new">SSR Genotype Sample Template</a></td></tr>		
		<tr><td width="10%" align=right class="displayText"><html:radio property="radios" value="SNPGenotype"/></td><td width="15%" class="displayText">SNP Genotype</td><td width="35%" class="displayText"><a href="<%=request.getContextPath()%>/jsp/dataupload/SNPGenotypingTemplate.txt" target="new">SNP Genotype Sample Template</a></td></tr>
		<tr><td width="10%" align=right class="displayText"><html:radio property="radios" value="DArT"/></td><td width="15%" class="displayText">DArT Genotype</td><td width="35%" class="displayText"><a href="<%=request.getContextPath()%>/jsp/dataupload/DArTGenotypingTemplate.xls" target="new">DArT Genotype Sample Template</a></td></tr>
		<tr><td width="10%" align=right class="displayText"><html:radio property="radios" value="Mapping"/></td><td width="15%" class="displayText">Map</td><td width="35%" class="displayText"><a href="<%=request.getContextPath()%>/jsp/dataupload/MapTemplate.xls" target="new">Map Sample Template</a></td></tr>		
		<tr><td width="10%" align=right class="displayText"><html:radio property="radios" value="QTL"/></td><td width="15%" class="displayText">QTL</td><td width="35%" class="displayText"><a href="<%=request.getContextPath()%>/jsp/dataupload/QTLTemplate.xls" target="new">QTL Sample Template</a></td></tr>		
		
		<tr><td colspan=3>&nbsp;</td></tr>
		<tr><td colspan=3 align="center"><html:file property="fileuploads"/></td></tr>
		<tr><td colspan=3>&nbsp;</td></tr>
		<tr><td>&nbsp;</td><td>&nbsp;</td><td><html:submit property="dupload" onclick="return chkFile()"/></td></tr>
	
	</table>
	<input type=hidden name="hResult" value='<%=strResult %><%=strConc %>'>
	</center>
</logic:notEmpty>
<logic:empty name="user">
	<br><br><br>
	<center><font color="blue" face="verdana" size="3px">Please Login to upload/retrieve data</font></center>
</logic:empty>
</html:form>
</body>
</html:html>