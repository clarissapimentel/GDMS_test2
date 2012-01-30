/**
 * 
 */
package org.icrisat.gdms.retrieve;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

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
import org.icrisat.gdms.common.ExportFormats;
import org.icrisat.gdms.common.HibernateSessionFactory;
import org.icrisat.gdms.common.MaxIdValue;


public class RetrieveExportFormatAction extends Action{
	Connection con=null;
	String str="";
	String map="";
	String mapData="";
	ArrayList list=new ArrayList();
	ArrayList mlist=new ArrayList();
	private Session hsession;	
	private Transaction tx;
	
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		/*long heapSize = Runtime.getRuntime().totalMemory();
		 
        //Print the jvm heap size.
        System.out.println("Heap Size = " + heapSize);*/
		hsession = HibernateSessionFactory.currentSession();
		tx=hsession.beginTransaction();
		HttpSession session = req.getSession(true);
		/*if(session!=null){
			session.removeAttribute("strdata");
			
		}*/
		MaxIdValue r=new MaxIdValue();
		ArrayList gListExp=new ArrayList();
		ArrayList mListExp=new ArrayList();
		
		
		String chValues="";
		Query charQuery=null;
		String markers="";
		String gids="";
		String gidslist="";
		String datasetDesc="";
		DynaActionForm df = (DynaActionForm) af;
		//System.out.println("********************  "+df.get("opType"));
		String op=df.get("opType").toString();
		req.getSession().setAttribute("op", op);
		//System.out.println(df.get("FormatcheckGroup"));
		String format=df.get("FormatcheckGroup").toString();
		ExportFormats ef=new ExportFormats();
		String markerslist="";
		String accessionslist="";
		String filePath="";
		filePath=req.getSession().getServletContext().getRealPath("//");
		if(!new File(filePath+"/jsp/analysisfiles").exists())
	   		new File(filePath+"/jsp/analysisfiles").mkdir();
		
		//if((format.equalsIgnoreCase("flapjack"))||(format.equalsIgnoreCase("cmtv")))
		if(format.equalsIgnoreCase("flapjack"))
			map=df.get("maps").toString();
		
		req.getSession().setAttribute("exportFormat", format);
		Calendar now = Calendar.getInstance();
		String mSec=now.getTimeInMillis()+"";
		//String fname=filePath+"/jsp/analysisfiles/matrix"+mSec+".xls";
		
