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

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.common.HibernateSessionFactory;
import org.icrisat.gdms.common.MaxIdValue;


public class SSRGenotypingDataUpload {
	String str="";
	private Session session;
	
	private Transaction tx;	
	
	public String getUpload(HttpServletRequest request, String fname) throws SQLException{
		try{
			
			session = HibernateSessionFactory.currentSession();
			tx=session.beginTransaction();
			
			String alertGN="no";
	        String alertGID="no";
	        String notMatchingData="";
	        String notMatchingGIDS="";
	        String notMatchingDataExists="";
			
	        String ErrMsg = "";
			int intDataOrderIndex = 1;
			String dataset_type="SSR";
			String datatype="int";
			DatasetBean ub=new DatasetBean();
			GenotypeUsersBean usb=new GenotypeUsersBean();	
			ConditionsBean ubConditions=new ConditionsBean();
			CheckNumericDatatype cnd = new CheckNumericDatatype();
			UsersBean u=new UsersBean();
			
			//MetaDatasetBean ub1=new MetaDatasetBean();
			Workbook workbook=Workbook.getWorkbook(new File(fname));
			String[] strSheetNames=workbook.getSheetNames();
			
			///All the Sheets
			///Excel sheet validations
			ExcelSheetValidations fv = new ExcelSheetValidations();
			String strFv=fv.validation(workbook, request,"SSRG");
			//System.out.println("Valid="+strFv);
			if(!strFv.equals("valid"))
				return strFv;
			
			
			//avoids the case sensitive of sheet names
			String strSource="",strDatalist="";
			
			for (int i=0;i<strSheetNames.length;i++){				
				if(strSheetNames[i].equalsIgnoreCase("SSR_Source"))
					strSource = strSheetNames[i];
				if(strSheetNames[i].equalsIgnoreCase("SSR_Data List"))
					strDatalist = strSheetNames[i];					
			}
				
			int size=0;
			///timestamp code			
			String mon="";
			Calendar cal = new GregorianCalendar();
			int month = cal.get(Calendar.MONTH);
			int year = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			if(month>=10) 
				mon=String.valueOf(month+1);
			else 
				mon="0"+(month+1);
			  
			 String curDate=year+ "-" + mon + "-" +day;
			
			Sheet sheetSource = workbook.getSheet(strSource);
			
			Sheet sheetDataList=workbook.getSheet(strDatalist);
			int intDataListRowCount=sheetDataList.getRows();
			
			MaxIdValue uptMId=new MaxIdValue();		
			//getting the germplasm names and count of germplasm from Data List of Template
			List<String> listGIDs = new ArrayList<String>();
			List<String> listGNames = new ArrayList<String>();
			String strMarCheck = (String) sheetDataList.getCell(2,1).getContents().trim();
			String species=sheetSource.getCell(1,4).getContents().trim();
			for(int i=1;i<intDataListRowCount;i++){
				String strAmount=(String)sheetDataList.getCell(10,i).getContents().trim();
				float fltAmount = Float.parseFloat(strAmount);
				//System.out.println("floatval="+fltAmount+" == " +strMarCheck+" == "+(String)sheetDataList.getCell(1,i).getContents().trim());
				if(strMarCheck == (String)sheetDataList.getCell(2,i).getContents().trim()){
					if((fltAmount == 0.0) || (fltAmount == 1.0)){
						//System.out.println("IF............");
						
						//if((listGNames.contains(sheetDataList.getCell(0,1).getContents().trim())) && (fltAmount == 1.0))
						listGNames.add(sheetDataList.getCell(1,i).getContents().trim());
						listGIDs.add(sheetDataList.getCell(0,i).getContents().trim());
							//System.out.println("Acc:01="+(String)sheetDataList.getCell(0,i).getContents().trim());
					}else{
						//System.out.println("ELSE..............");
						//System.out.println("BOolean" +listGNames.contains(sheetDataList.getCell(0,i).getContents().trim()));
																			
						//if(!(listGNames.contains(sheetDataList.getCell(0,i).getContents().trim()))){
						listGNames.add(sheetDataList.getCell(1,i).getContents().trim());
						listGIDs.add(sheetDataList.getCell(0,i).getContents().trim());
							//System.out.println("contains="+(String)sheetDataList.getCell(0,i).getContents().trim());
						//}else{
							String curAcc = (String)sheetDataList.getCell(0,i).getContents().trim();
							String preAcc = (String)sheetDataList.getCell(0,i-1).getContents().trim();
							//String strPreAmount=(String)sheetDataList.getCell(9,i-1).getContents().trim();
							String strPreAmount=(String)sheetDataList.getCell(10,i-1).getContents().trim();
							double fltPreAmount = Float.parseFloat(strPreAmount);
														
							int fltA=0;
								for(int r=1;r<25;r++){
									double f = fltAmount*r;
									
									MaxIdValue rt = new MaxIdValue();
									double fltRB=rt.roundThree(f);
									if((fltRB>=0.900 && fltRB<=0.999))
										fltRB=Math.round(f);
									
									if(fltRB==1.000){
										//System.out.println("fltRB==1.000="+fltRB+"Rvalue="+r);
										fltA=r;
										r=25;
									}
								}							
							if(fltA!=0){
								i=i+fltA-1;								
							}					
					}		
				}else{					
					//strMarCheck=(String) sheetDataList.getCell(1,i).getContents().trim();
					strMarCheck=(String) sheetDataList.getCell(2,i).getContents().trim();
					i=i-1;
				}
			 }
			String gidsString="";
			ArrayList gidsList = new ArrayList();
			ArrayList gnamesList = new ArrayList();
			for(int g1=0;g1<listGIDs.size();g1++){
				if(!gidsList.contains(listGIDs.get(g1)))
					gidsList.add(listGIDs.get(g1));
			}
			//System.out.println("listGIDs="+listGIDs);
			//System.out.println("gidsList="+gidsList);
			for(int g2=0;g2<listGNames.size();g2++){
				if(!gnamesList.contains(listGNames.get(g2)))
					gnamesList.add(listGNames.get(g2));
			}
			System.out.println(gnamesList.size()+"    "+gidsList.size());
			int gCount=gnamesList.size();
			int gidCount=gidsList.size();
			if(gidCount<gCount){
				ErrMsg = "The number of GIDs is less than the number of Germplasm names provided";
				request.getSession().setAttribute("indErrMsg", ErrMsg);
				return "ErrMsg";
			}else if(gCount<gidCount){
				ErrMsg = "The number of GIDs is more than the number of Germplasm names provided";
				request.getSession().setAttribute("indErrMsg", ErrMsg);
				return "ErrMsg";
			}
			
			
				String gidsForQuery = "";
				HashMap<Integer, String> GIDsMap = new HashMap<Integer, String>();
				
				 //SortedMap GIDsMap = new TreeMap();
	            for(int d=1;d<gidsList.size();d++){	               
	            	gidsForQuery = gidsForQuery + gidsList.get(d)+",";
	            	GIDsMap.put((Integer.parseInt(gidsList.get(d).toString())), gnamesList.get(d).toString());
	            }
	            gidsForQuery=gidsForQuery.substring(0, gidsForQuery.length()-1);
	           // System.out.println("GIDsMap.."+GIDsMap);
	            Map<Object, String> sortedMap = new TreeMap<Object, String>(GIDsMap);
	            //System.out.println("%%%%%%%%%%%%%sortedMap=:"+sortedMap);
	            //HashMap<Object, String> map = new HashMap<Object, String>();
	            //ArrayList lstGIDs=uptMethod.getGIds("gid, germplasm_name", "germplasm_temp", "gid", session, gidsForQuery);
	            ArrayList lstGIDs=uptMId.getGIds("gid, nval", "names", "gid", session, gidsForQuery);
	            SortedMap gidsmap = new TreeMap();
	            List lstgermpName = new ArrayList();
	            for(int w=0;w<lstGIDs.size();w++){
	                 Object[] strMareO= (Object[])lstGIDs.get(w);
	                 lstgermpName.add(strMareO[0]);
	                 String strMa123 = (String)strMareO[1];
	                 gidsmap.put(strMareO[0], strMa123);
	                 
	            }
	            Iterator iterator = gidsmap.keySet().iterator();
		        Iterator iterator1 = sortedMap.keySet().iterator();
	           //System.out.println("map=:"+map.size());
	           if(gidsmap.size()==0){
	        	   alertGID="yes";
	        	   size=0;
	           }
	            
	           String markersForQuery="";
	           ArrayList markerList = new ArrayList();
				for(int m1=1;m1<intDataListRowCount;m1++){
					//markersForQuery=markersForQuery+"'"+sheetDataList.getCell(2,m1).getContents().trim()+"',";
					if(!markerList.contains(sheetDataList.getCell(2,m1).getContents().trim()))
						markerList.add(sheetDataList.getCell(2,m1).getContents().trim());
				}
	           System.out.println(markerList.size()+"  markers="+markerList);
	           for(int ml=0;ml<markerList.size();ml++){
	        	   markersForQuery=markersForQuery+"'"+markerList.get(ml)+"',";
	           }
	           markersForQuery=markersForQuery.substring(0, markersForQuery.length()-1);
	           while ((iterator.hasNext())&&(iterator1.hasNext())) {
	        	   Object key = iterator.next();
	        	   Object key1 = iterator1.next();
	        	   //System.out.println("key : " + key + " value :" + map.get(key));
	        	   if(key.equals(key1)){
	        		   //System.out.println("**************************************");
		        	   if(!(gidsmap.get(key).equals(sortedMap.get(key1)))){	
		        		   //notMatchingData=notMatchingData+sortedMap.get(key1)+",";
		        		   notMatchingGIDS=notMatchingGIDS+key1+",";
		        		   notMatchingDataExists=notMatchingDataExists+gidsmap.get(key)+",";
		        		   notMatchingData=notMatchingData+key1+"   "+gidsmap.get(key)+"\n\t";
		        		   alertGN="yes";      		   
		        	   }		        	   
	        	   }else{
	        		   //System.out.println("key : " + key + " value :" + map.get(key));
	        		   alertGID="yes";
	        		   size=gidsmap.size();
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
	        		   ErrMsg = "Please verify the following GID/Germplasm(s) provided doesnot exists. \n Upload germplasm Information into GMS \n \t"+notMatchingGIDS;
	        	   }
	        	   request.getSession().setAttribute("indErrMsg", ErrMsg);
	        	   return "ErrMsg";
	           }
	           
			
			
			/*for(int g=0;g<gnamesList.size();g++){
				GermplasmBean gb= new GermplasmBean();
				gb.setGid(Integer.parseInt(gidsList.get(g).toString()));
				gb.setGermplasm_name(gnamesList.get(g).toString());
				session.save(gb);
			}*/
	           SortedMap map = new TreeMap();
	           SortedMap finalMarkersMap = new TreeMap();
	           HashMap<String, Object> markerMap = new HashMap<String, Object>();
	            ArrayList lstMarIdNames=uptMId.getMarkerIds("marker_id, marker_name", "marker", "marker_name", session, markersForQuery);
	          
	            List lstMarkers = new ArrayList();
	            for(int w=0;w<lstMarIdNames.size();w++){
	                 Object[] strMareO= (Object[])lstMarIdNames.get(w);
	                 lstMarkers.add(strMareO[1]);
	                 String strMa123 = (String)strMareO[1];
	                 map.put(strMa123, strMareO[0]);
	                 
	            }
	            int markerId=0;
	            String marker="";
	            int maxMarkerId=uptMId.getMaxIdValue("marker_id","marker",session);
	           
	           
////		Retrieve the maximum column id from the database
			
			int intDatasetId=uptMId.getMaxIdValue("dataset_id","dataset",session);
			int dataset_id=intDatasetId+1;
			//** writing to 'dataset' table **//*
			ub.setDataset_id(dataset_id);
			ub.setDataset_desc((String)sheetSource.getCell(1,2).getContents().trim());
			ub.setDataset_type(dataset_type);
			ub.setGenus(sheetSource.getCell(1,3).getContents().trim());
			ub.setSpecies(species);
			ub.setTemplate_date(curDate);					
			ub.setDatatype(datatype);
			//System.out.println("dataset id = ");
			session.saveOrUpdate(ub);
			
			int user_id=uptMId.getUserId("userid", "users", "uname", session,sheetSource.getCell(1,1).getContents().trim());
			//System.out.println("user_id="+user_id);
			/*if(user_id==0){
				int maxUid=uptMId.getMaxIdValue("userid","users",session);
				ErrMsg = "PI doesnot exists in the database";
				request.getSession().setAttribute("indErrMsg", ErrMsg);
				return "ErrMsg";
				u.setUserid(maxUid+1);
				u.setUname(sheetSource.getCell(1,1).getContents().trim());
				u.setUpswd("gdms");
				session.save(u);
			}*/
			//*************  writing to dataset_users table*************
			usb.setDataset_id(dataset_id);
			usb.setUser_id(user_id);
			
			session.save(usb);
			
			//*********** writing to dataset_details table **********
			ubConditions.setDataset_id(dataset_id);
			ubConditions.setMissing_data(sheetSource.getCell(1,5).getContents().trim());
			
			session.saveOrUpdate(ubConditions);	
			
			//getting the marker names and count from Data List of Template
			
			
			String markers="";
			int mid=0;
			//String marker="";
			int count=0;
			//int maxMarkerId=uptMId.getMaxIdValue("marker_id","marker",session);
			
			if (sheetDataList==null){
				System.out.println("Empty Sheet");		
			}else{
				int intNR=sheetDataList.getRows();			
				int intColRowEmpty=0;			
				for(int i=0;i<intNR;i++){
					Cell c=sheetDataList.getCell(0,i);
					String s=c.getContents();
					if(!s.equals("")){
						intColRowEmpty=intColRowEmpty + 1;						
					}
				}

				int m=0;
				
				
				//End marker metadataset insertion //
				for(int i=1;i<intColRowEmpty;i++){					
					MarkerInfoBean mib=new MarkerInfoBean();
					marker=sheetDataList.getCell(2,i).getContents().trim();
					//mid=uptMId.getUserId("marker_id", "marker", "marker_name", session,sheetDataList.getCell(2,i).getContents().trim());
					if(lstMarkers.contains(marker)){						
						maxMarkerId=(Integer)(map.get(marker));
						mid=0;
					}else{						
						mid=1;
						if(!(finalMarkersMap.containsValue(marker))){
							maxMarkerId=maxMarkerId+1;
							finalMarkersMap.put(maxMarkerId,marker);
						}
						//System.out.println("........................marker EXITS   "+maxMarkerId);	
					}
					
					IntArrayBean intb=new IntArrayBean();
					IntArrayCompositeKey intcomk=new IntArrayCompositeKey();
					
					intcomk.setDataset_id(dataset_id);
					intcomk.setDataorder_index(intDataOrderIndex);
					intb.setComKey(intcomk);					
					intb.setMarker_id(maxMarkerId);
					//System.out.println("88888888888888888888888888 "+m1[m]);
					intb.setGid(Integer.parseInt(listGIDs.get(m)));
					
					String strV = (String)sheetDataList.getCell(5,i).getContents().trim();
					String strRV = (String)sheetDataList.getCell(6,i).getContents().trim();
					String strAmountVal = (String)sheetDataList.getCell(10,i).getContents().trim();
					int intAlleleBinValues = 0;
					float intAlleleRawValues = 0;
					if(cnd.isInteger(strV)){
						intAlleleBinValues = Integer.parseInt(sheetDataList.getCell(5,i).getContents().trim());
					}else{
						String str=(String)sheetDataList.getCell(5,i).getContents().trim();
						if(str.equalsIgnoreCase("?")){
							intAlleleBinValues=999999999;
						}else{
							intAlleleBinValues=88888888;
						}
					}
					
					if(cnd.isFloat(strRV)){
						intAlleleRawValues = Float.parseFloat(sheetDataList.getCell(6,i).getContents().trim());
					}else{
						String str=(String)sheetDataList.getCell(6,i).getContents().trim();
						if(str.equalsIgnoreCase("?")){
							intAlleleRawValues=999999999;
						}else{
							intAlleleRawValues=88888888;
						}
					}
					//check the amount value and insert the data into database 
					//without using amount value
					
						if(strAmountVal.equals("1")){
							//System.out.println("strAmountval="+strAmountVal);
							String strValue = intAlleleBinValues+":"+intAlleleBinValues;
							String strRValue = intAlleleRawValues+":"+intAlleleRawValues;
							
							intb.setAllele_bin_value(strValue);
							intb.setAllele_raw_value(strRValue);						
						}else{
							
							String strValue1="";
							String strRValue1="";
							////////////////////tr	
							//amout value 
							String strA = (String)sheetDataList.getCell(10,i).getContents().trim();
							int intAmoutVal = 0;
							for(int l=1;l<17;l++){
								Float val;
								val = Float.parseFloat(strA) * l;
								if(val >= 0.9){
									intAmoutVal = l;
									break;
								}
							}
							intAmoutVal = intAmoutVal +i;
							
							for(int n=i;n<intAmoutVal;n++){
								String strV1 = (String)sheetDataList.getCell(5,n).getContents().trim();
								String strRV1 = (String)sheetDataList.getCell(6,n).getContents().trim();
								int intAlleleBinValues1 = 0;
								float intAlleleRawValues1 = 0;
								if(cnd.isInteger(strV1)){
									intAlleleBinValues1 = Integer.parseInt(sheetDataList.getCell(5,n).getContents().trim());
								}else{
									String str=(String)sheetDataList.getCell(5,n).getContents().trim();
									if(str.equalsIgnoreCase("?")){
										intAlleleBinValues1=999999999;
									}else{
										intAlleleBinValues1=88888888;
									}
								}
								
								if(cnd.isFloat(strRV1)){
									intAlleleRawValues1 = Float.parseFloat(sheetDataList.getCell(6,n).getContents().trim());
								}else{
									String str=(String)sheetDataList.getCell(6,n).getContents().trim();
									if(str.equalsIgnoreCase("?")){
										intAlleleRawValues1=999999999;
									}else{
										intAlleleRawValues1=88888888;
									}
								}			
								
								 strValue1 = strValue1+intAlleleBinValues1+":";
								 strRValue1 = strRValue1+intAlleleRawValues1+":";
								 i++;
							}
							i--;
							
							strValue1=strValue1.substring(0, strValue1.length()-1);
							strRValue1=strRValue1.substring(0, strRValue1.length()-1);
							
							//////////////////////////////////////////////////
							
							//System.out.println("strRValue1="+strRValue1);
							//System.out.println("strValue1="+strValue1);
							
							intb.setAllele_bin_value(strValue1);
							intb.setAllele_raw_value(strRValue1);
							
						}
						intDataOrderIndex++;
						m++;
						session.save(intb);								
						if (i % 1 == 0){
							session.flush();
							session.clear();
						}
				}
				if(mid==1){
					
						Iterator iteratorM = finalMarkersMap.keySet().iterator();						
						while (iteratorM.hasNext()) {	
							MarkerInfoBean mib=new MarkerInfoBean();
							int key = Integer.parseInt(iteratorM.next().toString());						
							//System.out.println("Error " + key + " means " + finalMarkersMap.get(key));
							mib.setMarkerId(key);
							mib.setMarker_type(dataset_type);
							mib.setMarker_name(finalMarkersMap.get(key).toString());
							mib.setCrop(species);
							session.saveOrUpdate(mib);						
						}
				}
				//con.commit();
				
				tx.commit();
			
			//System.out.println("File Uploaded:"+finalMarkersMap);
			
		}
		str="inserted";
			
		
		}catch(Exception e){
			session.clear();			
			tx.rollback();			
			e.printStackTrace();
		}finally{
			// Actual contact insertion will happen at this step
		    //session.flush();
		    //session.close();
			//session.clear();

		}
		return str;
	}

}
