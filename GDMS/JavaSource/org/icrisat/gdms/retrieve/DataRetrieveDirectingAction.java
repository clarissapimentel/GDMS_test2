package org.icrisat.gdms.retrieve;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.common.HibernateSessionFactory;

public class DataRetrieveDirectingAction extends Action{
	private Session hsession;	
	private Transaction tx;
	List listValues=null;
	List list1=null;
	Query query=null;
	Query query1=null;
	Query query2=null;
	String str="";
	String crop="";
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		HttpSession session = req.getSession(true);  
		
		if(session!=null){
			session.removeAttribute("listValues");			
		}
		
		//System.out.println("**********************************"+req.getQueryString());
		String op=req.getQueryString();
		
		hsession = HibernateSessionFactory.currentSession();
		tx=hsession.beginTransaction();
		DynaActionForm df = (DynaActionForm) af;
		Connection con=null;
		ResultSet rs=null;
		ResultSet rs1=null;
		ResultSet rsQTL=null;
		ResultSet rsDS=null;
		ResultSet rsMap=null;
		ArrayList qtlList=new ArrayList();
		ArrayList gids =new ArrayList();
		int count=0;
		try{
			
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/icis","root","root");
			String datasetId="";
			Statement stmt=con.createStatement();
			Statement stmt1=con.createStatement();
			Statement stmtR=con.createStatement();
			if(op.equalsIgnoreCase("first")){
				/** retieving crops from the database 
				 * this is executed when the genotyping data option is selected		
				 * under retrievals  
				 *  **/
				query=hsession.createQuery("select distinct species from DatasetBean ORDER BY species asc");
							
				listValues=query.list();
				//itList=listValues.iterator();
				session.setAttribute("listValues", listValues);
				System.out.println(".................... "+listValues);
				str="retSpecies";
			}else if(op.equalsIgnoreCase("second")){
				crop=(String)df.get("crop");
				//System.out.println("Crop ="+crop);
				session.setAttribute("crop", crop);
				str="directing";
			}else if(op.equalsIgnoreCase("poly")){
				/** retrieving germplasm names based on crop **/
				crop=session.getAttribute("crop").toString();
				System.out.println("select dataset_id, dataset_type from dataset where lower(species) ='"+crop.toLowerCase()+"' AND dataset_type != 'QTL' order BY dataset_id");
				rs=stmt.executeQuery("select dataset_id, dataset_type from dataset where lower(species) ='"+crop.toLowerCase()+"' AND dataset_type != 'QTL' order BY dataset_id");
				while (rs.next()){
					datasetId=datasetId+rs.getString(1)+"!~!"+rs.getString(2)+",";
					count=count+1;
				}
				if(count==0){
					/*ae.add("myerrors", new ActionError("qtl.doesnot.exists"));
					saveErrors(req, ae);				
					//msg="chkPlateid";
					return (new ActionForward(am.getInput()));*/
					String ErrMsg = "Genotyping data not submitted";
					req.getSession().setAttribute("indErrMsg", ErrMsg);
					return am.findForward("ErrMsg");
				}
				System.out.println("datasetId="+datasetId);
				String[] dlen=datasetId.split(",");
				String gidString="";
				//System.out.println("dType="+dType+"   dlen="+dlen.length);
				String germplasmName="";
				ArrayList germName=new ArrayList();
				for(int i=0;i<dlen.length;i++){
					String[] dId=dlen[i].split("!~!");					
					if((dId[1].equalsIgnoreCase("SSR"))||dId[1].equalsIgnoreCase("DArT")){
						//System.out.println("IF ");
						ResultSet rs2=stmt1.executeQuery("SELECT DISTINCT gid FROM allele_values WHERE dataset_id="+dId[0]+"");
						while (rs2.next()){
							gidString=gidString+rs2.getString(1)+",";
						}						
					}else if(dId[1].equalsIgnoreCase("SNP")){
						//System.out.println("ELSE IF ");
						ResultSet rs2=stmt1.executeQuery("SELECT DISTINCT gid FROM char_values WHERE dataset_id="+dId[0]+"");
						while (rs2.next()){
							gidString=gidString+rs2.getString(1)+",";
						}						
					}
					//System.out.println("SELECT distinct germplasm_name FROM germplasm_temp WHERE gid IN("+gidString.substring(0,gidString.length()-1)+") ORDER BY germplasm_name");
					//rs1=stmtR.executeQuery("SELECT distinct germplasm_name FROM germplasm_temp WHERE gid IN("+gidString.substring(0,gidString.length()-1)+") ORDER BY germplasm_name");
					rs1=stmtR.executeQuery("SELECT distinct nval from names where gid IN("+gidString.substring(0,gidString.length()-1)+") ORDER BY nval");
					while(rs1.next()){
						germplasmName=germplasmName+rs1.getString(1)+",";
						if(!(germName.contains(rs1.getString(1))))
							germName.add(rs1.getString(1));
					}					
				}
				//System.out.println("germplasmName="+germplasmName);
				session.setAttribute("listValues", germName);
				str="retLines";
			}else if(op.equalsIgnoreCase("out")){
				str="genoOut";
			}else if(op.equalsIgnoreCase("gidsDirecting")){
				str="gdir";
				
			}else if(op.equalsIgnoreCase("markersDirecting")){
				str="mdir";
			}else if(op.equalsIgnoreCase("datasetRet")){
				//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				/** retrieving datasets based on crop **/
				ArrayList mapList=new ArrayList();
				ArrayList dataSetList=new ArrayList();
				Statement stmtDS=con.createStatement();
				Statement stmtMap=con.createStatement();
				
				crop=session.getAttribute("crop").toString();
				System.out.println("select dataset_desc from dataset where species='"+crop+"' and dataset_type !='QTL'");
				rsDS=stmtDS.executeQuery("select dataset_desc from dataset where species='"+crop+"' and dataset_type !='QTL'");
				while(rsDS.next()){
					dataSetList.add(rsDS.getString(1));
				}
				//System.out.println("dataSetList="+dataSetList);
				rsMap=stmtMap.executeQuery("select distinct linkagemap_name from linkagemap");
				while(rsMap.next()){
					mapList.add(rsMap.getString(1));
				}
				//System.out.println("mapList="+mapList);
				session.setAttribute("dataSetList", dataSetList);				
				session.setAttribute("mapList", mapList);
				
				str="retDataset";
			
			}else{
				if(session!=null){
					session.removeAttribute("indErrMsg");		
				}
				
				String dType="";
				
				Statement stmt2=con.createStatement();
				
				
				rsQTL=stmt2.executeQuery("select qtl_name from qtl");
				while(rsQTL.next()){
					qtlList.add(rsQTL.getString(1));					
				}
				//System.out.println("qtlList="+qtlList);
				
				/**for retrieving the size of dataset**/
				//rsS=stS."SELECT UPPER(char_values.dataset_id) AS id, COUNT(DISTINCT(char_values.marker_id)) AS Markercount, COUNT(DISTINCT(char_values.gid)) AS Gcount FROM char_values GROUP BY UPPER(char_values.dataset_id)");
				
				//System.out.println("QTL LIST ="+qtlList);
				
				//query=hsession.createQuery("select distinct species from DatasetBean ORDER BY species asc");
				//query1=hsession.createQuery("select distinct germplasm_name from RetrievalMarkers WHERE lower(species) ='"+crop.toLowerCase()+"'order BY germplasm_name");
				//listValues=query1.list();
				//itList=listValues.iterator();
				session.setAttribute("qtlList", qtlList);
				
				//System.out.println(".................... "+listValues);
				
				str="retLines";
				//System.out.println("*****************END*******************");
			}
		}catch(Exception e){
			e.printStackTrace();
			hsession.clear();		
			tx.rollback();
		}finally{
			hsession.clear();
		}
		return am.findForward(str);
	}
}
