package org.icrisat.gdms.upload;

import java.io.File;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jxl.Sheet;
import jxl.Workbook;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.common.HibernateSessionFactory;
import org.icrisat.gdms.common.MaxIdValue;

import com.mysql.jdbc.Connection;

public class QTLDataUpload {
	
	private Session session;
	private Transaction tx;
	Connection con = null;
	public QTLDataUpload(){
		session = HibernateSessionFactory.currentSession();
		tx=session.beginTransaction();	
	}
		
	public String setQTLData(HttpServletRequest request, String qtlfile) throws SQLException{
		String result = "inserted";
		String dataset_type="QTL";
		String datatype="int";
		try{
			MaxIdValue uptMId=new MaxIdValue();
			
			DatasetBean ub=new DatasetBean();
			GenotypeUsersBean usb=new GenotypeUsersBean();	
			UsersBean u=new UsersBean();
			String strSource="",strDatalist="";
			HttpSession httpsession = request.getSession();
			Workbook workbook=Workbook.getWorkbook(new File(qtlfile));
			String[] strSheetNames=workbook.getSheetNames();
			
			ExcelSheetValidations fv = new ExcelSheetValidations();
			String strFv=fv.validation(workbook, request,"QTL");
			System.out.println("Valid="+strFv);
			if(!strFv.equals("valid"))
				return strFv;
			
			for (int i=0;i<strSheetNames.length;i++){				
				if(strSheetNames[i].equalsIgnoreCase("QTL_Source"))
					strSource = strSheetNames[i];
				if(strSheetNames[i].equalsIgnoreCase("QTL_Data"))
					strDatalist = strSheetNames[i];					
			}
			Sheet sheetSource = workbook.getSheet(strSource);
						
			int intDatasetId=uptMId.getMaxIdValue("dataset_id","dataset",session);
			int dataset_id=intDatasetId+1;
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
			  
			 String curDate=year+"-"+mon+"-"+day;
			
			/** inserting to 'dataset' table  **/
			ub.setDataset_id(dataset_id);
			ub.setDataset_desc((String)sheetSource.getCell(1,4).getContents().trim());
			ub.setDataset_type(dataset_type);
			ub.setGenus(sheetSource.getCell(1,2).getContents().trim());
			ub.setSpecies(sheetSource.getCell(1,3).getContents().trim());
			ub.setTemplate_date(curDate);					
			ub.setDatatype(datatype);
			//System.out.println("dataset id = ");
			session.saveOrUpdate(ub);
			
			int user_id=uptMId.getUserId("userid", "users", "uname", session,sheetSource.getCell(1,1).getContents().trim());
			//System.out.println("user_id="+user_id);
			
			if(user_id==0){
				int maxUid=uptMId.getMaxIdValue("userid","users",session);
				/*String ErrMsg = "PI doesnot exists in the database";
				request.getSession().setAttribute("indErrMsg", ErrMsg);
				return "ErrMsg";*/
				u.setUserid(maxUid+1);
				u.setUname(sheetSource.getCell(1,1).getContents().trim());
				u.setUpswd("gdms");
				session.save(u);
				
			}
			
			
			//*************  dataset_users*************
			usb.setDataset_id(dataset_id);
			usb.setUser_id(user_id);
			
			session.save(usb);
			
			Sheet sheetData = workbook.getSheet(strDatalist);		
			
			int rowCount=sheetData.getRows();
			int colCount=sheetData.getColumns();
			int mapId=0;
			String linkMapId="";
			
			int intqtlId=uptMId.getMaxIdValue("qtl_id","qtl",session);
			//System.out.println("user_id="+user_id);
			int qtlId=intqtlId+1;
			
			
			for (int i=1;i<rowCount;i++){
				mapId=uptMId.getMapId("linkagemap_id", "linkagemap", "linkagemap_name", session,sheetData.getCell(2,i).getContents().trim());
				if(mapId!=0){
					linkMapId=linkMapId+mapId+",";	
				}else{
					String ErrMsg = "Map does not exists.\nPlease Upload the corresponding Map";
					request.getSession().setAttribute("indErrMsg", ErrMsg);
					return "ErrMsg";
				}
			}

			//System.out.println("mapID="+linkMapId);
			int l=0;
			String[] linkageMapID=linkMapId.split(",");
			for(int i=1;i<rowCount;i++){	
				/** reading from datasheet of template & writing to 'qtl' table  **/
				QTLBean qb=new QTLBean();	
				qb.setDataset_id(dataset_id);
				qb.setQtl_id(qtlId);
				qb.setQtl_name(sheetData.getCell(0, i).getContents().trim().toString());
				session.save(qb);	
				
				/** reading from datasheet of template & writing to 'qtl_linkagemap' **/
				QTLLinkageMapBean qtlb=new QTLLinkageMapBean();				
				qtlb.setLinkagemap_id(Integer.parseInt(linkageMapID[l]));
				qtlb.setQtl_id(qtlId);
				qtlb.setLinkage_group(sheetData.getCell(1, i).getContents().trim().toString());
				qtlb.setMin_position(Float.parseFloat(sheetData.getCell(4, i).getContents().trim().toString()));
				qtlb.setMax_position(Float.parseFloat(sheetData.getCell(5, i).getContents().trim().toString()));
				qtlb.setTrait(sheetData.getCell(6, i).getContents().trim().toString());
				qtlb.setExperiment(sheetData.getCell(7, i).getContents().trim().toString());
				qtlb.setLeft_flanking_marker(sheetData.getCell(9, i).getContents().trim().toString());
				qtlb.setRight_flanking_marker(sheetData.getCell(10, i).getContents().trim().toString());
				qtlb.setEffect(Float.parseFloat(sheetData.getCell(11, i).getContents().trim().toString()));
				qtlb.setLod(Float.parseFloat(sheetData.getCell(12, i).getContents().trim().toString()));
				qtlb.setR_square(Float.parseFloat(sheetData.getCell(13, i).getContents().trim().toString()));
				qtlb.setInteractions(sheetData.getCell(14, i).getContents().trim().toString());
					
			
				l++;
						
				session.save(qtlb);
				qtlId++;
				if (i % 1 == 0){
					session.flush();
					session.clear();
				}
			}
			
			tx.commit();
		}catch(Exception e){
			tx.rollback();
			session.clear();
			e.printStackTrace();
		}finally{		    
			session.clear();							
		}
		return result;	
	}

}
