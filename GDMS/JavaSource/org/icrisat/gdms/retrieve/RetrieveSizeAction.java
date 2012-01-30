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

public class RetrieveSizeAction extends Action{
	Connection con=null;
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		try{

			ResultSet rs=null;
			
			ResultSet rs1=null;
			//ResultSet rs=null;
			int datasetId=0;
			String datasetType="";
			ResultSet rs2=null;
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/icis","root","root");
			Statement stmt=con.createStatement();
			Statement stmt1=con.createStatement();
			Statement stmt2=con.createStatement();
			Statement st=con.createStatement();
			String datasetDesc=req.getParameter("ChkDataSets");
			rs=stmt.executeQuery("select dataset_id,dataset_type from dataset where dataset_desc='"+datasetDesc+"'");
			while(rs.next()){
				datasetId=rs.getInt(1);
				datasetType=rs.getString(2);					
			}
			PrintWriter writer=res.getWriter();
			res.setContentType("text/xml");
			res.setHeader("Cache-Control", "no-cache");	
			String size="";
			if(datasetType.equalsIgnoreCase("SNP")){
				System.out.println("SELECT COUNT(DISTINCT char_values.marker_id) AS marker_count, COUNT(DISTINCT char_values.gid) AS gid_count FROM char_values WHERE char_values.dataset_id="+datasetId+" GROUP BY UCASE(char_values.dataset_id)");
				rs1=st.executeQuery("SELECT COUNT(DISTINCT char_values.marker_id) AS marker_count, COUNT(DISTINCT char_values.gid) AS gid_count FROM char_values WHERE char_values.dataset_id="+datasetId+" GROUP BY UCASE(char_values.dataset_id)");
			}else{
				System.out.println("SELECT COUNT(DISTINCT allele_values.marker_id) AS marker_count, COUNT(DISTINCT allele_values.gid) AS gid_count FROM allele_values WHERE allele_values.dataset_id="+datasetId+" GROUP BY UCASE(allele_values.dataset_id)");
				rs1=st.executeQuery("SELECT COUNT(DISTINCT allele_values.marker_id) AS marker_count, COUNT(DISTINCT allele_values.gid) AS gid_count FROM allele_values WHERE allele_values.dataset_id="+datasetId+" GROUP BY UCASE(allele_values.dataset_id)");
			}
			while(rs1.next()){
				size=rs1.getInt(2)+" X "+rs1.getInt(1);
				//System.out.println("Size="+size);
				writer.println("1");
				req.getSession().setAttribute("size", size);
				return null;
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return am.findForward("ret");
	}
	

}
