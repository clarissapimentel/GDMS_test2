/**
 * Retrieves marker information
 */
package org.icrisat.gdms.retrieve;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.common.HibernateSessionFactory;
import org.icrisat.gdms.upload.SNPMarkerBean;

public class RetrieveMarkerDetailsAction extends Action{
	Connection con=null;
	private Session hsession;	
	private Transaction tx;
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		HttpSession session = req.getSession(true);
		hsession = HibernateSessionFactory.currentSession();
		tx=hsession.beginTransaction();
		ArrayList al=new ArrayList();
		Query query=null;
		Query querysnp=null;
		Query queryssr=null;
		ArrayList mdet=new ArrayList();
		try{			
			String DRIVER ="com.mysql.jdbc.Driver";
			//loading driver
			Class.forName(DRIVER).newInstance();			
			String url="jdbc:mysql://localhost:3306/icis?user=root&password=root";
			
			//establishing database connection
			con=DriverManager.getConnection(url);
			String AnnealingTemp="",ElongationTemp="";
			String SelctedName="";
			String QueryFeilds="";
			String SearchM="";
			String Query="",Query1="";
			String value="" ,MarkerName="";
			String str="",s1="";
			
			String str123="Marker Name";
			//Common feilds for all types of Data
			String CommonFeildNames="Principal Investigator!!~~Contact!!~~Institute!!~~Incharge Person!!~~Reference!!~~Crop!!~~AccessionID!!~~Genotype";
			String fStr="";
			String strQuery="";
			String SSR="";
			String SNP="";
			//feilds to suply in query
			//String CommonFeilds="marker_user_info.principal_investigator,marker_user_info.contact,marker_user_info.institute,marker_user_info.incharge_person,marker.reference,marker.crop,marker.accession_id,marker.genotype";
			String CommonFeilds="principal_investigator,contact,institute,incharge_person,reference,crop,accession_id,genotype";
			//String beanFeilds="rmib.getPrincipal_investigator(),rmib.getContact(),rmib.getInstitute(),rmib.getIncharge_person(),rmib.getReference(),rmib.getCrop(),rmib.getAccession_id(),rmib.getGenotype()";
			
			
			//retrieve selected feilds
			String SelectedOption=req.getParameter("SelectedOption");
			int a=3;int Geno_position=-1;
			//boolean cmap=false;
			boolean AllFeilds=false;
			
			String[] str123Arr=CommonFeildNames.split("!!~~");				
			String[]CommonFeildsArr=CommonFeilds.split(",");				
			
			String[] SelectedFeildsList=req.getParameter("SelectedFeilds").split(",");
			//System.out.println("SelectedFeildsList === "+req.getParameter("SelectedFeilds"));
			if(SelectedFeildsList==null){					
				QueryFeilds="";
			}else{
				for(int k=0;k<SelectedFeildsList.length;k++){
					if(SelectedFeildsList[k].equals("All")){
						//database feilds for SSR table 
						SSR="Assay Type!!~~Repeat!!~~No of Repeats!!~~SSR Type!!~~Sequence!!~~Sequence Length!!~~Min Allele!!~~Max Allele!!~~SSR number!!~~Size of Repeat Motif!!~~Forward Primer!!~~Reverse Primer!!~~Product Size!!~~Primer Length!!~~Forward Primer Temperature!!~~Reverse Primer Temperature!!~~Annealing Temperature!!~~Elongation Temperature!!~~Fragment Size Expected!!~~Fragment Size Observed!!~~Amplification";
						//database feilds for SNP table
						SNP="Assay Type!!~~Forward Primer!!~~Reverse Primer!!~~Product Size!!~~Expected Product Size!!~~Position on Refrence Sequence!!~~Motif!!~~Annealing Temperature!!~~Sequence";
						a=11;
						str123=str123+"!!~~"+CommonFeildNames;
						QueryFeilds=","+CommonFeilds;
						strQuery="select *";
						//cmap=true;
						//cmap_position=12;
						AllFeilds=true;
						break;
					}else{
						a++;
						//System.out.println("str123Arr["+Integer.parseInt(SelectedFeildsList[k])+"] == "+str123Arr[Integer.parseInt(SelectedFeildsList[k])]);
						str123=str123+"!!~~"+str123Arr[Integer.parseInt(SelectedFeildsList[k])];
						QueryFeilds=QueryFeilds+","+CommonFeildsArr[Integer.parseInt(SelectedFeildsList[k])];
						//strQuery="Select marker_id,marker_name,marker_type,"+QueryFeilds.substring(1)+",genotypes_count";
						strQuery="Select "+QueryFeilds.substring(1)+",genotypes_count,marker_type,marker_id,marker_name";
					}
				}
			}
			Geno_position=a-2;
			str123=str123+"!!~~GenotypeCount!!~~";
			
			
			//if the selected Query option is 'Quick Search'
			if(SelectedOption.equals("QuickSearch")){
				String QuickSearch =req.getParameter("QuickSearch");
				SearchM=req.getParameter("SearchMark");
				s1=s1+" LOWER("+QuickSearch+")like LOWER('"+SearchM+"%')";
				s1="where "+s1;
				
			}else if(SelectedOption.equals("main")){
				//if the selected Query option is 'Conditional Search'				
				String []value12 =req.getParameterValues("main");
				String next="";
				for(int l=0;l<value12.length;l++){
					str=str+value12[l]+",";
				}
				str=str.substring(0,str.length()-1);
				if(str.contains("Amplified")){
					String amp =req.getParameter("Amp");
					s1=s1+" SSR_Marker.Amplification like '"+amp+"'";
					if(amp.equals("unAmplified")){
						s1=s1+" or SSR_Marker.Amplification=''";
					}
					
					next=req.getParameter("AmplificationOption");
				}
				if(str.contains("SearchMarker")){
					if(!(s1.equals(""))){
						s1=s1+next;
					}
					SearchM =req.getParameter("MarkNm");			
					s1=s1+" LOWER(marker_retrieval_info.marker_name) like LOWER('"+SearchM+"%')";
					next=req.getParameter("MarkerOption");
				}				
				if(str.contains("Annealing")){
					String StartTemp =req.getParameter("StartRange");
					String EndTemp =req.getParameter("EndRange");
					if(!(s1.equals(""))){
						s1=s1+next;
					}
					s1=s1+" ((SSR_Marker.Annealing_Temp BETWEEN "+ StartTemp+" AND "+ EndTemp+") or (((SSR_Marker.Forward_Primer_Temp+SSR_Marker.Reverse_Primer_Temp)/2)-5) BETWEEN "+ StartTemp+" AND "+ EndTemp+")";
				}
				if(!(s1.equals(""))){
					s1="inner join SSR_Marker on SSR_Marker.Marker_id=marker_retrieval_info.marker_id where "+s1; 
				}
			}else{
				//if the selected Query option is 'Search By'
				//System.out.println("********************************************************");
				value =req.getParameter("type");
				SelctedName=req.getParameter("List1");
				//System.out.println("SelctedName == "+SelctedName+" ,value == "+value);
				//if(!(value.equals("MarkerName"))){
					String[] mname=req.getParameterValues("List2");
					//System.out.println(mname);
					for(int p=0;p<mname.length;p++){
						//System.out.println("vals[p]");
						MarkerName=MarkerName+"'"+mname[p]+"',";
					}
					MarkerName=MarkerName.substring(0, (MarkerName.length()-1));	
					
					s1=s1+" "+value+" ='"+SelctedName+"' and Marker_Name in ("+MarkerName+")";
					
					s1="where "+s1;				
			}
			
			if(SelctedName.equals("")){		
				if(!(SearchM.equals(""))){
					/*Statement markerTypeSt=con.createStatement();
					ResultSet markerTypeRS=markerTypeSt.executeQuery("select marker.marker_type from marker where marker.marker_name='"+SearchM+"'");
					while(markerTypeRS.next()){
						SelctedName=markerTypeRS.getString(1);
					}*/
					query=hsession.createQuery("from RetrievalMarkers where marker_name ='"+SearchM+"'");
					mdet=(ArrayList)query.list();
					for(Iterator iterator=mdet.iterator();iterator.hasNext();){
						RetrievalMarkers rmb=(RetrievalMarkers) iterator.next();
						//marker_id=rmb.getMarker_id();
						SelctedName=rmb.getMarker_type();
					}
				}else{
					SelctedName="SSR";
				}
			}
			String marker_type="";
			Query=strQuery+" from marker_retrieval_info "+s1;
			//query=hsession.createQuery(strQuery+" from RetrievalMarkerInfoBean where marker_name ='"+SearchM+"'");
			Statement st=con.createStatement();
			ResultSet rs=st.executeQuery(Query);
			Statement st1=con.createStatement();
			
			ArrayList SNPlist=new ArrayList();
			ArrayList SSRlist=new ArrayList();
			int Count=0;
			int GenotypeCount=0;
			while (rs.next()){		
				if(Count==0){
					SSRlist.add(str123+SSR);
					SNPlist.add(str123+SNP);
				}	
				str123="";
				Count++;
				
				GenotypeCount=rs.getInt("genotypes_count");
				//System.out.println("AllFeilds="+AllFeilds);
				if(AllFeilds==true){
					//marker_type=;
					str123=str123+rs.getString(3)+"!!~~"+rs.getString(9)+"!!~~"+rs.getString(10)+"!!~~"+rs.getString(11)+"!!~~"+rs.getString(12)+"!!~~"+rs.getString(6)+"!!~~"+rs.getString(4)+"!!~~"+rs.getString(5)+"!!~~"+rs.getString(7)+"!!~~"+rs.getString(13)+"!~!"+rs.getString(1);
				}else{
					String[] qf=QueryFeilds.split(",");
					fStr="";
					for(int i=1;i<=qf.length;i++){
						fStr=fStr+rs.getString(i)+"!!~~";	
						//System.out.println(">>>>>>>>>>>>>>>>   "+MarkerName+"   "+MarkerName1);				
					}
					//System.out.println("fStr="+fStr);
					marker_type=rs.getString("marker_type");				
					str123=rs.getString("marker_name")+"!!~~"+fStr.substring(0,fStr.length()-4)+"!~!"+rs.getString("marker_id")+"!!~~";
				}
					
				//str123=str123+GenotypeCount+"!~!"+rs.getInt(1)+"!!~~";
				if(AllFeilds==true){				
					Statement st2=con.createStatement();
					ResultSet rs2=null;
					if(rs.getString("marker_type").equals("SSR")){
						rs2=st2.executeQuery("Select assay_type,repeats,no_of_repeats,ssr_type,sequence,sequence_length,min_allele,max_allele,ssr_nr,size_of_repeat_motif,forward_primer,reverse_primer,product_size,primer_length,forward_primer_temp,reverse_primer_temp,annealing_temp,elongation_temp,fragment_size_expected,fragment_size_observed,amplification from ssr_marker where marker_id='"+rs.getString(1)+"'");
						if(rs2.next()){
							if((rs2.getString(17).equals("0"))||(rs2.getString(17).equals(""))||(rs2.getString(17)==null)){								
								double val1=0;
								double val2=0;
								double temp=0;
								if(!rs2.getString(15).equals("0")){
									val1=Double.parseDouble(rs2.getString(16));
								}
								if(!rs2.getString(16).equals("0")){
									val2=Double.parseDouble(rs2.getString(17));
								}
								if((rs2.getString(15).equals("0"))&&(rs2.getString(16).equals("0"))){
									temp=0;
								}else{
									temp= Math.round((((val1+val2)/2)-5)*100.0)/100.0;
								}								
								AnnealingTemp=String.valueOf(temp);
							}else{
								AnnealingTemp=rs2.getString(17);						
							}
							if((rs2.getString(18)==null)||rs2.getString(18).equals("")||rs2.getString(18).equals("null")){
								ElongationTemp="72c";
							}else{
								ElongationTemp=rs2.getString(18);
							}
							//str123="";							
							str123=str123+"!!~~"+rs2.getString(1)+"!!~~"+rs2.getString(2)+"!!~~"+rs2.getString(3)+"!!~~"+rs2.getString(4)+"!!~~"+rs2.getString(5)+"!!~~"+rs2.getString(6)+"!!~~"+rs2.getString(7)+"!!~~"+rs2.getString(8)+"!!~~"+rs2.getString(9)+"!!~~"+rs2.getString(10)+"!!~~"+rs2.getString(11)+"!!~~"+rs2.getString(12)+"!!~~"+rs2.getString(13)+"!!~~"+rs2.getString(14)+"!!~~"+rs2.getString(15)+"!!~~"+rs2.getString(16)+"!!~~"+AnnealingTemp+"!!~~"+ElongationTemp+"!!~~"+rs2.getString(19)+"!!~~"+rs2.getString(20)+"!!~~"+rs2.getString(21);
							System.out.println(str123);							
						}
						SSRlist.add(str123);
					}else if (rs.getString("marker_type").equals("SNP")){
						querysnp=hsession.createQuery("from SNPMarkerBean WHERE marker_id="+ rs.getString(1));
						//query=hsession.createQuery("from RetrievalMarkers WHERE germplasm_name='5066-002'");
						mdet=(ArrayList)querysnp.list();
						for(Iterator iterator=mdet.iterator();iterator.hasNext();){
							SNPMarkerBean snp = (SNPMarkerBean) iterator.next();
							 str123=str123+"!!~~"+snp.getAssay_type()+"!!~~"+snp.getForward_primer()+"!!~~"+snp.getReverse_primer()+"!!~~"+snp.getProduct_size()+"!!~~"+snp.getExpected_product_size()+"!!~~"+snp.getPosition_on_reference_sequence()+"!!~~"+snp.getMotif()+"!!~~"+snp.getAnnealing_temp()+"!!~~"+snp.getSequence();
						 }
						SNPlist.add(str123);
					}				
				}else{
					if(marker_type.equals("SSR")){
						SSRlist.add(str123);
					}else if (marker_type.equals("SNP")){
						SNPlist.add(str123);
					}					
				}
			}
			if(SSRlist.size()>1){
				al.add(SSRlist);
			}
			if(SNPlist.size()>1){
				al.add(SNPlist);
			}
			session.setAttribute("al", al);
		}catch(Exception e){
			e.printStackTrace();
		}		
		return am.findForward("ret");
	}
	

}
