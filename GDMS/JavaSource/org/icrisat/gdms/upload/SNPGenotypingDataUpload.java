/**
 * 
 */
package org.icrisat.gdms.upload;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.common.HibernateSessionFactory;
import org.icrisat.gdms.common.MaxIdValue;

public class SNPGenotypingDataUpload {
	String strupl="";
	private Session session;
	
	private Transaction tx;

	public SNPGenotypingDataUpload(){
		//session=HibernateSessionFactory.currentSession();
		session = HibernateSessionFactory.currentSession();
		tx=session.beginTransaction();
	
	}
	//getUpload method is used to insert the SNP Genotyping data from SNPGenotyping Template to database.	

	public String getUpload(HttpServletRequest request, String fname) throws SQLException{
		//System.out.println("fname="+fname);
		
		DatasetBean ub=new DatasetBean();
		UsersBean u=new UsersBean();
		GenotypeUsersBean usb=new GenotypeUsersBean();	
		ConditionsBean ubConditions=new ConditionsBean();
		
		HttpSession httpsession = request.getSession();
		int g1=0;
		String line; 
		String[] datavalue = null;
		String datatype="char";
		String dataset_type="SNP";
		String ins="";
		String strIns="";
		String strPI="";
		String strEmail="";
		String strDesc="";
		String strGenus="";
		String strSpecies="";
		String strMissData="";
		String strDate="";
		String IncPerson="";
		String PurposeStudy="";
		String[] gids=null;
		String[] genotype=null;
		List<List<String>> genoData = new ArrayList<List<String>>(); 
		int intDataOrderIndex = 1;
		int intRMarkerId=1;
		int mid=0;
		String charData="";
		String marker_type="SNP";
		String marker="";
		List<String> str=new ArrayList<String>();
		
		String ErrMsg="";
		int size=0;
		
		List genoDataMarkers = new ArrayList();
		String alertGN="no";
        String alertGID="no";
        String notMatchingData="";
        String notMatchingGIDS="";
        String notMatchingDataExists="";
		int maxMid=0;
		int gidCount=0;
		int gCount=0;int dataCount=0;
		try{
			
			MaxIdValue uptMethod=new MaxIdValue();
			int maxDatasetId=uptMethod.getMaxIdValue("dataset_id","dataset",session);
			int dataset_id=maxDatasetId+1;
			//System.out.println("....................:"+dataset_id);
			
			BufferedReader bReader = new BufferedReader(new FileReader(fname)); 
			while ((line = bReader.readLine()) != null) {
				datavalue =line.split("\t");	
								
				int len=datavalue.length;		
				
				if(line.startsWith("Institute")){
					//System.out.println("INS="+len);
					if(len==2) strIns=datavalue[1];
					if(len==1){
						//System.out.println("Length = 1 and null");
						ErrMsg = "Please provide the Institute";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
					if(len>2){ 	
						//System.out.println("Length greater than 2");
						ErrMsg = "There are extra tabs at line Institute";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
				}
				/*if((len==2)&&(datavalue[0].startsWith("Institute"))){
					strIns=datavalue[1]; 
				}*/
				if(line.startsWith("PI")){
					//System.out.println("PI"+len+"   line="+line);
					if(len==2) strPI=datavalue[1];
					if(len==1){
						//System.out.println("Length = 1 and null");
						ErrMsg = "Please provide the PI ";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
					if(len>2){ 	
						//System.out.println("Length greater than 2");
						ErrMsg = "There are extra tabs at line PI";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
				}		
				//if((len==2)&&(datavalue[0].contains("PI"))) strPI=datavalue[1];
				if(line.startsWith("Email")){
					//System.out.println("Email"+len);
					if(len==2) strEmail=datavalue[1];
					if(len==1){
						//System.out.println("Length = 1 and null");
						ErrMsg = "Please provide the Email ";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
					if(len>2){ 	
						//System.out.println("Length greater than 2");
						ErrMsg = "There are extra tabs at line Email";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
				}			
				//if((len==2)&&(datavalue[0].contains("Email"))) strEmail=datavalue[1];
				if(line.startsWith("Incharge_Person")){
					//System.out.println("INC Per="+len);
					if(len==2){
						//System.out.println("******************IP"+len);
						IncPerson=datavalue[1];
					}else if(len==1){
						//System.out.println("Length = 1 and null");
						ErrMsg = "Please provide the Incharge Person ";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}else if(len>2){ 	
						//System.out.println("Length greater than 2");
						ErrMsg = "There are extra tabs at line Incharge_Person";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
				}	
				//if((len==2)&&(datavalue[0].contains("Incharge_Person"))) IncPerson=datavalue[1];    			
				if(line.startsWith("Purpose_Of_Study")){
					//System.out.println("P OF Study"+len);
					if(len==2){ 
						//System.out.println("len"+len);
						PurposeStudy=datavalue[1];
						}
					if(len==1){
						//System.out.println("Length = 1 and null");
						ErrMsg = "Please provide the Purpose_Of_Study";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
					if(len>2){ 	
						//System.out.println("Length greater than 2");
						ErrMsg = "There are extra tabs at line Purpose_Of_Study";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
				}
				//if((len==2)&&(datavalue[0].contains("Purpose_Of_Study"))) PurposeStudy=datavalue[1];			
				if(line.startsWith("Description")){
					if(len==2) strDesc=datavalue[1];		
					if(len==1){
						//System.out.println("Length = 1 and null");
						ErrMsg = "Please provide the Description";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
					if(len>2){ 	
						//System.out.println("Length greater than 2");
						ErrMsg = "There are extra tabs at line Description";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
				}
				//if((len==2)&&(datavalue[0].contains("Description"))) strDesc=datavalue[1];
				if(line.startsWith("Genus")){
					if(len==2) strGenus=datavalue[1];	
					if(len==1){
						//System.out.println("Length = 1 and null");
						ErrMsg = "Please provide the Genus";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
					if(len>2){ 	
						//System.out.println("Length greater than 2");
						ErrMsg = "There are extra tabs at line Genus";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
				}	
				//if((len==2)&&(datavalue[0].contains("Genus"))) strGenus=datavalue[1];
				if(line.startsWith("Species")){
					if(len==2) strSpecies=datavalue[1];
					if(len==1){
						//System.out.println("Length = 1 and null");
						ErrMsg = "Please provide the Species";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
					if(len>2){ 	
						//System.out.println("Length greater than 2");
						ErrMsg = "There are extra tabs at line Species";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
				}				
				//if((len==2)&&(datavalue[0].contains("Species"))) strSpecies=datavalue[1];
				if(line.startsWith("Missing_Data")){
					if(len==2) strMissData=datavalue[1];
					if(len==1){
						//System.out.println("Length = 1 and null");
						ErrMsg = "Please provide the Missing_Data";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
					if(len>2){ 	
						//System.out.println("Length greater than 2");
						ErrMsg = "There are extra tabs at line Missing_Data";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
				}			
				//if((len==2)&&(datavalue[0].contains("Missing_Data"))) strMissData=datavalue[1];
				if(line.startsWith("Creation_Date")){
					if(len==2) strDate=datavalue[1];		
					if(len==1){
						//System.out.println("Length = 1 and null");
						ErrMsg = "Please provide the Creation_Date";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
					if(len>2){ 	
						//System.out.println("Length greater than 2");
						ErrMsg = "There are extra tabs at line Creation_Date";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
				}			
				
				if((len>2)&&(datavalue[0].startsWith("gid's"))){					
					len=datavalue.length;
					gidCount=len;
					for(int g=1;g<len;g++){						
						gids=datavalue;							
					}					
				}
				if((len>2)&&(datavalue[0].startsWith("Marker\\Genotype"))){	
					len=datavalue.length;
					gCount=len;
					for(int g=1;g<len;g++){
						genotype=datavalue;							
					}					
				}
				
				if((len>2)&&(!(datavalue[0].equals("Marker\\Genotype")))&&(!(datavalue[0].equals("gid's")))){
					dataCount=len;
					genoData.add(Arrays.asList(datavalue)); 			
					
					 genoDataMarkers.add(datavalue[0]);
				}				
			
			}
			//System.out.println("******:"+genoDataMarkers);
			if(gidCount<gCount){
				ErrMsg = "The number of GIDs is less than the number of Germplasm names provided";
				request.getSession().setAttribute("indErrMsg", ErrMsg);
				return "ErrMsg";
			}else if(gCount<gidCount){
				ErrMsg = "The number of GIDs is more than the number of Germplasm names provided";
				request.getSession().setAttribute("indErrMsg", ErrMsg);
				return "ErrMsg";
			}
			

			int user_id=uptMethod.getUserId("userid", "users", "uname", session,strPI);
			//System.out.println("user_id="+user_id);
			if(user_id==0){
				int maxUid=uptMethod.getMaxIdValue("userid","users",session);
				/*String ErrMsg = "PI doesnot exists in the database";
				request.getSession().setAttribute("indErrMsg", ErrMsg);
				return "ErrMsg";*/
				u.setUserid(maxUid+1);
				u.setUname(strPI);
				u.setUpswd("gdms");
				session.save(u);
				
			}
			if(gids.length!=genotype.length){
				strupl="notInserted";
			}else if(gids.length==genotype.length){
				String gidsForQuery = "";
				HashMap<Integer, String> GIDsMap = new HashMap<Integer, String>();
				
				 //SortedMap GIDsMap = new TreeMap();
	            for(int d=1;d<gids.length;d++){	               
	            	gidsForQuery = gidsForQuery + gids[d]+",";
	            	GIDsMap.put(Integer.parseInt(gids[d]), genotype[d]);
	            }
	            gidsForQuery=gidsForQuery.substring(0, gidsForQuery.length()-1);
	            //System.out.println("GIDsMap.."+GIDsMap);
	            Map<Object, String> sortedMap = new TreeMap<Object, String>(GIDsMap);
	            //System.out.println("%%%%%%%%%%%%%sortedMap=:"+sortedMap);
	            //HashMap<Object, String> map = new HashMap<Object, String>();
	            //ArrayList lstGIDs=uptMethod.getGIds("gid, germplasm_name", "germplasm_temp", "gid", session, gidsForQuery);
	            ArrayList lstGIDs=uptMethod.getGIds("gid, nval", "names", "gid", session, gidsForQuery);
	            SortedMap map = new TreeMap();
	            List lstgermpName = new ArrayList();
	            for(int w=0;w<lstGIDs.size();w++){
	                 Object[] strMareO= (Object[])lstGIDs.get(w);
	                 lstgermpName.add(strMareO[0]);
	                 String strMa123 = (String)strMareO[1];
	                 map.put(strMareO[0], strMa123);
	                 
	            }
	            Iterator iterator = map.keySet().iterator();
		        Iterator iterator1 = sortedMap.keySet().iterator();
	           //System.out.println("map=:"+map.size());
	           if(map.size()==0){
	        	   alertGID="yes";
	        	   size=0;
	           }
	            
	          
	           
	           while ((iterator.hasNext())&&(iterator1.hasNext())) {
	        	   Object key = iterator.next();
	        	   Object key1 = iterator1.next();
	        	   //System.out.println("key : " + key + " value :" + map.get(key));
	        	   if(key.equals(key1)){
	        		   //System.out.println("**************************************");
		        	   if(!(map.get(key).equals(sortedMap.get(key1)))){	
		        		   //notMatchingData=notMatchingData+sortedMap.get(key1)+",";
		        		   notMatchingGIDS=notMatchingGIDS+key1+",";
		        		   notMatchingDataExists=notMatchingDataExists+map.get(key)+",";
		        		   notMatchingData=notMatchingData+key1+"   "+map.get(key)+"\n\t";
		        		   alertGN="yes";      		   
		        	   }		        	   
	        	   }else{
	        		   //System.out.println("key : " + key + " value :" + map.get(key));
	        		   alertGID="yes";
	        		   size=map.size();
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
	        	   //ErrMsg = "Please verify the following GID/Germplasm(s) doesnot exists. \n Upload germplasm Information into GMS \n\t"+notMatchingGIDS;
	        	   request.getSession().setAttribute("indErrMsg", ErrMsg);
	        	   return "ErrMsg";
	           }
				/*for(int l=1;l<gids.length;l++){
					if(lstgermpName.contains(Integer.parseInt(gids[l]))){
						//System.out.println("gids Exists    :"+lstgermpName+"   "+gids[l]);
						String ErrMsg = "GID(s) already exists";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}else{
						//System.out.println("gids doesnot Exists    :"+lstgermpName+"   "+gids[l]);
						/*GermplasmBean gb=new GermplasmBean();					
						*//******************   GermplasmTemp   *********************//*	
						gb.setGid(Integer.parseInt(gids[l]));
						gb.setGermplasm_name(genotype[l]);
						session.save(gb);
	                    if (l % 1 == 0){
	                        session.flush();
	                        session.clear();
						}
					//System.out.println("gids  :"+lstgermpName+"   "+gids[l]);
					if(!(lstgermpName.contains(Integer.parseInt(gids[l])))){						
						String ErrMsg = "GID(s) doesnot exists. \n Please upload germplasm Information into GMS";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
				}	*/		
			}
			/*System.out.println("gids= "+gids.length);
			System.out.println("genotype = "+genotype.length);
			System.out.println("Data = "+genoData);
			System.out.println("Done");*/
			
			/** writing to 'dataset' table **/
			
			ub.setDataset_id(dataset_id);
			ub.setDataset_desc(strDesc);
			ub.setDataset_type(dataset_type);
			ub.setGenus(strGenus);
			ub.setSpecies(strSpecies);
			ub.setTemplate_date(strDate);					
			ub.setDatatype(datatype);
			//System.out.println("dataset id = ");
			session.saveOrUpdate(ub);
			
			//*************  dataset_users*************
			usb.setDataset_id(dataset_id);
			usb.setUser_id(user_id);
			
			session.save(usb);
			
			//*********** dataset_details;
			ubConditions.setDataset_id(dataset_id);
			ubConditions.setMissing_data(strMissData);
			
			session.saveOrUpdate(ubConditions);
			
			
			/*****  modified code for performance  *****/
			String marForQuery = "";
            for(int d=0;d<genoDataMarkers.size();d++){
               
                marForQuery = marForQuery +"'"+ genoDataMarkers.get(d)+"',";
            }
            marForQuery=marForQuery.substring(0, marForQuery.length()-1);
         
            HashMap<String, Object> map = new HashMap<String, Object>();
            ArrayList lstMarIdNames=uptMethod.getMarkerIds("marker_id, marker_name", "marker", "marker_name", session, marForQuery);
          
            List lstMarkers = new ArrayList();
            for(int w=0;w<lstMarIdNames.size();w++){
                 Object[] strMareO= (Object[])lstMarIdNames.get(w);
                 lstMarkers.add(strMareO[1]);
                 String strMa123 = (String)strMareO[1];
                 map.put(strMa123, strMareO[0]);
                 
            }
            
          
           maxMid=uptMethod.getMaxIdValue("marker_id","marker",session);		
           /*******************   END   *****************************/		
			
			
			for(int d=0;d<genoData.size();d++){					
				MarkerInfoBean mb=new MarkerInfoBean();				
				str=genoData.get(d);						
				//System.out.println(str);
				for(int s=0;s<str.size();s++){
					if(s==0){
						marker=str.get(0);							
						/*************   Modified for performance  *******************/						
						if(lstMarkers.contains(marker)){
							intRMarkerId=(Integer)(map.get(marker));
						}else{
							maxMid=maxMid+1;
							intRMarkerId=maxMid;
							mid=1;
						}
						//**************** END  **********************//
					}
					
					/***********  commented for performance  ****************/
					/*//System.out.println(s+"   "+marker+"    "+str.get(s)+"   "+genotype[s]+"   "+gids[s]);
					*//** retrieving marker id from marker table **//*
					mid=uptMethod.getUserId("marker_id", "marker", "marker_name", session, marker);
					//System.out.println("mid="+mid);
					
					*//**
					 * if marker doesnot exists in the marker table then retrieving max id & incrementing it by 1
					 * for inserting to the database 
					 *   
					 **//*
					
					if(mid==0){
						maxMid=uptMethod.getMaxIdValue("marker_id","marker",session);
						intRMarkerId=maxMid+1;
					}else{
						intRMarkerId=mid;
					}*/
					/*****    END   ********************/
					if(s!=0){
						CharArrayBean chb=new CharArrayBean();
						CharArrayCompositeKey cack = new CharArrayCompositeKey();
						//ReferenceBean ref=new ReferenceBean();					
						//ReferenceCompositeKey rbck = new ReferenceCompositeKey();							
						MarkerMetaDataBean mmb=new MarkerMetaDataBean();
						
						//**************** writing to char_values tables........
						cack.setDataset_id(dataset_id);
						cack.setDataorder_index(intDataOrderIndex);
						chb.setComKey(cack);
                        //System.out.println("........................"+str.get(s));
						if(str.get(s).equalsIgnoreCase("A")){
							charData="A:A";	
						}else if(str.get(s).equalsIgnoreCase("C")){	
							charData="C:C";
						}else if(str.get(s).equalsIgnoreCase("G")){
							charData="G:G";
						}else if(str.get(s).equalsIgnoreCase("T")){
							charData="T:T";
						}else if(str.get(s).equalsIgnoreCase("B")){
							charData="B:B";
						}else{
							charData=str.get(s);
						}
						//System.out.println(charData+"   "+gids[s]+"   "+intRMarkerId+"   "+genotype[s]);
						chb.setChar_value(charData);
						chb.setGid(Integer.parseInt(gids[s]));
						
						chb.setMarker_id(intRMarkerId);
						chb.setGermplasm_name(genotype[s]);
						session.save(chb);
						
						
						intDataOrderIndex++;
												
						if (d % 1 == 0){
							session.flush();
                            session.clear();
						}							
					}				
				}
				/** writing to 'marker' table */
				/// ******************** marker  ******************
				System.out.println("marker="+marker);
				if(mid==1){
					//int maxMid=uptMethod.getMaxIdValue("marker_id","marker",session);
					//intRMarkerId=maxMid+1;
					mb.setMarkerId(intRMarkerId);
					mb.setMarker_type(marker_type);
					mb.setMarker_name(marker);
					mb.setCrop(strSpecies);
					
					session.save(mb);
					 mid=0;
				}
					intRMarkerId++;
			}				
			tx.commit();
			
			
			strupl="inserted";
		}catch(Exception e){
			e.printStackTrace();
			session.clear();
			//con.rollback();
			tx.rollback();
		}finally{			
		    // Actual contact insertion will happen at this step
		    //session.flush();
		    //session.close();
			//con.close();
			session.clear();
		}
		
		return strupl;
	}

}
