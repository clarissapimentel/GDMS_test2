package org.icrisat.gdms.retrieve;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.upload.FormFile;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.common.ExportFormats;
import org.icrisat.gdms.common.FileUploadToServer;
import org.icrisat.gdms.common.HibernateSessionFactory;
import org.icrisat.gdms.common.MaxIdValue;

public class DataRetrieveAction extends Action{
	
	Connection con;
	private Session hsession;	
	private Transaction tx;
	String str="";
	String strVal="";
	String str1="";
	String type="";
	ArrayList  markerList=new ArrayList();
	ArrayList genotypesList=new ArrayList();
	List mapList=null;
	String gids="";
	String upRes="";
	String chValues="";
	ResultSet rsDet=null;
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		HttpSession session = req.getSession(true);
		ActionErrors ae = new ActionErrors();	
		if(session!=null){
			session.removeAttribute("strdata");			
		}
		// TODO Auto-generated method stub
		hsession = HibernateSessionFactory.currentSession();
		tx=hsession.beginTransaction();
		DynaActionForm df = (DynaActionForm) af;
		
		String retrieveOP=(String)df.get("retrieveOP");	
		//System.out.println(".................................:"+retrieveOP);
		float distance=0;
		int mCount=0;
		String mapData="";
		ArrayList CD=new ArrayList();
		ArrayList dist=new ArrayList();
		List listValues=null;
		Query query=null;
		String exp="";
		//Query query=null;	
		Query query1=null;
		Query query2=null;
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/icis","root","root");
			ResultSet rs=null;
			ResultSet rs1=null;
			Statement stmt=con.createStatement();
			Statement stmtR=con.createStatement();
		