		if(session!=null){
			session.removeAttribute("msec");			
		}
		try{

			ResultSet rs=null;
			
			ResultSet rs1=null;
			//ResultSet rs=null;
			
			ResultSet rs2=null;
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/icis","root","root");
			Statement stmt=con.createStatement();
			Statement stmt1=con.createStatement();
			Statement stmt2=con.createStatement();
			Statement st=con.createStatement();
			req.getSession().setAttribute("msec", mSec);
			System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^op="+op);
			if(op.equalsIgnoreCase("dataset")){
				/** retrieving data of the whole dataset for export formats **/
				
				datasetDesc=df.get("dataset").toString();
				int datasetId=0;
				String datasetType="";
			
				//Statement st=con.createStatement();
				list.clear();
				System.out.println("select dataset_id,dataset_type from dataset where dataset_desc='"+datasetDesc+"'");
				rs=stmt.executeQuery("select dataset_id,dataset_type from dataset where dataset_desc='"+datasetDesc+"'");
				while(rs.next()){
					datasetId=rs.getInt(1);
					datasetType=rs.getString(2);					
				}
				
				System.out.println("datasetId="+datasetId+" datasetType ="+datasetType);
				if(datasetType.equalsIgnoreCase("SNP")){
					/*String gid="";
					System.out.println("SELECT gid from char_values where dataset_id="+datasetId);
					rs2=stmt2.executeQuery("SELECT gid from char_values where dataset_id="+datasetId);
					while(rs2.next()){
						gid=gid+","+rs2.getInt(1);
					}
					System.out.println("gid="+gid);
					System.out.println("select germplasm_name from germplasm_temp where gid in "+ gid.substring(0,gid.length()-1));*/
					/*System.out.println("SELECT germplasm_temp.germplasm_name, marker.marker_name, char_values.char_value FROM germplasm_temp JOIN char_values ON germplasm_temp.gid=char_values.gid  JOIN marker ON marker.marker_id=char_values.marker_id WHERE char_values.dataset_id="+datasetId+" ORDER BY char_values.gid DESC, marker.marker_name");
					//rs1=st.executeQuery("SELECT distinct germplasm_temp.germplasm_name, marker.marker_name,char_values.char_value FROM germplasm_temp, char_values, marker WHERE char_values.dataset_id="+datasetId+" AND char_values.gid=germplasm_temp.gid AND char_values.marker_id=marker.marker_id ORDER BY char_values.gid DESC, marker.marker_name");
					rs1=st.executeQuery("SELECT germplasm_temp.germplasm_name, marker.marker_name, char_values.char_value FROM germplasm_temp JOIN char_values ON germplasm_temp.gid=char_values.gid  JOIN marker ON marker.marker_id=char_values.marker_id WHERE char_values.dataset_id="+datasetId+" ORDER BY char_values.gid DESC, marker.marker_name");*/
					System.out.println("SELECT names.nval, marker.marker_name, char_values.char_value FROM names JOIN char_values ON names.gid=char_values.gid  JOIN marker ON marker.marker_id=char_values.marker_id WHERE char_values.dataset_id="+datasetId+" ORDER BY char_values.gid DESC, marker.marker_name");
					//rs1=st.executeQuery("SELECT distinct germplasm_temp.germplasm_name, marker.marker_name,char_values.char_value FROM germplasm_temp, char_values, marker WHERE char_values.dataset_id="+datasetId+" AND char_values.gid=germplasm_temp.gid AND char_values.marker_id=marker.marker_id ORDER BY char_values.gid DESC, marker.marker_name");
					rs1=st.executeQuery("SELECT names.nval, marker.marker_name, char_values.char_value FROM names JOIN char_values ON names.gid=char_values.gid  JOIN marker ON marker.marker_id=char_values.marker_id WHERE char_values.dataset_id="+datasetId+" ORDER BY char_values.gid DESC, marker.marker_name");
				}else if((datasetType.equalsIgnoreCase("SSR"))||(datasetType.equalsIgnoreCase("DArT"))){
					/*System.out.println("SELECT distinct germplasm_temp.germplasm_name, marker.marker_name,allele_values.allele_bin_value FROM germplasm_temp, allele_values, marker WHERE allele_values.dataset_id="+datasetId+" AND allele_values.gid=germplasm_temp.gid AND allele_values.marker_id=marker.marker_id ORDER BY allele_values.gid DESC, marker.marker_name ");
					rs1=st.executeQuery("SELECT distinct germplasm_temp.germplasm_name, marker.marker_name,allele_values.allele_bin_value FROM germplasm_temp, allele_values, marker WHERE allele_values.dataset_id="+datasetId+" AND allele_values.gid=germplasm_temp.gid AND allele_values.marker_id=marker.marker_id ORDER BY allele_values.gid DESC, marker.marker_name ");
					*/
					System.out.println("SELECT distinct names.nval, marker.marker_name,allele_values.allele_bin_value FROM names, allele_values, marker WHERE allele_values.dataset_id="+datasetId+" AND allele_values.gid=names.gid AND allele_values.marker_id=marker.marker_id ORDER BY allele_values.gid DESC, marker.marker_name ");
					rs1=st.executeQuery("SELECT distinct names.nval, marker.marker_name,allele_values.allele_bin_value FROM names, allele_values, marker WHERE allele_values.dataset_id="+datasetId+" AND allele_values.gid=names.gid AND allele_values.marker_id=marker.marker_id ORDER BY allele_values.gid DESC, marker.marker_name ");
				}
				//charQuery=hsession.createSQLQuery("SELECT germplasm_temp.germplasm_name,marker.marker_name,char_values.char_value FROM germplasm_temp, char_values, marker WHERE char_values.dataset_id="+datasetId+" AND char_values.gid=germplasm_temp.gid AND char_values.marker_id=marker.marker_id ORDER BY marker.marker_name, char_values.gid");
				//list=(ArrayList)charQuery.list();
				while(rs1.next()){
					if(!(gListExp.contains(rs1.getString(1))))
						gListExp.add(rs1.getString(1));
					if(!(mListExp.contains(rs1.getString(2))))
						mListExp.add(rs1.getString(2));
					list.add(rs1.getString(1)+","+rs1.getString(2)+","+rs1.getString(3));
					
				}
				System.out.println("****************************************************");
				System.out.println(" mlist   ****************  "+mListExp);
				System.out.println("list.size..."+list.size()+"   mlist.size="+mListExp.size()+"    gList.size="+gListExp.size());
				/*System.out.println("list...."+list);
				System.out.println(" mlist   ****************  "+mListExp);
				System.out.println("  glist  ****************  "+gListExp);*/
				/** retrieving map data for flapjack .map file **/
				if(format.contains("Flapjack")){
					System.out.println("select marker_name, linkage_group, start_position from mapping_data where linkagemap_name in ('"+map+"') order by marker_name");
					rs=stmt.executeQuery("select marker_name, linkage_group, start_position from mapping_data where linkagemap_name in ('"+map+"') order by marker_name");
					while(rs.next()){
						//System.out.println(rs.getString(1)+"   "+rs.getString(2)+"   "+rs.getFloat(3));
						mapData=mapData+rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getFloat(3)+"~~!!~~";
					}					
				}	
				if(format.contains("Genotyping X Marker Matrix")){
					ef.Matrix(list, filePath, req, gListExp, mListExp);
					//ef.Matrix(list, filePath, req);
				}
				
				if(format.contains("Flapjack")){
					ef.MatrixDat(list, mapData, filePath, req, gListExp, mListExp);
					//ef.MatrixDat(list, mapData, filePath, req);
					//ef.Matrix(list, filePath, request);
				}	
				
			}else{
				String gid="";
				String mid="";
				String mlist1="";
				
				int gCount=0;
				int mCount=0;
				ResultSet rsDet=null;
				int count=0;
				list.clear();
				String f1="";
				String type=req.getSession().getAttribute("type").toString();
				System.out.println("type="+req.getSession().getAttribute("type"));
				if(type.equals("map")){
					String[] mapStr=null;
					//session.setAttribute("mapsSess", df.get("selMaps"));
					System.out.println("***********************************  "+df.get("selMaps"));
					mapStr=df.get("selMaps").toString().split(",");
					if(session!=null){
						session.removeAttribute("msec");			
					}
					//float distance=0;
					
					//Calendar now = Calendar.getInstance();
					filePath=req.getSession().getServletContext().getRealPath("//");
					if(!new File(filePath+"/jsp/analysisfiles").exists())
				   		new File(filePath+"/jsp/analysisfiles").mkdir();
					session.setAttribute("count", (mapStr.length));
					for(int c=0;c<mapStr.length;c++){
						if(session!=null){
							session.removeAttribute("msec");			
						}
						mSec=now.getTimeInMillis()+"";
						//System.out.println("msec="+mSec+c);			
						f1=f1+mapStr[c]+"!~!"+mSec+c+";;";
						ArrayList dist=new ArrayList();
						ArrayList CD=new ArrayList();
						req.getSession().setAttribute("exportFormat","CMTV");
						req.getSession().setAttribute("msec", mSec+c);
						//String filePath="";
						//ExportFormats ef=new ExportFormats();
						//MaxIdValue r=new MaxIdValue();
						
						System.out.println("SELECT marker_name, linkage_group, start_position,linkagemap_name FROM mapping_data WHERE linkagemap_name=("+mapStr[c]+") ORDER BY marker_id, linkage_group AND start_position");
						rs=stmt.executeQuery("SELECT marker_name, linkage_group, start_position,linkagemap_name FROM mapping_data WHERE linkagemap_name =("+mapStr[c]+") ORDER BY marker_id, linkage_group AND start_position");
						while(rs.next()){
							mapData=mapData+rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getFloat(3)+"!~!"+rs.getString(4)+"~~!!~~";
							CD.add(rs.getString(2)+"!~!"+rs.getFloat(3)+"!~!"+rs.getString(1)+"!~!"+rs.getString(4));
							count=count+1;
							//CD.add(rs.getFloat(3));
						}
						/*if(count==0){
							ae.add("myerrors", new ActionError("trait.doesnot.exists"));
							saveErrors(req, ae);				
							//msg="chkPlateid";
							return (new ActionForward(am.getInput()));
							String ErrMsg = "Map Name not found";
							req.getSession().setAttribute("indErrMsg", ErrMsg);
							return am.findForward("ErrMsg");
						}*/
						//System.out.println("CDistance="+CD);
						String[] strArr=CD.get(0).toString().split("!~!");
						float dis=Float.parseFloat(strArr[1]);
						String chr=strArr[0];
						count=0;
						for(int a=0;a<CD.size();a++){
							String[] str1=CD.get(a).toString().split("!~!");						
							if(str1[0].equals(chr)){							
								float distance=Float.parseFloat(str1[1])-dis;							
								dist.add(str1[0]+"!~!"+str1[2]+"!~!"+count+"!~!"+r.roundThree(distance)+"!~!"+str1[1]);
								count=count+1;
								//dis=distance;
								dis=Float.parseFloat(str1[1]);
							}else{	
								count=0;
								//float distance=Float.parseFloat(str1[1])-dis;
								dis=Float.parseFloat(str1[1]);
								chr=str1[0];					
								dist.add(str1[0]+"!~!"+str1[2]+"!~!"+count+"!~!"+dis+"!~!"+str1[1]);
								count=count+1;	
							}
						}
						//System.out.println("..............."+dist);
						ef.CMTVTxt(dist, filePath, req);
						
					}
					System.out.println("f1="+f1);
					session.setAttribute("f1", f1);
				}else{
					if(type.equals("GermplasmName")){
						gidslist=df.get("markersSel").toString();
						markerslist=req.getSession().getAttribute("mnames").toString();
						mlist1=req.getSession().getAttribute("mnames1").toString();
						String[] gList=gidslist.split(";;");
						for(int m=0;m<gList.length;m++){
							gids=gids+gList[m]+",";
						}
						//System.out.println("Marekrs="+markerslist+"    Gids="+gids);
						gCount=gList.length;
						mCount=Integer.parseInt(req.getSession().getAttribute("mCount").toString());
						req.getSession().setAttribute("genCount", gList.length);
						gid=gids.substring(0,gids.length()-1);
						mid=markerslist.substring(0,markerslist.length()-1);
					}else if(type.equals("markers")){				
						markers=df.get("markersSel").toString();
						//System.out.println("*********************************************"+markers);
						gids=req.getSession().getAttribute("gidsN").toString();
						//System.out.println("gid="+gids);
						String[] mList=markers.split(";;");
						for(int m=0;m<mList.length;m++){
							//System.out.println(m+"="+mList[m]);
							mlist1=mlist1+"'"+mList[m]+"',";
							markerslist=markerslist+mList[m]+",";
						}
						//System.out.println("mcount="+mList.length+"&&&&&&&&&&&&&&&&&&&&&&  "+markerslist);
						mCount=mList.length;
						gCount=Integer.parseInt(req.getSession().getAttribute("genCount").toString());
						req.getSession().setAttribute("mCount", mList.length);
						gid=gids.substring(0,gids.length()-1);
						mid=markerslist.substring(0,markerslist.length()-1);
					}
					int alleleCount=0;
					int charCount=0;
				
					try{
						System.out.println("select count(*) from allele_values where gid in ("+gid+")");
						ResultSet rsa=stmt1.executeQuery("select count(*) from allele_values where gid in ("+gid+")");
						while (rsa.next()){
							alleleCount=rsa.getInt(1);
						}
						
						ResultSet rsc=stmt2.executeQuery("select count(*) from char_values where gid in("+gid+")");
						while(rsc.next()){
							charCount=rsc.getInt(1);
						}
					
						if(charCount>0){
							//System.out.println("SELECT DISTINCT char_values.gid,char_values.germplasm_name,char_values.marker_id,char_values.char_value as data,marker.marker_name,marker.marker_type,dataset.species,char_values.dataset_id FROM char_values,marker,dataset WHERE char_values.dataset_id=dataset.dataset_id AND char_values.marker_id=marker.marker_id AND char_values.gid IN ("+gid+") AND char_values.marker_id IN (SELECT marker_id FROM marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY char_values.gid DESC, marker.marker_name"); 
							/*rsDet=st.executeQuery("SELECT DISTINCT char_values.gid,char_values.germplasm_name,char_values.marker_id,char_values.char_value as data,marker.marker_name,marker.marker_type,dataset.species,char_values.dataset_id"+
									" FROM char_values,marker,dataset WHERE char_values.dataset_id=dataset.dataset_id AND char_values.marker_id=marker.marker_id"+
									" AND char_values.gid IN ("+gid+") AND char_values.marker_id IN (SELECT marker_id FROM marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY char_values.gid DESC, marker.marker_name");*/
							rsDet=st.executeQuery("SELECT DISTINCT char_values.gid,names.nval,char_values.marker_id,char_values.char_value as data,marker.marker_name,marker.marker_type,dataset.species,char_values.dataset_id"+
									" FROM char_values,marker,dataset,names WHERE char_values.dataset_id=dataset.dataset_id AND char_values.marker_id=marker.marker_id AND char_values.gid=names.gid"+
									" AND char_values.gid IN ("+gid+") AND char_values.marker_id IN (SELECT marker_id FROM marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY char_values.gid DESC, marker.marker_name");
							while(rsDet.next()){
								if(!(gListExp.contains(rsDet.getString(2))))
									gListExp.add(rsDet.getString(2));
								if(!(mListExp.contains(rsDet.getString(5))))
									mListExp.add(rsDet.getString(5));
								
								list.add(rsDet.getString(2)+","+rsDet.getString(5)+","+rsDet.getString(4));
							}						
						}
						if(alleleCount>0){
							//System.out.println("SELECT distinct allele_values.gid,germplasm_temp.germplasm_name,allele_values.marker_id,allele_values.allele_bin_value as data,marker.marker_name,marker.marker_type,dataset.species,allele_values.dataset_id FROM allele_values,marker,dataset,germplasm_temp WHERE allele_values.dataset_id=dataset.dataset_id AND allele_values.marker_id=marker.marker_id AND germplasm_temp.gid=allele_values.gid AND allele_values.gid IN ("+gid+") AND allele_values.marker_id IN (SELECT marker_id FROM marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY allele_values.gid DESC, marker.marker_name"); 
							/*rsDet=st.executeQuery("SELECT distinct allele_values.gid,germplasm_temp.germplasm_name,allele_values.marker_id,allele_values.allele_bin_value,marker.marker_name,marker.marker_type,dataset.species,allele_values.dataset_id"+
									" FROM allele_values,marker,dataset,germplasm_temp WHERE allele_values.dataset_id=dataset.dataset_id AND allele_values.marker_id=marker.marker_id"+
									" AND germplasm_temp.gid=allele_values.gid AND allele_values.gid IN ("+gid+") AND allele_values.marker_id IN (SELECT marker_id FROM marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY allele_values.gid DESC, marker.marker_name");*/
							rsDet=st.executeQuery("SELECT distinct allele_values.gid,names.nval,allele_values.marker_id,allele_values.allele_bin_value,marker.marker_name,marker.marker_type,dataset.species,allele_values.dataset_id"+
									" FROM allele_values,marker,dataset,names WHERE allele_values.dataset_id=dataset.dataset_id AND allele_values.marker_id=marker.marker_id"+
									" AND names.gid=allele_values.gid AND allele_values.gid IN ("+gid+") AND allele_values.marker_id IN (SELECT marker_id FROM marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY allele_values.gid DESC, marker.marker_name");
							while(rsDet.next()){
								if(!(gListExp.contains(rsDet.getString(2))))
									gListExp.add(rsDet.getString(2));
								if(!(mListExp.contains(rsDet.getString(5))))
									mListExp.add(rsDet.getString(5));
								
								list.add(rsDet.getString(2)+","+rsDet.getString(5)+","+rsDet.getString(4));
							}
						}
									
					
					//System.out.println("List="+list);
					
					}catch(Exception e){
						e.printStackTrace();
					}
				
					//System.out.println(".....................List="+gListExp);
					if(format.contains("Flapjack")){
						
						System.out.println("select marker_name, linkage_group, start_position from mapping_data where marker_name in ("+ mlist1.substring(0,mlist1.length()-1) +") and linkagemap_name in ('"+map+"') order by marker_name");
						rs=stmt.executeQuery("select marker_name, linkage_group, start_position from mapping_data where marker_name in ("+ mlist1.substring(0,mlist1.length()-1) +") and linkagemap_name in ('"+map+"') order by marker_name");
						while(rs.next()){
							//System.out.println(rs.getString(1)+"   "+rs.getString(2)+"   "+rs.getFloat(3));
							mapData=mapData+rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getFloat(3)+"~~!!~~";
							
						}						
					}			
				}
	
				//To write matrix  if datatype is character 
				
				if(format.contains("Genotyping X Marker Matrix")){
					ef.Matrix(list, filePath, req, gListExp, mListExp);
					//ef.Matrix(list, filePath, req);
				}
				
				if(format.contains("Flapjack")){
					ef.MatrixDat(list, mapData, filePath, req, gListExp, mListExp);
					//ef.MatrixDat(list, mapData, filePath, req);
					//ef.Matrix(list, filePath, request);
				}	
			}
			
	}catch(Exception e){
		e.printStackTrace();
	}finally{
	      try{		      		
	      		if(con!=null) con.close();
	      		
	         }catch(Exception e){System.out.println(e);}
		}
		return am.findForward("exp");
	}
	

}
