package org.icrisat.gdms.upload;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jxl.Sheet;
import jxl.Workbook;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.upload.ExcelSheetColumnName;
import org.icrisat.gdms.common.HibernateSessionFactory;
import org.icrisat.gdms.common.MaxIdValue;

import com.mysql.jdbc.Connection;

public class SSRMarkerInfoUpload {
	//to insert the data of SSR Markers
	//Feild names from the template
	private Session session;
	static int map_count=0;
		private Transaction tx;
		Connection con = null;
		public SSRMarkerInfoUpload(){
			//session=HibernateSessionFactory.currentSession();
			 session = HibernateSessionFactory.currentSession();
			tx=session.beginTransaction();
		
		}
		
		public String setMarkerDetails(HttpServletRequest request, String ssrfile) throws SQLException{
			String strResult = "inserted";
			try{
				HttpSession hsession = request.getSession();
				Workbook workbook=Workbook.getWorkbook(new File(ssrfile));
				String[] strSheetNames=workbook.getSheetNames();
				
				///Sheet Names display
				String[] strArrSheetNames=null;
				String strSheetName = "";
				for (int i=0;i<strSheetNames.length;i++){
					if(strSheetNames[i].equalsIgnoreCase("SSRMarkers"))
						strSheetName = strSheetNames[i];
				}
				Sheet sheetMarkerDetails = workbook.getSheet(strSheetName);
				//Marker Template validations
				String sheetNameCheck = "";			
				if(strSheetName.equals("")){
					return strResult = "SheetNameNotFound";	
				}
				int ColCount = sheetMarkerDetails.getColumns();
				int intSRC=sheetMarkerDetails.getRows();
				String strTempColumnNames[] ={"Marker Name","Alias (comma separated for multiple names)","Crop","Genotype","Ploidy","GID","Principal Investigator","Contact","Institute","Incharge Person","Assay Type","Repeat","No of Repeats","SSR Type","Sequence","Sequence Length","Min Allele","Max Allele","SSR number","Size of Repeat Motif","Forward Primer","Reverse Primer","Product Size","Primer Length","Forward Primer Temperature","Reverse Primer Temperature","Annealing Temperature","Elongation Temperature","Fragment Size Expected","Fragment Size Observed","Amplification","Reference"};
				//String strTempColumnNames[] = {"Marker Name","Marker Type","Principal Investigator","Contact","Institute","Incharge Person","publication","Crop","Accession ID","Genotype","Chromosome","Map","Position","Cmap ID","Repeat","No of Repeats","SSR Start","SSR End","SSR Type","Sequence","Sequence Length","Min Allele","Max Allele","SSR number","Size of Repeat Motif","Forward Primer","Reverse Primer","Product Size","Primer Length","Forward Primer Temperature","Reverse Primer Temperature","Annealing Temperature","Elongation Temperature","Fragment Size Expected","Fragment Size Observed","Amplification","Reference"};
				for(int j=0;j<strTempColumnNames.length;j++){
					String strMFieldNames = (String)sheetMarkerDetails.getCell(j, 0).getContents().trim();
					//checking the feild names to match the names in template
					if(strMFieldNames.equalsIgnoreCase(""))
						strMFieldNames = "Empty";
					if(!strTempColumnNames[j].toLowerCase().contains(strMFieldNames.toLowerCase())){
						hsession.setAttribute("colMsg", strMFieldNames);
						hsession.setAttribute("colMsg1", strTempColumnNames[j]);
						hsession.setAttribute("sheetName", strSheetName);
						return strResult="ColumnNameNotFound";
					}
				}
				
				
				//check the Marker Name, Crop Name, Principal Investigator, Institue, Forward Primer and Reverse Primer fields in sheet
				for(int i=1;i<intSRC;i++){
					String strMarkerName = (String)sheetMarkerDetails.getCell(0, i).getContents().trim();
					String strCropName = (String)sheetMarkerDetails.getCell(2, i).getContents().trim();	
					String strPrincipalInves=(String)sheetMarkerDetails.getCell(6, i).getContents().trim();	
					String strInstitute=(String)sheetMarkerDetails.getCell(8, i).getContents().trim();
					String strForwaredPrimer=(String)sheetMarkerDetails.getCell(20, i).getContents().trim();
					String strReversePrimer=(String)sheetMarkerDetails.getCell(21, i).getContents().trim();
					boolean bCrop = false;
					if(strMarkerName.equals("")||strMarkerName==null){
						ExcelSheetColumnName escn =  new ExcelSheetColumnName();
						String strColName = escn.getColumnName(sheetMarkerDetails.getCell(0, i).getColumn());							
						String ErrMsg = " Provide Marker name at cell position "+strColName+(sheetMarkerDetails.getCell(0, i).getRow()+1);
						hsession.setAttribute("indErrMsg", ErrMsg);							
						return strResult = "ErrMsg";
					}else{ //	bCrop = !strCropName.equals("");						
						if(strCropName.equals("")||strCropName==null){
							ExcelSheetColumnName escn =  new ExcelSheetColumnName();
							String strColName = escn.getColumnName(sheetMarkerDetails.getCell(2, i).getColumn());							
							String ErrMsg = "Provide the Species derived from for Marker "+strMarkerName+" at cell position "+strColName+(sheetMarkerDetails.getCell(2, i).getRow()+1);
							hsession.setAttribute("indErrMsg", ErrMsg);							
							return strResult = "ErrMsg";							
						}else if(strPrincipalInves.equals("")||strPrincipalInves==null){
							ExcelSheetColumnName escn =  new ExcelSheetColumnName();
							String strColName = escn.getColumnName(sheetMarkerDetails.getCell(6, i).getColumn());							
							String ErrMsg = "Provide the Principal Investigator for Marker "+strMarkerName+" at cell position "+strColName+(sheetMarkerDetails.getCell(6, i).getRow()+1);
							hsession.setAttribute("indErrMsg", ErrMsg);							
							return strResult = "ErrMsg";
						}else if(strInstitute.equals("")||strInstitute==null){
							ExcelSheetColumnName escn =  new ExcelSheetColumnName();
							String strColName = escn.getColumnName(sheetMarkerDetails.getCell(8, i).getColumn());							
							String ErrMsg = "Provide the Institute for Marker "+strMarkerName+" at cell position "+strColName+(sheetMarkerDetails.getCell(8, i).getRow()+1);
							hsession.setAttribute("indErrMsg", ErrMsg);							
							return strResult = "ErrMsg";
						}else if(strForwaredPrimer.equals("")||strForwaredPrimer==null){
							ExcelSheetColumnName escn =  new ExcelSheetColumnName();
							String strColName = escn.getColumnName(sheetMarkerDetails.getCell(20, i).getColumn());							
							String ErrMsg = "Provide the Forward Primer value for Marker "+strMarkerName+" at cell position "+strColName+(sheetMarkerDetails.getCell(20, i).getRow()+1);
							hsession.setAttribute("indErrMsg", ErrMsg);							
							return strResult = "ErrMsg";
						}else if(strReversePrimer.equals("")||strReversePrimer==null){
							ExcelSheetColumnName escn =  new ExcelSheetColumnName();
							String strColName = escn.getColumnName(sheetMarkerDetails.getCell(21, i).getColumn());							
							String ErrMsg = "Provide the Reverse Primer value for Marker "+strMarkerName+" at cell position "+strColName+(sheetMarkerDetails.getCell(21, i).getRow()+1);
							hsession.setAttribute("indErrMsg", ErrMsg);							
							return strResult = "ErrMsg";
						}
					}
					
				}			
				
				///Get the Marker names from template and adding the Marker and Crop names to the List object.
				List<String> listTempMarkerNames= new ArrayList<String>();
				List<String> listCropNames = new ArrayList<String>();
				//List<String> listMarkerType= new ArrayList<String>();
				String strCropNames = "";
				String strC = "";
				//String mtype="";
				for(int i=1;i<intSRC;i++){
					String strMName = (String)sheetMarkerDetails.getCell(0,i).getContents().trim();
					String strCName = (String)sheetMarkerDetails.getCell(2,i).getContents().trim();
					//String MarkerTyp=(String)sheetMarkerDetails.getCell(1,i).getContents().trim();
					//if(!strMName.equals("") && !strCName.equals("")){
					String str=sheetMarkerDetails.getCell(0,i).getContents().trim()+"!`!"+sheetMarkerDetails.getCell(2,i).getContents().trim()+"!`!SSR";
					listTempMarkerNames.add(str.toLowerCase());
					strC = sheetMarkerDetails.getCell(2,i).getContents().trim();
					//mtype=sheetMarkerDetails.getCell(1,i).getContents().trim();
					if(!listCropNames.contains(strC.toLowerCase())){
						strCropNames = strCropNames +"'"+ sheetMarkerDetails.getCell(2,i).getContents().trim() +"'"+",";	
						listCropNames.add(strC.toLowerCase());
					}
					/*if(listMarkerType.contains(mtype.toLowerCase())){
						listMarkerType.add(mtype.toLowerCase());
					}	*/					
					//}
				}
				
				///Retrieving the marker names from the database					
				Query rsMarkerNames=session.createQuery("from MarkerInfoBean where Lower(crop) in("+strCropNames.substring(0, strCropNames.length()-1).toLowerCase()+") and marker_type='SSR'");				
				List result= rsMarkerNames.list();				
				Iterator it=result.iterator();
				List<String> listDBMarkerNames = new ArrayList<String>();				
				///concatenate Marker name with crop name, Marker tpe and adding to List object.
				while(it.hasNext()){
					MarkerInfoBean uMarkerInfo= (MarkerInfoBean)it.next();					
					String strMC=uMarkerInfo.getMarker_name()+"!`!"+uMarkerInfo.getCrop()+"!`!"+uMarkerInfo.getMarker_type();					
					listDBMarkerNames.add(strMC.toLowerCase());					
				}								
				///Database and Template Marker names comparision
				Object objCom=null;
				Iterator itCom;				
				itCom=listTempMarkerNames.iterator();
				List<String> listNewMarkers = new ArrayList<String>();
				String strDupMarkers = "";
				while(itCom.hasNext()){
					objCom=itCom.next();
					if(!listDBMarkerNames.contains(objCom)){						
						String str=(String)objCom;
						listNewMarkers.add(str.toLowerCase());
					}else{
						strDupMarkers = strDupMarkers + (String)objCom +",";
						strDupMarkers=strDupMarkers.replaceAll("!`!", ":");
					}
				}
				//Message will be displayed when the marker(s) are already exists in the database.
				if(listNewMarkers.size()==0){
					String ErrMsg = "All the marker(s) already exists in the database";
					hsession.setAttribute("indErrMsg", ErrMsg);					
					return strResult = "ErrMsg";
				}	
				
				///New Markers insertion
				
				
				//ChromosomeBean chromosome=null;
				
				//Retrieve the maximum marker_id from the marker_details table.
				MaxIdValue uptMDsetId=new MaxIdValue();
				int MarkerID=0;
				int maxMarkerId=uptMDsetId.getMaxIdValue("marker_id","marker",session);
				//System.out.println("Max Marker Id =="+maxMarkerId);
					//maxMarkerId++;
				String MarkerType="",annealing_temp="",reverse_primer_temp="",forward_primer_temp="";
				boolean	exists=false;
				int No_of_repeats,Sequence_length,Min_allele,Max_allele,Ssr_nr,Size_of_repeat_motif,Product_size,Primer_length,Fragment_size_expected,Fragment_size_observed;
				double Elongation_temp;
				for(int r=1;r<intSRC;r++){	
					exists=false;
					forward_primer_temp="";
					reverse_primer_temp="";
					annealing_temp="";
					//String strMName = (String)sheetMarkerDetails.getCell(0,r).getContents().trim();
					//String strCName = (String)sheetMarkerDetails.getCell(2,r).getContents().trim();
					MarkerType="SSR";
					
					String str=sheetMarkerDetails.getCell(0,r).getContents().trim()+"!`!"+sheetMarkerDetails.getCell(2,r).getContents().trim()+"!`!SSR";					
					if(listNewMarkers.contains(str.toLowerCase())){						
						MarkerID=maxMarkerId++;
					}else{
						//retrieving MarkerID for the already existing marker from MarkerInfo table
						List listValues=session.createQuery("select markerId from MarkerInfoBean where Lower(marker_name) ='"+(String)sheetMarkerDetails.getCell(0,r).getContents().trim().toLowerCase()+"' and Lower(crop)='"+(String)sheetMarkerDetails.getCell(2,r).getContents().trim().toLowerCase()+"' and marker_type='SSR'").list();
						Iterator itList=listValues.iterator();
						Object obj=null;			
						while(itList.hasNext()){
							obj=itList.next();
							MarkerID=Integer.parseInt(obj.toString());
							//checking if the data for marker exists in SSR Marker Table
							List MarkerInSSR=session.createQuery("from SSRMarkerBean where marker_id ="+MarkerID).list();
							Iterator MarkerInSSRList=MarkerInSSR.iterator();
							if(MarkerInSSRList.hasNext()){
								exists=true;
							}
						}
					}
					if(exists==true){
						continue;
					}
					//System.out.println("MarkerID =="+MarkerID);
					//Adding data to MarkerInfo table
					SSRMarkerBean SSRMarker=null;
					MarkerInfoBean markerInfo=null;
					MarkerAliasBean markerAlias=null;
					MarkerUserInfoBean markerUserInfo=null;
						markerInfo=new MarkerInfoBean();
						markerInfo.setMarkerId(MarkerID);
						//markerInfo.setMarker_type_id(MarkerTypeID);
						markerInfo.setMarker_type(MarkerType);
						markerInfo.setMarker_name(sheetMarkerDetails.getCell(0,r).getContents().trim());
						markerInfo.setCrop(sheetMarkerDetails.getCell(2,r).getContents().trim());
						markerInfo.setAccession_id(sheetMarkerDetails.getCell(5,r).getContents().trim());
						markerInfo.setReference(sheetMarkerDetails.getCell(31,r).getContents().trim());
						markerInfo.setGenotype(sheetMarkerDetails.getCell(3,r).getContents().trim());
						markerInfo.setPloidy(sheetMarkerDetails.getCell(4,r).getContents().trim());
						
						//Adding data to marker_alias Table
						markerAlias=new MarkerAliasBean();
						markerAlias.setMarkerId(MarkerID);
						markerAlias.setAlias(sheetMarkerDetails.getCell(1,r).getContents().trim());						
						
						//Adding data to marker_user_info
						markerUserInfo=new MarkerUserInfoBean();						
						markerUserInfo.setMarker_id(MarkerID);
						markerUserInfo.setPrincipal_investigator(sheetMarkerDetails.getCell(6,r).getContents().trim());
						markerUserInfo.setContact(sheetMarkerDetails.getCell(7,r).getContents().trim());
						markerUserInfo.setInstitute(sheetMarkerDetails.getCell(8,r).getContents().trim());
						//markerUserInfo.setPublication(sheetMarkerDetails.getCell(6,r).getContents().trim());
						markerUserInfo.setIncharge_person(sheetMarkerDetails.getCell(9,r).getContents().trim());
								
						//Adding data to SSRMarker Table
						SSRMarker=new SSRMarkerBean();						
						SSRMarker.setMarker_id(MarkerID);
						SSRMarker.setAssay_type(sheetMarkerDetails.getCell(10,r).getContents().trim());
						SSRMarker.setRepeats(sheetMarkerDetails.getCell(11,r).getContents().trim());
						if(sheetMarkerDetails.getCell(12,r).getContents().equals("")){
							No_of_repeats=0;
						}else{
							No_of_repeats=Integer.parseInt(sheetMarkerDetails.getCell(12,r).getContents().trim());
						}
						SSRMarker.setNo_of_repeats(No_of_repeats);
						//SSRMarker.setSsr_start(sheetMarkerDetails.getCell(16,r).getContents().trim());
						//SSRMarker.setSsr_end(sheetMarkerDetails.getCell(17,r).getContents().trim());
						SSRMarker.setSsr_type(sheetMarkerDetails.getCell(13,r).getContents().trim());
						SSRMarker.setSequence(sheetMarkerDetails.getCell(14,r).getContents().trim());
						if(sheetMarkerDetails.getCell(15,r).getContents().equals("")){
							Sequence_length=0;
						}else{
							Sequence_length=Integer.parseInt(sheetMarkerDetails.getCell(15,r).getContents().trim());							
						}
						SSRMarker.setSequence_length(Sequence_length);
						if(sheetMarkerDetails.getCell(16,r).getContents().equals("")){
							Min_allele=0;						
						}else{
							Min_allele=Integer.parseInt(sheetMarkerDetails.getCell(16,r).getContents().trim());
						}
						SSRMarker.setMin_allele(Min_allele);
						if(sheetMarkerDetails.getCell(17,r).getContents().equals("")){
							Max_allele=0;
						}else{
							Max_allele=Integer.parseInt(sheetMarkerDetails.getCell(17,r).getContents().trim());
						}
						SSRMarker.setMax_allele(Max_allele);
						if(sheetMarkerDetails.getCell(18,r).getContents().equals("")){
							Ssr_nr=0;
						}else{
							Ssr_nr=Integer.parseInt(sheetMarkerDetails.getCell(18,r).getContents().trim());
						}
						SSRMarker.setSsr_nr(Ssr_nr);
						if(sheetMarkerDetails.getCell(19,r).getContents().equals("")){
							Size_of_repeat_motif=0;
						}else{
							Size_of_repeat_motif=Integer.parseInt(sheetMarkerDetails.getCell(19,r).getContents().trim());
						}
						SSRMarker.setSize_of_repeat_motif(Size_of_repeat_motif);
						SSRMarker.setForward_primer(sheetMarkerDetails.getCell(20,r).getContents().trim());
						SSRMarker.setReverse_primer(sheetMarkerDetails.getCell(21,r).getContents().trim());
						if(sheetMarkerDetails.getCell(22,r).getContents().equals("")){
							Product_size=0;
						}else{
							Product_size=Integer.parseInt(sheetMarkerDetails.getCell(22,r).getContents().trim());
						}
						SSRMarker.setProduct_size(Product_size);
						if(sheetMarkerDetails.getCell(23,r).getContents().equals("")){
							Primer_length=0;
						}else{
							Primer_length=Integer.parseInt(sheetMarkerDetails.getCell(23,r).getContents().trim());
						}
						SSRMarker.setPrimer_length(Primer_length);
						if(sheetMarkerDetails.getCell(24,r).getContents().equals("")){
							forward_primer_temp="0";
						}else{
							forward_primer_temp=sheetMarkerDetails.getCell(24,r).getContents().trim();
						}
						if(sheetMarkerDetails.getCell(25,r).getContents().equals("")){
							reverse_primer_temp="0";
						}else{
							reverse_primer_temp=sheetMarkerDetails.getCell(25,r).getContents().trim();
						}
						if(sheetMarkerDetails.getCell(26,r).getContents().equals("")){
							annealing_temp="0";
						}else{
							annealing_temp=sheetMarkerDetails.getCell(26,r).getContents().trim();
						}
						SSRMarker.setForward_primer_temp(Double.parseDouble(forward_primer_temp));
						SSRMarker.setReverse_primer_temp(Double.parseDouble(reverse_primer_temp));
						SSRMarker.setAnnealing_temp(Double.parseDouble(annealing_temp));
						if(sheetMarkerDetails.getCell(27,r).getContents().equals("")){
							Elongation_temp=0.0;
						}else{
							Elongation_temp=Double.parseDouble(sheetMarkerDetails.getCell(27,r).getContents().trim());
						}
						SSRMarker.setElongation_temp(Elongation_temp);
						if(sheetMarkerDetails.getCell(28,r).getContents().equals("")){
							Fragment_size_expected=0;
						}else{
							Fragment_size_expected=Integer.parseInt(sheetMarkerDetails.getCell(28,r).getContents().trim());
						}
						SSRMarker.setFragment_size_expected(Fragment_size_expected);
						if(sheetMarkerDetails.getCell(29,r).getContents().equals("")){
							Fragment_size_observed=0;
						}else{
							Fragment_size_observed=Integer.parseInt(sheetMarkerDetails.getCell(29,r).getContents().trim());
						}
						SSRMarker.setFragment_size_observed(Fragment_size_observed);
						SSRMarker.setAmplification(sheetMarkerDetails.getCell(30,r).getContents().trim());
						
						session.saveOrUpdate(markerInfo);	
						session.saveOrUpdate(markerUserInfo);
						session.saveOrUpdate(SSRMarker);
						//session.saveOrUpdate(chromosome);
						session.saveOrUpdate(markerAlias);
						//System.out.println("Inserted a record");					
					if (r % 10 == 0){
						session.flush();
						session.clear();
					}					
				}	
				
				tx.commit();				
				//duplication markers list display message.							
				if(!strDupMarkers.equals("")){
					strDupMarkers = strDupMarkers.substring(0,strDupMarkers.length()-1);					
					String ErrMsg = "Marker(s) ["+strDupMarkers+"] are already exists in the database.\nRemaining Marker(s) has been inserted successfully";
					hsession.setAttribute("indErrMsg", ErrMsg);					
					return strResult = "ErrMsg";
				}	
			}catch(Exception e){
				session.clear();
				//con.rollback();
				//tx.rollback();
				
				e.printStackTrace();
			}finally{			    
				session.clear();				
			}
			return strResult;
		}
		

}