			if(retrieveOP.equalsIgnoreCase("first")){
				if(session!=null){
					session.removeAttribute("indErrMsg");	
				}
				query2=hsession.createQuery("select distinct linkagemap_name from LinkagemapBean");
				mapList=query2.list();
				session.setAttribute("mapList", mapList);
				str="qtlPage";
			}else if(retrieveOP.equalsIgnoreCase("Submit")){
				/** for retrieving polymorphic markers between 2 lines **/
				if(session!=null){
					session.removeAttribute("MissingData");		
					session.removeAttribute("map_data");	
					session.removeAttribute("recCount");	
					session.removeAttribute("missingCount");	
				}
				String gids,gids1="";
				chValues="";
				String line1=(String)df.get("linesO");
				String line2=(String)df.get("linesT");
				String selLines="'"+line1+"' & '"+line2+"'";
				strVal="'"+line1+"','"+line2+"'";	
				session.setAttribute("selLines", selLines);
				ArrayList finalList=new ArrayList();
				String[] str1=null;
				ArrayList geno1=new ArrayList();
				ArrayList geno2=new ArrayList();
				ArrayList mark1=new ArrayList();
				ArrayList mark2=new ArrayList();
				ArrayList ch1=new ArrayList();
				ArrayList ch2=new ArrayList();
				ArrayList missingList=new ArrayList();
				int alleleCount=0;
				int charCount=0;
			
				/**
				 * Query for retrieving the gid of the respective germplasm_name 
				 */
				//System.out.println("select gid from germplasm_temp where germplasm_name in ("+strVal+")");
				//rs = stmt.executeQuery("select gid from germplasm_temp where germplasm_name in ("+strVal+")");
				rs = stmt.executeQuery("select gid from names where nval in ("+strVal+")");				
				while(rs.next()){				
					gids1 = gids1+rs.getString(1)+",";
				}
				gids = gids1.substring(0,gids1.length()-1);
				//System.out.println("gids="+gids);
				
				
				Statement stA=con.createStatement();
				Statement stC=con.createStatement();
				Statement stmtC=con.createStatement();
				Statement stmtA=con.createStatement();
				
				/** checking whether the gid exists in 'allele_values' table **/
				ResultSet rsa=stA.executeQuery("select count(*) from allele_values where gid in ("+gids+")");
				while (rsa.next()){
					alleleCount=rsa.getInt(1);
				}
				
				/** checking whether the gid exists in 'char_values' table **/
				ResultSet rsc=stC.executeQuery("select count(*) from char_values where gid in("+gids+")");
				while(rsc.next()){
					charCount=rsc.getInt(1);
				}
				
				/** if gids exists in 'char_values' table retrieving corresponding data and concatinating germplasm name, marker_name & char_value **/
				if(charCount>0){
					//System.out.println("SELECT distinct char_values.dataset_id,char_values.gid,germplasm_temp.germplasm_name,marker.crop,char_values.marker_id,marker.marker_name,char_values.char_value FROM char_values,germplasm_temp,marker WHERE char_values.gid in ("+gids+") AND char_values.gid=germplasm_temp.gid AND marker.marker_id = char_values.marker_id ORDER BY germplasm_name, marker_name");
					//rsDet=stmtC.executeQuery("SELECT distinct char_values.dataset_id,char_values.gid,germplasm_temp.germplasm_name,marker.crop,char_values.marker_id,marker.marker_name,char_values.char_value as data FROM char_values,germplasm_temp,marker WHERE char_values.gid in ("+gids+") AND char_values.gid=germplasm_temp.gid AND marker.marker_id = char_values.marker_id ORDER BY germplasm_name, marker_name");
					System.out.println("SELECT distinct char_values.dataset_id,char_values.gid,names.nval,marker.crop,char_values.marker_id,marker.marker_name,char_values.char_value FROM char_values,names,marker WHERE char_values.gid in ("+gids+") AND char_values.gid=names.gid AND marker.marker_id = char_values.marker_id ORDER BY nval, marker_name");
					rsDet=stmtC.executeQuery("SELECT distinct char_values.dataset_id,char_values.gid,names.nval,marker.crop,char_values.marker_id,marker.marker_name,char_values.char_value as data FROM char_values,names,marker WHERE char_values.gid in ("+gids+") AND char_values.gid=names.gid AND marker.marker_id = char_values.marker_id ORDER BY nval, marker_name");
					
					
					while(rsDet.next()){
						chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
					}
				
				}
				
				/** if gids exists in 'allele_values' table retrieving corresponding data and concatinating germplasm name, marker_name & allele_bin_value **/				
				if(alleleCount>0){
					/*System.out.println("SELECT allele_values.dataset_id,allele_values.gid,germplasm_temp.germplasm_name,marker.crop,allele_values.marker_id,marker.marker_name,allele_values.allele_bin_value as data FROM allele_values,germplasm_temp,marker WHERE allele_values.gid in ("+gids+") AND allele_values.gid=germplasm_temp.gid AND marker.marker_id = allele_values.marker_id ORDER BY germplasm_name, marker_name");
					rsDet=stmtA.executeQuery("SELECT allele_values.dataset_id,allele_values.gid,germplasm_temp.germplasm_name,marker.crop,allele_values.marker_id,marker.marker_name,allele_values.allele_bin_value as data FROM allele_values,germplasm_temp,marker WHERE allele_values.gid in ("+gids+") AND allele_values.gid=germplasm_temp.gid AND marker.marker_id = allele_values.marker_id ORDER BY germplasm_name, marker_name");*/
					System.out.println("SELECT allele_values.dataset_id,allele_values.gid,names.nval,marker.crop,allele_values.marker_id,marker.marker_name,allele_values.allele_bin_value as data FROM allele_values,names,marker WHERE allele_values.gid in ("+gids+") AND allele_values.gid=names.gid AND marker.marker_id = allele_values.marker_id ORDER BY nval, marker_name");
					rsDet=stmtA.executeQuery("SELECT allele_values.dataset_id,allele_values.gid,names.nval,marker.crop,allele_values.marker_id,marker.marker_name,allele_values.allele_bin_value as data FROM allele_values,names,marker WHERE allele_values.gid in ("+gids+") AND allele_values.gid=names.gid AND marker.marker_id = allele_values.marker_id ORDER BY nval, marker_name");
					
					while(rsDet.next()){
						chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
					}
				}
				
				 String[] chVal=chValues.split("~!!~");
				
				 int s=0;
				 geno1.clear();geno2.clear();mark1.clear(); mark2.clear();ch1.clear();ch2.clear();
				 for(int c=0;c<chVal.length;c++){	
					str1=chVal[c].split("!~!");
					if(str1[0].equalsIgnoreCase(line1)){
						geno1.add(str1[0]);
						mark1.add(str1[1]);
						ch1.add(str1[2]);					
					}else{
						geno2.add(str1[0]);
						mark2.add(str1[1]);
						ch2.add(str1[2]);
					}			
				}
				
				finalList=new ArrayList();
				missingList=new ArrayList();
				String geno="";
				String markers="";
				for(int k=0;k<geno1.size();k++){
					if((!(ch2.get(k).equals("N")||ch2.get(k).equals("?")||ch2.get(k).equals("-")))&&(!(ch1.get(k).equals("N")||ch1.get(k).equals("?")||ch1.get(k).equals("-")))&&(!(ch1.get(k).equals(ch2.get(k))))){
						finalList.add(mark1.get(k));
						markers=markers+mark1.get(k)+"!~!";						
					}
					if((ch1.get(k).equals("?"))||(ch2.get(k).equals("?"))||(ch1.get(k).equals("N"))||(ch2.get(k).equals("N"))||(ch1.get(k).equals("-"))||(ch2.get(k).equals("-"))){
						missingList.add(mark1.get(k));
					}
				}
				String map_data="";
				String mark="";
				ArrayList mapList=new ArrayList();
				
				for(int f=0;f<finalList.size();f++){
					mark=mark+"'"+finalList.get(f)+"',";
				}
				boolean map=false;
				
				/** retrieving map info for the above markers if exists **/
				//System.out.println("SELECT distinct marker_name, start_position, linkage_group FROM mapping_data where marker_name in("+ mark.substring(0,mark.length()-1) +") order BY marker_name");
				rs1=stmtR.executeQuery("SELECT distinct marker_name, start_position, linkage_group FROM mapping_data where marker_name in("+ mark.substring(0,mark.length()-1) +") order BY marker_name");
				while(rs1.next()){	
					map=true;
					map_data=map_data+rs1.getString(1)+"!~!"+rs1.getString(2)+"!~!"+rs1.getString(3)+"~!!~";
				}
				//System.out.println("missingList="+missingList);
				String recCount=finalList.size()+"";
				String mcount=missingList.size()+"";
				
				req.getSession().setAttribute("map", map);
				//req.getSession().setAttribute("lines", geno);
				req.getSession().setAttribute("MissingData", missingList);
				req.getSession().setAttribute("map_data", map_data);
				req.getSession().setAttribute("recCount",recCount);
				req.getSession().setAttribute("missingCount",mcount);
				str="poly";
				String sel="yes";
				req.getSession().setAttribute("sel", sel);
				if(map==true){
					req.getSession().setAttribute("result",map_data);
				}else if(map==false){
					req.getSession().setAttribute("result", finalList);
				}
			
			}else if(retrieveOP.equalsIgnoreCase("Get Lines")){
				/** retrieving gid, germplasm name for the markers that are uploaded through the text file  **/
				type="GermplasmName";
				req.getSession().setAttribute("op", retrieveOP);
				ArrayList germNames=new ArrayList();
				ArrayList markerList=new ArrayList();
				String markers="";
				String m1="";
				String m2="";
				FileUploadToServer fus = null;
				String fileName="";String saveFtoServer="";String saveF="";
				InputStream stream=null;
				FormFile file=(FormFile)df.get("txtNameL");
				//String fname1=file.getFileName();
					
				stream=file.getInputStream();
			    saveFtoServer="UploadFiles";
			    saveF=req.getSession().getServletContext().getRealPath("//")+"/"+saveFtoServer;
			    if(!new File(saveF).exists())
				   	new File(saveF).mkdir();	        
				fileName=saveF+"/"+file.getFileName();
				fus=new FileUploadToServer();
				//System.out.println("................   :"+fileName);
				fus.createFile(stream,fileName);
				BufferedReader bf=new BufferedReader(new FileReader(fileName));		
				while ((str1 = bf.readLine()) != null){				
					markers= markers+str1+",";	
					markerList.add(str1);
				}
				//System.out.println("markers="+markers);
				String[] splitStr=markers.split(",");
				session.setAttribute("mCount", splitStr.length-1);
				if(splitStr[0].equalsIgnoreCase("marker name")){
					for(int m=1;m<splitStr.length;m++){
						m1=m1+"'"+splitStr[m]+"',";
						m2=m2+splitStr[m]+",";
					}			
				}
				//System.out.println(m1);
				session.setAttribute("mnames1", m1);
				session.setAttribute("mnames", m2);
				String markerId="";
				
				
				int alleleCount=0;
				int charCount=0;
				Statement stA=con.createStatement();
				Statement stC=con.createStatement();
				Statement stmtC=con.createStatement();
				Statement stmtA=con.createStatement();
				
					Statement stmttest=con.createStatement();
					ResultSet rs2=null;
					Statement st=con.createStatement();
					int count=0;
					rs=stmt.executeQuery("select marker_id from marker where marker_name in("+ m1.substring(0,m1.length()-1) +") order by marker_id");
					while(rs.next()){
						count=count+1;
						markerId=markerId+rs.getInt(1)+",";
					}
					System.out.println(markerId);
					
					/** checking whether the marker id exists in 'allele_values' table **/
					System.out.println("select count(*) from allele_values where marker_id in ("+ markerId.substring(0,markerId.length()-1) +")");
					ResultSet rsa=stA.executeQuery("select count(*) from allele_values where marker_id in ("+ markerId.substring(0,markerId.length()-1) +")");
					while (rsa.next()){
						alleleCount=rsa.getInt(1);
					}
					
					/** checking whether the marker id exists in 'char_values' table **/
					ResultSet rsc=stC.executeQuery("select count(*) from char_values where marker_id in ("+ markerId.substring(0,markerId.length()-1) +")");
					while(rsc.next()){
						charCount=rsc.getInt(1);
					}
					
					/** if marker_id exists in 'char_values' table retrieving corresponding data and concatinating germplasm name, marker_name & char_value **/
					if(charCount>0){
						//System.out.println("SELECT distinct char_values.gid,germplasm_temp.germplasm_name,marker.marker_name FROM char_values,germplasm_temp,marker WHERE char_values.gid in ("+gids+") AND char_values.gid=germplasm_temp.gid AND marker.marker_id = char_values.marker_id ORDER BY germplasm_name, marker_name");
						//rsDet=stmtC.executeQuery("SELECT distinct char_values.gid,germplasm_temp.germplasm_name,marker.marker_name FROM char_values,germplasm_temp,marker WHERE char_values.marker_id in ("+ markerId.substring(0,markerId.length()-1) +") AND char_values.gid=germplasm_temp.gid AND marker.marker_id = char_values.marker_id ORDER BY germplasm_name, marker_name");
						rsDet=stmtC.executeQuery("SELECT distinct char_values.gid,names.nval,marker.marker_name FROM char_values,names,marker WHERE char_values.marker_id in ("+ markerId.substring(0,markerId.length()-1) +") AND char_values.gid=names.gid AND marker.marker_id = char_values.marker_id ORDER BY nval, marker_name");
						
						while(rsDet.next()){
							//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
							if(!germNames.contains(rsDet.getString(2)+","+rsDet.getInt(1)))
								germNames.add(rsDet.getString(2)+","+rsDet.getInt(1));
						}
					
					}
					
					/** if marker id exists in 'allele_values' table retrieving corresponding data and concatinating germplasm name, marker_name & allele_bin_value **/				
					if(alleleCount>0){
						//System.out.println("SELECT allele_values.gid,germplasm_temp.germplasm_name,marker.marker_name FROM allele_values,germplasm_temp,marker WHERE allele_values.gid in ("+gids+") AND allele_values.gid=germplasm_temp.gid AND marker.marker_id = allele_values.marker_id ORDER BY germplasm_name, marker_name");
						//rsDet=stmtA.executeQuery("SELECT allele_values.gid,germplasm_temp.germplasm_name,marker.marker_name FROM allele_values,germplasm_temp,marker WHERE allele_values.marker_id in ("+ markerId.substring(0,markerId.length()-1) +") AND allele_values.gid=germplasm_temp.gid AND marker.marker_id = allele_values.marker_id ORDER BY germplasm_name, marker_name");
						rsDet=stmtA.executeQuery("SELECT allele_values.gid,names.nval,marker.marker_name FROM allele_values,names,marker WHERE allele_values.marker_id in ("+ markerId.substring(0,markerId.length()-1) +") AND allele_values.gid=names.gid AND marker.marker_id = allele_values.marker_id ORDER BY nval, marker_name");
						
						while(rsDet.next()){
							//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
							if(!germNames.contains(rsDet.getString(2)+","+rsDet.getInt(1)))
								germNames.add(rsDet.getString(2)+","+rsDet.getInt(1));
						}
					}
					
					
					
					/** stored procedure to retrieve data from 'char_values' & 'allele_values' tables  **//*
					try{
						//System.out.println("call test12(\'"+gids+"\',\'gid\',2,0,0)");
						stmttest.executeQuery("call RetrieveGenotypingData(\'"+markerId+"\',\'marker_id\',"+count+",0,0)");
					}catch(Exception e){
						e.printStackTrace();
					}
					
					try{
						//System.out.println("insert");
						ResultSet rstemp = null;
						Statement stmttemp=con.createStatement();
						rstemp=stmttemp.executeQuery(" select germplasm_name,gid from temp");
						while(rstemp.next()){
							//chValues=chValues+rstemp.getString(1)+"~!!~";
							if(!germNames.contains(rstemp.getString(1)+","+rstemp.getInt(2)))
								germNames.add(rstemp.getString(1)+","+rstemp.getInt(2));						
						}
						
					}catch(Exception e){
						e.printStackTrace();
					}*/
					query2=hsession.createQuery("select distinct linkagemap_name from LinkagemapBean");
					mapList=query2.list();
					session.setAttribute("AccListFinal", germNames);
					session.setAttribute("mapList", mapList);
					session.setAttribute("markerList", markerList);
					session.setAttribute("GenoCount", germNames.size());
					session.setAttribute("type", type);
				
				str="retGermplasms";
			}else if(retrieveOP.equalsIgnoreCase("Get Markers")){	
				/** retrieving markers for the gids that are uploaded through the text file  **/
				
				type="markers";
				gids="";
				FileUploadToServer fus = null;
				String fileName="";String saveFtoServer="";String saveF="";
				InputStream stream=null;
				
				Statement stmttest=con.createStatement();		
				FormFile file=(FormFile)df.get("txtNameM");
				
				stream=file.getInputStream();
			    saveFtoServer="UploadFiles";
			    saveF=req.getSession().getServletContext().getRealPath("//")+"/"+saveFtoServer;
			    if(!new File(saveF).exists())
				   	new File(saveF).mkdir();	        
				fileName=saveF+"/"+file.getFileName();
				fus=new FileUploadToServer();
				fus.createFile(stream,fileName);
				
				
				BufferedReader bf=new BufferedReader(new FileReader(fileName));		
				while ((str1 = bf.readLine()) != null){				
					gids= gids+str1+",";					
				}
				
				String gidsN="";
				//System.out.println("request.getParameter radios ="+request.getParameter("str") );
				//System.out.println("gids in class="+gids);
				//String op=req.getParameter("str");
				req.getSession().setAttribute("op", retrieveOP);
				
				String[] strGids=gids.split(",");
				int gidsCount=strGids.length-1;
				if(strGids[0].equalsIgnoreCase("gids")){				
					for(int i=1;i<strGids.length;i++){
						gidsN=gidsN+strGids[i]+",";
					}				
					req.getSession().setAttribute("genCount", gidsCount);
				}
				
				
				req.getSession().setAttribute("gidsN", gidsN);
				//System.out.println("%%%%%%%%%%%%%%%%   length="+gidsN);
				String gid=gidsN.substring(0,gidsN.length()-1);
				int alleleCount=0;
				int charCount=0;
				Statement stA=con.createStatement();
				Statement stC=con.createStatement();
				Statement stmtC=con.createStatement();
				Statement stmtA=con.createStatement();
				
				System.out.println("select count(*) from allele_values where gid in ("+gid+")");
				/** checking whether the gid exists in 'allele_values' table **/
				ResultSet rsa=stA.executeQuery("select count(*) from allele_values where gid in ("+gid+")");
				while (rsa.next()){
					alleleCount=rsa.getInt(1);
				}
				
				/** checking whether the gid exists in 'char_values' table **/
				ResultSet rsc=stC.executeQuery("select count(*) from char_values where gid in("+gid+")");
				while(rsc.next()){
					charCount=rsc.getInt(1);
				}
				
				/** if gids exists in 'char_values' table retrieving corresponding data and concatinating germplasm name, marker_name & char_value **/
				if(charCount>0){
					/*System.out.println("SELECT distinct germplasm_temp.germplasm_name,marker.marker_name FROM char_values,germplasm_temp,marker WHERE char_values.gid in ("+gid+") AND char_values.gid=germplasm_temp.gid AND marker.marker_id = char_values.marker_id ORDER BY germplasm_name, marker_name");
					rsDet=stmtC.executeQuery("SELECT distinct germplasm_temp.germplasm_name,marker.marker_name FROM char_values,germplasm_temp,marker WHERE char_values.gid in ("+gid+") AND char_values.gid=germplasm_temp.gid AND marker.marker_id = char_values.marker_id ORDER BY germplasm_name, marker_name");
					*/
					System.out.println("SELECT distinct names.nval,marker.marker_name FROM char_values,names,marker WHERE char_values.gid in ("+gid+") AND char_values.gid=names.gid AND marker.marker_id = char_values.marker_id ORDER BY nval, marker_name");
					rsDet=stmtC.executeQuery("SELECT distinct names.nval,marker.marker_name FROM char_values,names,marker WHERE char_values.gid in ("+gid+") AND char_values.gid=names.gid AND marker.marker_id = char_values.marker_id ORDER BY nval, marker_name");
					
					while(rsDet.next()){
						//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
						if(!genotypesList.contains(rsDet.getString(1)))
							genotypesList.add(rsDet.getString(1));	
						if(!markerList.contains(rsDet.getString(2)))
							markerList.add(rsDet.getString(2));
					}
				
				}
				
				/** if gids exists in 'allele_values' table retrieving corresponding data and concatinating germplasm name, marker_name & allele_bin_value **/				
				if(alleleCount>0){
					/*System.out.println("SELECT germplasm_temp.germplasm_name,marker.marker_name FROM allele_values,germplasm_temp,marker WHERE allele_values.gid in ("+gid+") AND allele_values.gid=germplasm_temp.gid AND marker.marker_id = allele_values.marker_id ORDER BY germplasm_name, marker_name");
					rsDet=stmtA.executeQuery("SELECT germplasm_temp.germplasm_name,marker.marker_name FROM allele_values,germplasm_temp,marker WHERE allele_values.gid in ("+gid+") AND allele_values.gid=germplasm_temp.gid AND marker.marker_id = allele_values.marker_id ORDER BY germplasm_name, marker_name");
					*/
					System.out.println("SELECT names.nval,marker.marker_name FROM allele_values,names,marker WHERE allele_values.gid in ("+gid+") AND allele_values.gid=names.gid AND marker.marker_id = allele_values.marker_id ORDER BY nval, marker_name");
					rsDet=stmtA.executeQuery("SELECT names.nval,marker.marker_name FROM allele_values,names,marker WHERE allele_values.gid in ("+gid+") AND allele_values.gid=names.gid AND marker.marker_id = allele_values.marker_id ORDER BY nval, marker_name");
					
					while(rsDet.next()){
						//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
						if(!genotypesList.contains(rsDet.getString(1)))
							genotypesList.add(rsDet.getString(1));	
						if(!markerList.contains(rsDet.getString(2)))
							markerList.add(rsDet.getString(2));
					}
				}
				
				
				
				
				/** stored procedure to retrieve data from 'char_values' & 'allele_values' tables here passing gids **//*
				
				try{
					stmttest.executeQuery("call RetrieveGenotypingData(\'"+gid+"\',\'gid\',"+gidsCount+",0,0)");				
				}catch(Exception e){
					e.printStackTrace();
				}
				try{
					
					ResultSet rstemp = null;
					Statement stmttemp=con.createStatement();
					rstemp=stmttemp.executeQuery("select distinct germplasm_name,marker_name from temp");
					while(rstemp.next()){
						//System.out.println("INSIDE While");
						if(!genotypesList.contains(rstemp.getString(1)))
								genotypesList.add(rstemp.getString(1));	
						if(!markerList.contains(rstemp.getString(2)))
							markerList.add(rstemp.getString(2));	
					}
					
				}catch(Exception e){
					e.printStackTrace();
				}
				
				*/
				query2=hsession.createQuery("select distinct linkagemap_name from LinkagemapBean");
				
				
				mapList=query2.list();
				req.getSession().setAttribute("AccListFinal", genotypesList);
				req.getSession().setAttribute("mapList", mapList);
				req.getSession().setAttribute("markerList", markerList);
				
				req.getSession().setAttribute("MarkerCount", markerList.size());
				session.setAttribute("type", type);
				str="retMarkers";
				
			}else if(retrieveOP.equalsIgnoreCase("GetInfo")){
				String opType=req.getParameter("retType");
				//System.out.println("888888888888888888888....  opType="+opType);
				//if((opType.equals("qtlData"))||(opType.equals("trait"))){
				//System.out.println("QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ TTTTTTTTTTTTTTTTTTTTTTTTTTT");
				/** Retrieving qtl information **/
				if(session!=null){
					session.removeAttribute("strdata");		
					session.removeAttribute("indErrMsg");	
				}
				
				
				//String qtlType=(String)df.get("qtlData");
				//String qtlType=req.getParameter("retType");
				String qtl=(String)df.get("qtl");
				String qtlData="";
				int linkid=0;			float min=0;			float max=0;
				String lg="";			String markers="";			int qtlId=0;
				String qtlIds="";int count=0;String qtl_id="";	String strData="";
				
					
				ResultSet rs2=null;
				Statement st=con.createStatement();
				session.setAttribute("qtlType", opType);
				System.out.println("******************************************  "+opType+"  $$$$$$$$   "+qtl);
				if(opType.equals("QTLName")){
					/** if retrieval is through QTL Name **/
					//System.out.println("select qtl_id from qtl where qtl_name like '"+qtl+"%'");
					rs1=st.executeQuery("select qtl_id from qtl where qtl_name like '"+qtl+"%' order by qtl_id");
					while(rs1.next()){
						qtlId=rs1.getInt(1);
						//qtl_id=qtl_id+qtlId+",";	
						count=count+1;
					}
					if(count==0){
						/*ae.add("myerrors", new ActionError("qtl.doesnot.exists"));
						saveErrors(req, ae);				
						//msg="chkPlateid";
						return (new ActionForward(am.getInput()));*/
						String ErrMsg = "QTL Name not found";
						req.getSession().setAttribute("indErrMsg", ErrMsg);
						return am.findForward("ErrMsg");
					}
					rs=stmt.executeQuery("SELECT qtl.qtl_name,linkagemap.linkagemap_name,qtl_linkagemap.linkage_group AS chromosome, qtl_linkagemap.min_position, qtl_linkagemap.max_position, qtl_linkagemap.trait, qtl_linkagemap.experiment, qtl_linkagemap.left_flanking_marker, qtl_linkagemap.right_flanking_marker, qtl_linkagemap.effect, qtl_linkagemap.lod, qtl_linkagemap.r_square,qtl_linkagemap.interactions FROM qtl_linkagemap, qtl,linkagemap WHERE qtl.qtl_name like '"+qtl.toLowerCase()+"%' AND qtl.qtl_id=qtl_linkagemap.qtl_id AND qtl_linkagemap.linkagemap_id=linkagemap.linkagemap_id order by qtl.qtl_id");
					while(rs.next()){
						qtlData=qtlData+rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getString(3)+"!~!"+rs.getFloat(4)+"!~!"+rs.getFloat(5)+"!~!"+rs.getString(6)+"!~!"+rs.getString(7)+"!~!"+rs.getString(8)+"!~!"+rs.getString(9)+"!~!"+rs.getFloat(10)+"!~!"+rs.getFloat(11)+"!~!"+rs.getString(13)+"!~!"+rs.getFloat(12)+";;;";
					}
					//System.out.println(qtlData);
					req.getSession().setAttribute("strdata",qtlData);
					req.getSession().setAttribute("qtl", qtl);
					
					str="retQTL";
				}else if(opType.equals("Trait")){
					/** if the option is through trait name **/
					
					//rs1=st.executeQuery("select qtl_id from qtl_linkagemap where trait='"+qtl+"' order by qtl_id");
					rs1=st.executeQuery("select qtl_id from qtl_linkagemap where trait like '"+qtl+"%' order by qtl_id");
					while(rs1.next()){
						qtlId=rs1.getInt(1);
						qtl_id=qtl_id+qtlId+",";	
						count=count+1;
					}
					if(count==0){
						/*ae.add("myerrors", new ActionError("trait.doesnot.exists"));
						saveErrors(req, ae);				
						//msg="chkPlateid";
						return (new ActionForward(am.getInput()));*/
						String ErrMsg = "Trait Name not found";
						req.getSession().setAttribute("indErrMsg", ErrMsg);
						return am.findForward("ErrMsg");
					}
					//System.out.println("QTL S="+qtl_id.substring(0,qtl_id.length()-1));
					req.getSession().setAttribute("qtl", qtl);
					
					//rs=stmt.executeQuery("select * from qtl_linkagemap where qtl_id in("+qtl_id.substring(0,qtl_id.length()-1)+")");
					rs=stmt.executeQuery("SELECT qtl.qtl_name,linkagemap.linkagemap_name,qtl_linkagemap.linkage_group AS chromosome, qtl_linkagemap.min_position, qtl_linkagemap.max_position, qtl_linkagemap.trait, qtl_linkagemap.experiment, qtl_linkagemap.left_flanking_marker, qtl_linkagemap.right_flanking_marker, qtl_linkagemap.effect, qtl_linkagemap.lod, qtl_linkagemap.r_square,qtl_linkagemap.interactions FROM qtl_linkagemap, qtl,linkagemap WHERE qtl_linkagemap.qtl_id IN("+qtl_id.substring(0,qtl_id.length()-1)+") AND qtl.qtl_id=qtl_linkagemap.qtl_id AND qtl_linkagemap.linkagemap_id=linkagemap.linkagemap_id order by qtl.qtl_id");
					while(rs.next()){
						strData=strData+rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getString(3)+"!~!"+rs.getFloat(4)+"!~!"+rs.getFloat(5)+"!~!"+rs.getString(6)+"!~!"+rs.getString(7)+"!~!"+rs.getString(8)+"!~!"+rs.getString(9)+"!~!"+rs.getFloat(10)+"!~!"+rs.getFloat(11)+"!~!"+rs.getString(13)+"!~!"+rs.getFloat(12)+"!~!"+rs.getFloat(13)+";;;";
						
					}					
					req.getSession().setAttribute("strdata",strData);
						
					str="retTrait";					
				}else if(opType.equals("maps")){
					
					//String map=df.get("maps").toString();
					
					//if(format.contains("CMTV")){
					ArrayList strDataM=new ArrayList();
					//rs1=stmtR.executeQuery("SELECT  MAX(`mapping_data`.`start_position`) AS `max` , `mapping_data`.`linkage_group` AS Linkage_group, `mapping_data`.`linkagemap_name` AS map FROM `mapping_data` WHERE mapping_data.linkagemap_name LIKE ('"+qtl+"%') GROUP BY UCASE(`mapping_data`.`linkage_group`)");
					int countF=0;
					//System.out.println("SELECT  COUNT(DISTINCT `mapping_data`.`marker_id`) AS `marker_count` ,MAX(`mapping_data`.`start_position`) AS `max` , `mapping_data`.`linkage_group` AS Linkage_group, `mapping_data`.`linkagemap_name` AS map FROM `mapping_data` WHERE lower(mapping_data.linkagemap_name) LIKE ('"+qtl.toLowerCase()+"%') GROUP BY UCASE(`mapping_data`.`linkage_group`),UCASE(mapping_data.linkagemap_name) ORDER BY `mapping_data`.`linkagemap_name`, `mapping_data`.`linkage_group`");
					rs1=stmtR.executeQuery("SELECT  COUNT(DISTINCT `mapping_data`.`marker_id`) AS `marker_count` ,MAX(`mapping_data`.`start_position`) AS `max` , `mapping_data`.`linkage_group` AS Linkage_group, `mapping_data`.`linkagemap_name` AS map FROM `mapping_data` WHERE lower(mapping_data.linkagemap_name) LIKE ('"+qtl.toLowerCase()+"%') GROUP BY UCASE(`mapping_data`.`linkage_group`),UCASE(mapping_data.linkagemap_name) ORDER BY `mapping_data`.`linkagemap_name`, `mapping_data`.`linkage_group`");
					while(rs1.next()){
						strDataM.add(rs1.getInt(1)+"!~!"+rs1.getFloat(2)+"!~!"+rs1.getString(3)+"!~!"+rs1.getString(4));
						countF=countF+1;
					}
					//System.out.println("strDataM="+strDataM);
					if(countF==0){
						//ae.add("myerrors", new ActionError("trait.doesnot.exists"));
						//saveErrors(req, ae);				
						//msg="chkPlateid";
						//return (new ActionForward(am.getInput()));
						String ErrMsg = "Map Name not found";
						req.getSession().setAttribute("indErrMsg", ErrMsg);
						return am.findForward("ErrMsg");
					}
					String[] strArr=strDataM.get(0).toString().split("!~!");
					
					String chr=strArr[3];
					mCount=Integer.parseInt(strArr[0]);
					distance=Float.parseFloat(strArr[1]);
					count=0;
					int mc=0;
					float d=0;
					String distSTR="";
					//System.out.println("strDataM.size()=:"+strDataM.size());
					for(int a=0;a<strDataM.size();a++){						
						String[] str1=strDataM.get(a).toString().split("!~!");		
						//System.out.println(" a="+a+" ,,,,markerCount="+str1[0]+"    ;startPosition="+str1[1]+"  ;LinkageGroup="+str1[2]+"  ;MapName="+str1[3]);
						if(str1[3].equals(chr)){
							mc=mc+Integer.parseInt(str1[0]);
							d=d+Float.parseFloat(str1[1]);							
							//System.out.println("..mc="+mc+"   d:"+d);
							if(a==(strDataM.size()-1)){
								//System.out.println("IF in IF "+a);
								mCount=mc;
								distance=d;
								distSTR=distSTR+mCount+"!~!"+chr+"!~!"+distance+";;";								
							}
						}else if(!(str1[3].equals(chr))){							
							mCount=mc;
							distance=d;
							distSTR=distSTR+mCount+"!~!"+chr+"!~!"+distance+";;";
							mc=0;
							d=0;
							chr=str1[3];
							a=a-1;
						}						
					}
					session.setAttribute("mapsSTR", distSTR);
					//System.out.println("........&&&&&&&&&&&&........"+distSTR);			
					session.setAttribute("type", "map");
					
					str="map";
						
					}/*else{
						Calendar now = Calendar.getInstance();
						String mSec=now.getTimeInMillis()+"";
						if(session!=null){
							session.removeAttribute("msec");			
						}
						req.getSession().setAttribute("exportFormat","CMTV");
						req.getSession().setAttribute("msec", mSec);
						String filePath="";
						ExportFormats ef=new ExportFormats();
						MaxIdValue r=new MaxIdValue();
						filePath=req.getSession().getServletContext().getRealPath("//");
						if(!new File(filePath+"/jsp/analysisfiles").exists())
					   		new File(filePath+"/jsp/analysisfiles").mkdir();
						//System.out.println("SELECT marker_name, linkage_group, start_position,linkagemap_name FROM mapping_data WHERE linkagemap_name like ('"+qtl+"%') ORDER BY marker_id, linkage_group AND start_position");
						rs=stmt.executeQuery("SELECT marker_name, linkage_group, start_position,linkagemap_name FROM mapping_data WHERE linkagemap_name like ('"+qtl+"%') ORDER BY marker_id, linkage_group AND start_position");
						while(rs.next()){
							mapData=mapData+rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getFloat(3)+"!~!"+rs.getString(4)+"~~!!~~";
							CD.add(rs.getString(2)+"!~!"+rs.getFloat(3)+"!~!"+rs.getString(1)+"!~!"+rs.getString(4));
							count=count+1;
							//CD.add(rs.getFloat(3));
						}
						if(count==0){
							ae.add("myerrors", new ActionError("trait.doesnot.exists"));
							saveErrors(req, ae);				
							//msg="chkPlateid";
							return (new ActionForward(am.getInput()));
							String ErrMsg = "Map Name not found";
							req.getSession().setAttribute("indErrMsg", ErrMsg);
							return am.findForward("ErrMsg");
						}
						//System.out.println("CDistance="+CD);
						String[] strArr=CD.get(0).toString().split("!~!");
						float dis=Float.parseFloat(strArr[1]);
						
						String chr=strArr[0];
						count=0;
						for(int a=0;a<CD.size();a++){
							String[] str1=CD.get(a).toString().split("!~!");						
							if(str1[0].equals(chr)){								
								distance=Float.parseFloat(str1[1])-dis;							
								dist.add(str1[0]+"!~!"+str1[2]+"!~!"+count+"!~!"+r.roundThree(distance)+"!~!"+str1[1]);
								count=count+1;
								dis=distance;							
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
						
						
						
						str="exp";
						
					}*/
				
				//}
				
			}
			
			//System.out.println(str);
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		      try{		      		
		      		if(con!=null) con.close();
		      		/*long time = System.currentTimeMillis();
		      	  System.gc();
		      	  System.out.println("It took " + (System.currentTimeMillis()-time) + " ms");*/
		         }catch(Exception e){System.out.println(e);}
			}
		return am.findForward(str);
	}
	
	

}
