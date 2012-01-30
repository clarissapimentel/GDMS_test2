package org.icrisat.gdms.retrieve;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Query;

public class RetrieveMarkerInfoAction extends Action{
	Connection con=null;
	ResultSet rs=null;
	Statement st=null;
	String str1="";
	Query query=null;
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		//System.out.println("test");
		String DRIVER ="com.mysql.jdbc.Driver";
		 //loading driver
		 Class.forName(DRIVER).newInstance();
		 //String url="jdbc:mysql://10.4.19.64/icrisat_markers?user=root&password=";
		 //databse connection string
		 String url="jdbc:mysql://localhost:3306/icis?user=root&password=root";
		 //establishing connection
		 con=DriverManager.getConnection(url);
		
		PrintWriter pr = res.getWriter();
		res.setContentType("text/xml");
		res.setHeader("Cache-Control", "no-cache");
		String value = req.getParameter("data");
		String type = req.getParameter("type");
		//System.out.println("value == "+value+"  type=="+type);
		String sqlQuery = "";
		try{
		if (value.equals("Marker_Type")) {
			if (type.equals("")) {
				sqlQuery = "Select Distinct Marker_Type from marker";
			} else {
				//Query = "Select Marker_Name from marker_info inner Join marker_type on Marker_Type.marker_type_id=marker_info.marker_type_id where Marker_Type.Marker_Type='"	+ type + "'";
				sqlQuery = "Select Marker_Name from marker where Marker_Type='"+ type + "'";
			}
		} else {
			if (type.equals("")) {
				sqlQuery = "Select Distinct(" + value + ") from marker";
			} else {
				sqlQuery = "Select Marker_Name from marker where "
						+ value + "='" + type + "'";
			}
		}
		//System.out.println("query="+sqlQuery);
		st = con.createStatement();
		//resultset object 
		rs = st.executeQuery(sqlQuery);
		//query=hsession.createQuery(sqlQuery);
		pr.println("<data>");
		pr.println("<details><![CDATA[- Select -]]></details>");
		while (rs.next()) {
			String str = rs.getString(1);			
			pr.println("<details><![CDATA[" + str + "]]></details>");
			
		}
		pr.println("</data>");	 
		
		return null;
	}catch(Exception e){
		e.printStackTrace();
		//hsession.clear();		
		//tx.rollback();
	}finally{
		try{
			//hsession.clear();
			//closing open connection, Resultset, Statement objects.
			if(con!=null)con.close();
			if(st!=null)st.close();
			if(rs!=null)rs.close();
		}catch(Exception e){
			System.out.println("Exception :"+e);
		}
	}
		str1="return";
		
		return am.findForward(str1);
	}
	
	
	

}
