package org.icrisat.gdms.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;

//import com.Ostermiller.util.ExcelCSVPrinter;
import javax.servlet.http.*;



import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


public class ExportFormats {
	
	ArrayList alleleList=new ArrayList();
	ArrayList accList=new ArrayList(); 
	ArrayList markList=new ArrayList();		
	int countofacc=1;
	String temp="",temp1="";
	ArrayList excelAccList=new ArrayList();
	ArrayList excelAccList1=new ArrayList();
	ArrayList dataidList=new ArrayList();
	
	

//	 To write matrix 
	public void Matrix(ArrayList a,String filePath,HttpServletRequest req, ArrayList gList, ArrayList mList){
		
		int columns=2;
		int row=1;
		String MarkernameId="";
		String previousMarkerId="";			
		String MarkerIdNameList="";
		String markerId="";
		accList.clear();
		markList.clear();
		alleleList.clear();
		try{
			//System.out.println("****************  EXPORT FORMATS CLASS  *****************");
			WritableWorkbook workbook=Workbook.createWorkbook(new File(filePath+"/jsp/analysisfiles/matrix"+(String)req.getSession().getAttribute("msec")+".xls"));
			WritableSheet sheet=workbook.createSheet("DataSheet",0);
			
			int accIndex=1,markerIndex=1;
			int i;
			for(int a1=0;a1<a.size();a1++){
				String[] argData=a.get(a1).toString().split(",");
				if(!accList.contains(argData[0].toString())){
					accList.add(argData[0].toString());
				}
				if(!markList.contains(argData[1])){
					markList.add(argData[1]);
				}
				alleleList.add(argData[2]);
			}
			//System.out.println("accList="+accList.size());
			//System.out.println("markList="+markList.size());
			//System.out.println("alleleList="+alleleList.size());
			req.getSession().setAttribute("mCount", markList.size());
			req.getSession().setAttribute("genCount", accList.size());
			
			
			int noOfAccs=accList.size();
			int noOfMarkers=markList.size();
			
			/*Label l=new Label(0,0," ");
			sheet.addCell(l);
			
//			 To write accessions
			for(i=0;i<noOfAccs;i++){
				l=new Label(0,accIndex++,(String)accList.get(i));
				sheet.addCell(l);
			}
			
			//To write markers
			for(i=0;i<noOfMarkers;i++){					
				l=new Label(markerIndex++,0,(String)markList.get(i));
				sheet.addCell(l);				
			}			
			MarkerIdNameList=MarkerNameIdList(mList);
			row=0;
			String[] AllelesList=null;
			for(int a1=0;a1<a.size();a1++){
				AllelesList=a.get(a1).toString().split(",");
				if((AllelesList[1].toString()).equals(previousMarkerId)){
					row++;
				}else{
					int totalRows=sheet.getRows();
					for(int ss=0;ss<totalRows;ss++){
						if(sheet.getCell(0,ss).getContents().equals(AllelesList[0].toString())){
							row=ss;
							break;
						}
					}
				}
				markerId=AllelesList[1];
				int firstindex=MarkerIdNameList.indexOf(markerId);

				if(firstindex!=0){
					firstindex=MarkerIdNameList.indexOf("!&&!"+markerId)+4;
				}
				int nextindex=MarkerIdNameList.indexOf("!&&!", firstindex);
				MarkernameId=MarkerIdNameList.substring(firstindex,nextindex);
				int totalcols=sheet.getColumns();

				for(int ss=0;ss<totalcols;ss++){
					if(sheet.getCell(ss,0).getContents().equals(MarkernameId)){
						columns=ss;
						break;
					}
				}	
				String[] allele1=null;
				String allele2=AllelesList[2];
				String allele="";
				if(allele2.contains(":")){
					allele1=allele2.split(":");
					if(allele1[0].equalsIgnoreCase(allele1[1])){
						allele=allele1[0];
					}else{
						allele=allele1[0]+"/"+allele1[1];
					}
						
				}else if(allele2.contains(",")){					
					allele1=allele.split(",");					
					if(allele1[0].equalsIgnoreCase(allele1[1])){						
						allele=allele1[0];
					}else{
						allele=allele1[0]+"/"+allele1[1];
					}
				}else{
					allele=allele2;
				}
				
				l=new Label(columns,row,allele+"");
				sheet.addCell(l);
				columns++;
					
				previousMarkerId=AllelesList[1].toString();
			}
			
			workbook.write();
			 * */
			 
			workbook.close();		
			
		}catch(Exception e){
			
		}
	}
	
	
	public String MarkerNameIdList(ArrayList markList){		
		String MarkerIdNameList="";
		for(int i=0;i<markList.size();i++){
			MarkerIdNameList=MarkerIdNameList+markList.get(i)+"!&&!";
		}
	
		return MarkerIdNameList;
	}
	
	
/**	Writing genotyping .dat file for FlapJack */
	public void MatrixDat(ArrayList a, String mapData, String filePath,HttpServletRequest req, ArrayList gList, ArrayList mList){
		accList.clear();
		markList.clear();
		alleleList.clear();
		try{
			
			for(int a1=0;a1<a.size();a1++){
				String[] argData=a.get(a1).toString().split(",");
				if(!accList.contains(argData[0].toString())){
					accList.add(argData[0].toString());
				}
				if(!markList.contains(argData[1])){
					markList.add(argData[1]);
				}
				alleleList.add(argData[2]);
			}
				
			req.getSession().setAttribute("mCount", markList.size());
			req.getSession().setAttribute("genCount", accList.size());				
			
			int noOfAccs=accList.size();
			int noOfMarkers=markList.size();			
			
			int accIndex=1,markerIndex=1;
			int i;String chVal="";
			
			FileWriter flapjackdatstream = new FileWriter(filePath+("//")+"/flapjack/Flapjack.dat");
			BufferedWriter fjackdat = new BufferedWriter(flapjackdatstream);
			
			for(int m1 = 0; m1< markList.size(); m1++){
				fjackdat.write("\t"+markList.get(m1));
			}
			
			int al=0;
						
			for (int j=0;j<accList.size();j++){ 
				fjackdat.write("\n"+accList.get(j));		
			    for (int k=0;k<markList.size();k++){
				   String strList5=a.get(al).toString();
				   String[] arrList6=strList5.split(",");
				   if((arrList6[0].equals(accList.get(j))) && arrList6[1].equals(markList.get(k))){
					   if(arrList6[2].contains(":")){								
							String[] ChVal1=arrList6[2].split(":");
							if(ChVal1[0].equalsIgnoreCase(ChVal1[1])){
								chVal=ChVal1[0];
							}else{
								chVal=ChVal1[0]+"/"+ChVal1[1];
							}
						}else{
							chVal=arrList6[2];
						}
						fjackdat.write("\t"+chVal);	
						al++;
					   
					}else{
						fjackdat.write("\t");
					}
			      }
		    	
		     }
						
			fjackdat.close();			
			
			
						
			/**	writing tab delimited .map file for FlapJack  
			 * 	consisting of marker chromosome & position
			 * 
			 * **/
			
			FileWriter flapjackmapstream = new FileWriter(filePath+("//")+"/flapjack/Flapjack.map");
			BufferedWriter fjackmap = new BufferedWriter(flapjackmapstream);
			String[] mData=mapData.split("~~!!~~");
			
			for(int m=0;m<mData.length;m++){		
				String[] strMData=mData[m].split("!~!");
				fjackmap.write(strMData[0]);
				fjackmap.write("\t");
				fjackmap.write(strMData[1]);
				fjackmap.write("\t");
				fjackmap.write(strMData[2]);
				fjackmap.write("\n");		
			}
			fjackmap.close();
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Writing input file for CMTV
	 * 
	 */
	public void CMTVTxt(ArrayList aList, String filePath, HttpServletRequest req){
		try{
			FileWriter cmtvstream = new FileWriter(filePath+("//")+"/jsp/analysisfiles/"+(String)req.getSession().getAttribute("msec")+"CMTV.txt");
			BufferedWriter cmtvBW = new BufferedWriter(cmtvstream);
			
			for(int a=0;a<aList.size();a++){		
				String[] strData=aList.get(a).toString().split("!~!");
				cmtvBW.write(strData[0]+"\t"+strData[1]+"\t"+strData[2]+"\t"+strData[3]+"\t"+strData[4]+"\n");					
			}
			cmtvBW.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
