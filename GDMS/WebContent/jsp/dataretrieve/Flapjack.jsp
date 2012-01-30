<%@ page language="java" %>

 
<html>
<head><title>GDMS- Running Batch file</title></head>
<%
	try{
				
	System.out.println("Str value="+request.getParameter("str"));
	String batchType=request.getParameter("str");
		String realPath=getServletContext().getRealPath("\\"); 
		String batchFileName="";
		
		if(batchType.equals("View in CMTV")){
			batchFileName=realPath+"jsp\\dataretrieve\\cmtvrun.bat";
			System.out.println("batch file path/...."+batchFileName);

			String[] cmd = {"cmd.exe", "/c", "start", "\""+"cmtv"+"\"", batchFileName};
			Runtime rt = Runtime.getRuntime();
			rt.exec(cmd);
			
		}else if(batchType.equals("Run Flapjack")){
			batchFileName=realPath+"jsp\\dataretrieve\\flapjackrun.bat";
			System.out.println("batch file path/...."+batchFileName);

			String[] cmd = {"cmd.exe", "/c", "start", "\""+"flapjack"+"\"", batchFileName};
			Runtime rt = Runtime.getRuntime();
			rt.exec(cmd);
		} 
	
%>
<body>
	<br>
	<center>
	<%if(batchType.equals("View in CMTV")){ %>
		<input type="button" name="Back" value=" Back " onclick="javascript:history.back()"/>
	<%}else{ %>
	 	<a href="FViewFiles.jsp">download</a>
	<%} %>
	</center>
	
	<br><br><br>
	<form name='BackFrm'> 
	</form>
</body>
<%}catch(Exception e){
	e.printStackTrace();
} %>
</html>

