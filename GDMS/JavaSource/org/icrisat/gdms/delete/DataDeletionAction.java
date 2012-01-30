package org.icrisat.gdms.delete;

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
import org.apache.struts.action.DynaActionForm;

public class DataDeletionAction extends Action{

	Connection con=null;
	String str="";
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		DynaActionForm df = (DynaActionForm) af;	
		String delData=df.get("getOp").toString();
		System.out.println(delData);
		try{
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/icis","root","root");
			ResultSet rs=null;
			ResultSet rs1=null;
			Statement stmt=con.createStatement();
			Statement stmtR=con.createStatement();
			Statement st=con.createStatement();
			Statement stR=con.createStatement();
			Statement stD=con.createStatement();
			Statement stDa=con.createStatement();
			
		
			int datasetID=0;
			String datasetType="";
			String qtls="";
		
			String[] strArr1=delData.split(";;");
			for(int i=0;i<strArr1.length;i++){
			
				if(!(strArr1[0]).equals("")){
					//System.out.println("&&&&&&&&&&&&&");
					String[] strArr2=strArr1[0].split("!~!");
					for(int d=0;d<strArr2.length;d++){
						rs=stmt.executeQuery("select dataset_id, dataset_type from dataset where dataset_desc='"+strArr2[d]+"'");
						while(rs.next()){
							datasetID=rs.getInt(1);
							datasetType=rs.getString(2);
						}
						if(datasetType.equalsIgnoreCase("SNP")){
							int del=stmtR.executeUpdate("delete from char_values where dataset_id='"+datasetID+"'");						
						}else if(datasetType.equalsIgnoreCase("SSR")){
							int del=stmtR.executeUpdate("delete from allele_values where dataset_id='"+datasetID+"'");							
						}else if(datasetType.equalsIgnoreCase("DArT")){
							int del=stmtR.executeUpdate("delete from allele_values where dataset_id='"+datasetID+"'");
							int delDA=stmtR.executeUpdate("delete from dart_details where dataset_id='"+datasetID+"'");						
						}
						int del1=st.executeUpdate("delete from dataset_users where dataset_id='"+datasetID+"'");
						int del2=stR.executeUpdate("delete from dataset_details where dataset_id='"+datasetID+"'");
						int del3=stD.executeUpdate("delete from dataset where dataset_id='"+datasetID+"'");	
					}
				}
				if(!(strArr1[1]).equals("")){
					//System.out.println(".............");
					String[] strArr2=strArr1[1].split("!~!");
					for(int d=0;d<strArr2.length;d++){
						rs=stmt.executeQuery("select dataset_id from dataset where dataset_desc='"+strArr2[d]+"'");
						while(rs.next()){
							datasetID=rs.getInt(1);							
						}
						rs1=st.executeQuery("select qtl_id from qtl where dataset_id='"+datasetID+"'");
						while(rs1.next()){
							qtls=qtls+rs1.getInt(1)+";;";						
						}
						String[] qtl=qtls.split(";;");
						for(int q=0;q<qtl.length;q++){
							int del=stmtR.executeUpdate("delete from qtl_linkagemap where qtl_id='"+qtl[q]+"'");
							int delDA=stmtR.executeUpdate("delete from qtl where qtl_id='"+qtl[q]+"'");
						}
						int del1=st.executeUpdate("delete from dataset_users where dataset_id='"+datasetID+"'");
						int del2=stR.executeUpdate("delete from dataset_details where dataset_id='"+datasetID+"'");
						int del3=stD.executeUpdate("delete from dataset where dataset_id='"+datasetID+"'");	
					}
				}
				if(!(strArr1[2]).equals("")){
					//System.out.println("....__________**********__________....");
					String[] strArr2=strArr1[2].split("!~!");
					for(int d=0;d<strArr2.length;d++){
						rs=stmt.executeQuery("select linkagemap_id from linkagemap where linkagemap_name='"+strArr2[d]+"'");
						while(rs.next()){
							datasetID=rs.getInt(1);						
						}
						int del=stmtR.executeUpdate("delete from marker_linkagemap where linkagemap_id='"+datasetID+"'");
						int delDA=stmtR.executeUpdate("delete from linkagemap where linkagemap_id='"+datasetID+"'");	
					}
				}
			}
			str="delete";
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		      try{		      		
		      		if(con!=null) con.close();
		         }catch(Exception e){System.out.println(e);}
			}
		
		
		return am.findForward(str);
	}
	

}
