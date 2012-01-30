package org.icrisat.gdms.upload;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import jxl.Sheet;
import jxl.Workbook;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.common.HibernateSessionFactory;
import org.icrisat.gdms.common.MaxIdValue;


public class DArTGenotypingDataUpload {
	String str="";
	private Session session;
	
	private Transaction tx;
	
	
	public DArTGenotypingDataUpload(){
		session = HibernateSessionFactory.currentSession();
		tx=session.beginTransaction();
	
	}
	public String setDArTData(HttpServletRequest request, String fname) throws SQLException{
		try{
			String dataset_type="DArT";
			String datatype="int";
			int intDataOrderIndex = 1;
			String strSource="",strDatalist="";
			String strGIDslist="";
			int mid=0;
			String markerId="";
			String germplasmName="";
			String gids1="";
			int gidsCount=0;
			int m=0;
			int g=0;
			String ErrMsg ="";
			
			List genoDataMarkers = new ArrayList();
			String alertGN="no";
	        String alertGID="no";
	        String notMatchingData="";
	        String notMatchingGIDS="";
	        String notMatchingDataExists="";
	        
	        
			DatasetBean ub=new DatasetBean();
			GenotypeUsersBean usb=new GenotypeUsersBean();	
			//ConditionsBean ubConditions=new ConditionsBean();
					
			
			
			Workbook workbook=Workbook.getWorkbook(new File(fname));
			String[] strSheetNames=workbook.getSheetNames();
			
			ExcelSheetValidations fv = new ExcelSheetValidations();
			//System.out.println("******************************");
			String strFv=fv.validation(workbook, request,"DArT");
			//System.out.println("Valid="+strFv);
			if(!strFv.equals("valid"))
				return strFv;
			
			
			for (int i=0;i<strSheetNames.length;i++){				
				if(strSheetNames[i].equalsIgnoreCase("DArT_Source"))
					strSource = strSheetNames[i];
				
				if(strSheetNames[i].equalsIgnoreCase("DArT_Data"))
					strDatalist = strSheetNames[i];	
				
				if(strSheetNames[i].equalsIgnoreCase("DArT_GIDs"))
					strGIDslist = strSheetNames[i];					
			}

			//////Retrieve the maximum column id from the database
			MaxIdValue uptMId=new MaxIdValue();
			int intDatasetId=uptMId.getMaxIdValue("dataset_id","dataset",session);
			int dataset_id=intDatasetId+1;
			
			int size=0;
			String mon="";
			Calendar cal = new GregorianCalendar();
			int month = cal.get(Calendar.MONTH);
			int year = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			if(month>=9) 
				mon=String.valueOf(month+1);
			else 
				mon="0"+(month+1);
			  
			 String curDate=year+ "-" + mon + "-" +day;		 
			
			Sheet sheetSource = workbook.getSheet(strSource);			
			/** reading from Data sheet of template **/
			Sheet sheetData = workbook.getSheet(strDatalist);				
			int rowCount=sheetData.getRows();
			int colCount=sheetData.getColumns();
			//System.out.println(dataset_id+"   "+sheetSource.getCell(1,4).getContents().trim()+"  "+dataset_type+"   "+sheetSource.getCell(1,2).getContents().trim()+"   "+sheetSource.getCell(1,3).getContents().trim()+"     "+curDate+"      "+datatype);

			/** retrieving user id from 'users' table **/
			int user_id=uptMId.getUserId("userid", "users", "uname", session,sheetSource.getCell(1,1).getContents().trim());
			
			
			/** retrieving maximum marker id from 'marker' table of database **/
			int maxMarkerId=uptMId.getMaxIdValue("marker_id","marker",session);
			
			for (int a=7;a<colCount;a++){				
				germplasmName=germplasmName+sheetData.getCell(a,0).getContents().trim()+",";				
			}
			String str1="";
			int strCount=0;
			
			/** appending all germplasm names to a variable **/
			for(int g1=7;g1<colCount;g1++){
				str1=str1+sheetData.getCell(g1,0).getContents().trim()+"!~!";
				strCount=strCount+1;				
			}
			
			/** reading gids & germplasm name from gids sheet of template and inserting to 'germplasm_temp' table **/
			Sheet sheetGIDs=workbook.getSheet(strGIDslist);			
			int rows=sheetGIDs.getRows();
			for(int r=1;r<rows;r++){					
				gids1=gids1+sheetGIDs.getCell(0,r).getContents().trim()+"!~!"+sheetGIDs.getCell(1,r).getContents().trim()+",";
				gidsCount=gidsCount+1;
			}
			if(gidsCount!=strCount){
				//System.out.println("NOT Matching");
				ErrMsg = "Germplasms in DArT_GIDs sheet doesnot match with the Germplasm in DArT_Data sheet.";
				request.getSession().setAttribute("indErrMsg", ErrMsg);
				return "ErrMsg";
			}
			System.out.println("gidsCount="+gidsCount+"     strCount="+strCount);
			int s=0;
			//String fGids="";
			ArrayList fGids=new ArrayList();
			String gidsRet="";
			HashMap<Integer, String> GIDsMap = new HashMap<Integer, String>();
			/** arranging gid's with respect to germplasm name in order to insert into allele_values table */
			if(gidsCount==strCount){			
				String[] arg1=gids1.split(",");
				String[] str2=str1.split("!~!");
				for(int a=0;a<arg1.length;a++){
					String[] arg2=arg1[a].split("!~!");
					if(str2[s].equals(arg2[1])){
						gidsRet=gidsRet+arg2[0]+",";
						fGids.add(arg2[0]);
						GIDsMap.put(Integer.parseInt(arg2[0]), arg2[1]);
					}
					s++;	
				}
			}
			
			System.out.println("GIDsMap="+GIDsMap);
			System.out.println("*******************"+gidsRet);
			 Map<Object, String> sortedMap = new TreeMap<Object, String>(GIDsMap);
			ArrayList lstGIDs=uptMId.getGIds("gid, nval", "names", "gid", session, gidsRet.substring(0,gidsRet.length()-1));
            SortedMap mapG = new TreeMap();
            List lstgermpName = new ArrayList();
            for(int w=0;w<lstGIDs.size();w++){
                 Object[] strMareO= (Object[])lstGIDs.get(w);
                 lstgermpName.add(strMareO[0]);
                 String strMa123 = (String)strMareO[1];
                 mapG.put(strMareO[0], strMa123);
                 
            }
            Iterator iterator = mapG.keySet().iterator();
	        Iterator iterator1 = sortedMap.keySet().iterator();
           //System.out.println("map=:"+map.size());
           if(mapG.size()==0){
        	   alertGID="yes";
        	   size=0;
        	   /*while (iterator1.hasNext()) {
        		   Object key1 = iterator1.next();
        		   notMatchingGIDS=notMatchingGIDS+key1+"   "+sortedMap.get(key1)+",\t";
        	   }*/
           }
            
          
           
           while ((iterator.hasNext())&&(iterator1.hasNext())) {
        	   Object key = iterator.next();
        	   Object key1 = iterator1.next();
        	   //System.out.println("key : " + key + " value :" + map.get(key));
        	   if(key.equals(key1)){
        		   //System.out.println("**************************************");
	        	   if(!(mapG.get(key).equals(sortedMap.get(key1)))){	
	        		   //notMatchingData=notMatchingData+sortedMap.get(key1)+",";
	        		   notMatchingGIDS=notMatchingGIDS+key1+",";
	        		   notMatchingDataExists=notMatchingDataExists+mapG.get(key)+",";
	        		   notMatchingData=notMatchingData+key1+"   "+mapG.get(key)+"\n\t";
	        		   alertGN="yes";      		   
	        	   }		        	   
        	   }else{
        		   //System.out.println("key : " + key + " value :" + map.get(key));
        		   alertGID="yes";
        		   size=mapG.size();
        		   notMatchingGIDS=notMatchingGIDS+key1+"\n\t";        		  
        	   }	        	   
           }
           if(alertGN.equals("yes")){
        	   //String ErrMsg = "GID(s) ["+notMatchingGIDS.substring(0,notMatchingGIDS.length()-1)+"] of Germplasm(s) ["+notMatchingData.substring(0,notMatchingData.length()-1)+"] being assigned to ["+notMatchingDataExists.substring(0,notMatchingDataExists.length()-1)+"] \n Please verify the template ";
        	   ErrMsg = "Please verify the following GID(s) which correspond to Germplasm with a different name: \n\t "+notMatchingData;
        	   request.getSession().setAttribute("indErrMsg", ErrMsg);
        	   return "ErrMsg";	 
           }
           if(alertGID.equals("yes")){
        	   if(size==0){
        		   ErrMsg = "GID/Germplasm(s) provided doesnot exists. \n Please upload germplasm Information into GMS ";
        	   }else{
        		   ErrMsg = "Please verify the following GID/Germplasm(s) provided doesnot exists. \n Please upload germplasm Information into GMS \n \t"+notMatchingGIDS;
        	   }
        	   request.getSession().setAttribute("indErrMsg", ErrMsg);
        	   return "ErrMsg";
           }
			System.out.println("fGids="+fGids);
			//** Writing from source sheet of Template to 'dataset' table  **//*
			ub.setDataset_id(dataset_id);
			ub.setDataset_desc((String)sheetSource.getCell(1,4).getContents().trim());
			ub.setDataset_type(dataset_type);
			ub.setGenus(sheetSource.getCell(1,2).getContents().trim());
			ub.setSpecies(sheetSource.getCell(1,3).getContents().trim());
			ub.setTemplate_date(curDate);					
			ub.setDatatype(datatype);
			session.saveOrUpdate(ub);
			
			
			//************* inserting into 'dataset_users' table  *************//*
			usb.setDataset_id(dataset_id);
			usb.setUser_id(user_id);			
			session.save(usb);
			
			//*********** dataset_details;
//			ubConditions.setDataset_id(dataset_id);
//			ubConditions.setMethod_name(sheetSource.getCell(1,7).getContents().trim());
//			ubConditions.setMethod_desc(sheetSource.getCell(1,8).getContents().trim());
//			session.saveOrUpdate(ubConditions);
			/*for(int r=1;r<rows;r++){	
				GermplasmBean gb= new GermplasmBean();
				gb.setGid(Integer.parseInt(sheetGIDs.getCell(0,r).getContents().trim()));
				gb.setGermplasm_name(sheetGIDs.getCell(1,r).getContents().trim());
				session.save(gb);
				
			}*/
			//** checking whether marker exists in the database if exists using the marker id other wise inserting to the 'marker' table **//*
			String markersList="";
			HashMap<String, Object> map = new HashMap<String, Object>();
			for (int r=1;r<rowCount;r++){
				markersList = markersList +"'"+ sheetData.getCell(1,r).getContents().trim().toString()+"',";
			}
			 ArrayList lstMarIdNames=uptMId.getMarkerIds("marker_id, marker_name", "marker", "marker_name", session, markersList.substring(0, markersList.length()-1));
	          
	            List lstMarkers = new ArrayList();
	            for(int w=0;w<lstMarIdNames.size();w++){
	                 Object[] strMareO= (Object[])lstMarIdNames.get(w);
	                 lstMarkers.add(strMareO[1]);
	                 String strMa123 = (String)strMareO[1];
	                 map.put(strMa123, strMareO[0]);
	                 
	            }
			
			System.out.println("map"+map);
			//**  inserting data from data sheet of template to database  **//*		
			for (int im=1;im<rowCount;im++){
				DArTDetailsBean dartBean=new DArTDetailsBean();
				MarkerInfoBean mib=new MarkerInfoBean();
				String marker=sheetData.getCell(1,im).getContents().trim().toString();
				if(lstMarkers.contains(marker)){
					maxMarkerId=Integer.parseInt(map.get(marker).toString());
				}else{
					maxMarkerId=maxMarkerId+1;
					mib.setMarkerId(maxMarkerId);
					mib.setMarker_type(dataset_type);
					mib.setMarker_name(sheetData.getCell(1,im).getContents().trim().toString());
					mib.setCrop(sheetSource.getCell(1,3).getContents().trim());
					session.saveOrUpdate(mib);
				}
				//mid=uptMId.getUserId("marker_id", "marker", "marker_name", session,sheetData.getCell(1,im).getContents().trim());
				/*if(mid==0){
					maxMarkerId=maxMarkerId+1;
					mib.setMarkerId(maxMarkerId);
					mib.setMarker_type(dataset_type);
					mib.setMarker_name(sheetData.getCell(1,im).getContents().trim().toString());
					mib.setCrop(sheetSource.getCell(1,3).getContents().trim());
					session.saveOrUpdate(mib);
				}else{
					maxMarkerId= mid;
				}*/
				markerId=markerId+maxMarkerId+",";
				//** inserting into 'dart_details' table **//*
				dartBean.setDataset_id(dataset_id);
				dartBean.setMarker_id(maxMarkerId);
				dartBean.setClone_id(Integer.parseInt(sheetData.getCell(0,im).getContents().trim()));
				dartBean.setQvalue(Float.parseFloat(sheetData.getCell(2,im).getContents().trim()));
				dartBean.setReproducibility(Float.parseFloat(sheetData.getCell(3,im).getContents().trim()));
				dartBean.setCall_rate(Float.parseFloat(sheetData.getCell(4,im).getContents().trim()));
				dartBean.setPic_value(Float.parseFloat(sheetData.getCell(5,im).getContents().trim()));
				dartBean.setDiscordance(Float.parseFloat(sheetData.getCell(6,im).getContents().trim()));
				session.save(dartBean);
				if (im % 1 == 0){
					session.flush();
					session.clear();
				}
				//m++;
			}			
			
			
			String[] markers=markerId.split(",");
			//String[] accessions=germplasmName.split(",");		
			
			int kk=0;
			
			//** inserting data into 'allele_values' table **//*
			for(int i=1;i<rowCount;i++){	
				//String[] insGids=fGids.split(",");
				for(int j=7;j<colCount;j++){
					IntArrayBean intB=new IntArrayBean();
					IntArrayCompositeKey cack = new IntArrayCompositeKey();
					cack.setDataset_id(dataset_id);
					cack.setDataorder_index(intDataOrderIndex);
					intB.setComKey(cack);
					//intB.setGid(Integer.parseInt(insGids[kk]));
					intB.setGid(Integer.parseInt(fGids.get(kk).toString()));
					intB.setMarker_id(Integer.parseInt(markers[m]));
					//chb.setAllele_raw_value((String)sheetData.getCell(j,i).getContents().trim());
					intB.setAllele_bin_value((String)sheetData.getCell(j,i).getContents().trim());
					kk++;
					g++;
					intDataOrderIndex++;
					session.save(intB);
					if (g % 1 == 0){
						session.flush();
						session.clear();
					}
					
				}
				kk=0;
				m++;
				g=0;
			}	
			//System.out.println("DONE");
			tx.commit();
			str="inserted";
		}catch(Exception e){
			tx.rollback();
			session.clear();
			e.printStackTrace();
		}finally{		    
			session.clear();							
		}
		return str;
	}

}
