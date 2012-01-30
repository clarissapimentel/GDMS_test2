package org.icrisat.gdms.upload;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.common.FileUploadToServer;
import org.icrisat.gdms.common.HibernateSessionFactory;
import org.icrisat.gdms.common.MaxIdValue;

import com.mysql.jdbc.Connection;

public class MappingDataUpload {
	private Session session;
	static int map_count=0;
		private Transaction tx;
		Connection con = null;
		public MappingDataUpload(){
			//session=HibernateSessionFactory.currentSession();
			 session = HibernateSessionFactory.currentSession();
			tx=session.beginTransaction();
		
		}
		
		public String setMappingDetails(HttpServletRequest request, String mapfile) throws SQLException{
			String result = "inserted";
			try{
				//upload the excel file
		
				String CropName="";
				String MapUnit="";			
				
				
				// Workbook code   				
				Workbook workbook=Workbook.getWorkbook(new File(mapfile));
				Sheet sheetMarkerDetails = workbook.getSheet(0);
				// All the Sheets
				// Excel sheet validations
				ExcelSheetValidations fv = new ExcelSheetValidations();
				String strFv=fv.validation(workbook, request,"Mapping");
				System.out.println("Valid="+strFv);
				if(!strFv.equals("valid"))
					return strFv;
				Cell cell=null;
				String str="";
				int ColCount = sheetMarkerDetails.getColumns();
				int intSRC=sheetMarkerDetails.getRows();
			    HttpSession hsession = request.getSession(true);
			    
				
				
//				/New Mapping insertion
				String markerType="UA";
				String mapType="genetic";
				String strMarkerName="";
				String LinkageGroup="" ;
				String Position=""; 
				String Query="";
				int count=0;
				int count1=0;
				int markerid_max=0;
		
				CropName=(String)sheetMarkerDetails.getCell(1, 2).getContents().trim();
				MapUnit=(String)sheetMarkerDetails.getCell(1, 3).getContents().trim();
				
				/** Retrieving maximum marker id from database table 'marker'  **/
				MaxIdValue uptMDsetId=new MaxIdValue();
				int maxMarkerId=uptMDsetId.getMaxIdValue("marker_id","marker",session);
				
				/** Retrieving maximum linkagemap_id from database table 'linkagemap' **/
				int maxLinkageMapId=uptMDsetId.getMaxIdValue("linkagemap_id","linkagemap",session);
				int linkageMapID=maxLinkageMapId+1;
				
				/** writing to database table 'linkagemap' **/
				
				LinkagemapBean linkagemap = new LinkagemapBean();
				linkagemap.setLinkagemap_id(linkageMapID);
				linkagemap.setLinkagemap_name((String)sheetMarkerDetails.getCell(1, 0).getContents().trim());
				linkagemap.setLinkagemap_type(mapType);
				session.saveOrUpdate(linkagemap);
				
				
				
				for(int i=6;i<intSRC;i++){
					/** writing to 'marker_linkagemap'  **/
					//map_count++;
					MarkerInfoBean markerInfo1 = new MarkerInfoBean();
					MarkerLinkagemapBean marker_linkage = new MarkerLinkagemapBean();
					
					strMarkerName = (String)sheetMarkerDetails.getCell(0, i).getContents().trim();
					LinkageGroup = (String)sheetMarkerDetails.getCell(1, i).getContents().trim();
					//int linkage_group= Integer.parseInt(LinkageGroup);
					Position = (String)sheetMarkerDetails.getCell(2, i).getContents().trim();
					//System.out.println("Position="+Position);
					double pos = Double.parseDouble(Position);
					/** insert into 'marker' table if marker doesn't exists **/
					List resultC= session.createQuery("select count(*) from MarkerInfoBean WHERE marker_name ='"+strMarkerName+"'").list();
					count= Integer.parseInt(resultC.get(0).toString()); 
							
						if(count==0){
							maxMarkerId=maxMarkerId+1;
							markerInfo1.setMarkerId(maxMarkerId);
							markerInfo1.setMarker_type(markerType);
							markerInfo1.setMarker_name(strMarkerName);
							markerInfo1.setCrop(CropName);
							session.saveOrUpdate(markerInfo1);
						}
						else{
							//System.out.println("........................marker EXITS");	
						}
							
						
						
						if(count==0){
							marker_linkage.setMarkerId(maxMarkerId);
						}else{
							List result1= session.createQuery("select markerId from MarkerInfoBean WHERE marker_name ='"+strMarkerName+"'").list();
							count1= Integer.parseInt(result1.get(0).toString()); 
							//System.out.println("count1 value is.....:"+count1);
							marker_linkage.setMarkerId(count1);
							
						}
						
						marker_linkage.setLinkagemap_id(linkageMapID);
						marker_linkage.setLinkage_group(LinkageGroup);
						marker_linkage.setStart_position(pos);
						marker_linkage.setEnd_position(pos);
						marker_linkage.setMap_unit(MapUnit);
						
						
						
						session.saveOrUpdate(marker_linkage);
						if (i % 1 == 0){
							session.flush();
							session.clear();
						}
				}				
				tx.commit();	
				
			}catch(Exception e){
			session.clear();
			//con.rollback();
			//tx.rollback();
			
			e.printStackTrace();
		}finally{
		    
			session.clear();
			
		      }
		return result;
		}

}
