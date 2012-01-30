/**
 * 
 */
package org.icrisat.gdms.upload;

import java.io.File;
import java.io.InputStream;

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
import org.icrisat.gdms.common.FileUploadToServer;

public class DataUploadAction extends Action{	
	String result="";
	FileUploadToServer fus = null;
	String fileName="";String saveFtoServer="";String saveF="";
	InputStream stream=null;
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		ActionErrors ae= new ActionErrors();
		HttpSession httpsession = req.getSession();
		DynaActionForm df = (DynaActionForm) af;
		String uploadType=(String)df.get("radios");	
		FormFile file=(FormFile)df.get("fileuploads");
		//String fname1=file.getFileName();
		stream=file.getInputStream();
	    saveFtoServer="UploadFiles";
	    saveF=httpsession.getServletContext().getRealPath("//")+"/"+saveFtoServer;
	    if(!new File(saveF).exists())
		   	new File(saveF).mkdir();	        
		fileName=saveF+"/"+file.getFileName();
		fus=new FileUploadToServer();
		fus.createFile(stream,fileName);
		System.out.println("uploadType="+uploadType);
		if(uploadType.equalsIgnoreCase("SSRGenotype")){
			SSRGenotypingDataUpload sssupl=new SSRGenotypingDataUpload();
			result=sssupl.getUpload(req, fileName);	
			System.out.println("result="+result);			
		}else if(uploadType.equalsIgnoreCase("SNPGenotype")){
			SNPGenotypingDataUpload snpupl=new SNPGenotypingDataUpload();
			result=snpupl.getUpload(req, fileName);				
			if(result.equals("notInserted")){
				ae.add("myerro",new ActionError("count.not.matching"));
				saveErrors(req, ae);
				return (new ActionForward(am.getInput()));
			}
		}else if(uploadType.equalsIgnoreCase("QTL")){
			QTLDataUpload qtlupl=new QTLDataUpload();
			result=qtlupl.setQTLData(req, fileName);
			
		}else if(uploadType.equalsIgnoreCase("DArT")){
			DArTGenotypingDataUpload dartupl=new DArTGenotypingDataUpload();
			result=dartupl.setDArTData(req, fileName);
			if(result.equals("countNotMatching")){
				ae.add("myerro",new ActionError("count.not.matching"));
				saveErrors(req, ae);
				return (new ActionForward(am.getInput()));
			}
			
		}else if(uploadType.equalsIgnoreCase("Mapping")){
			MappingDataUpload mapupl=new MappingDataUpload();
			result=mapupl.setMappingDetails(req, fileName);	
			
		}else if(uploadType.equalsIgnoreCase("SSRMarker")){
			SSRMarkerInfoUpload mapupl=new SSRMarkerInfoUpload();
			result=mapupl.setMarkerDetails(req, fileName);	
			System.out.println("............................   ;"+result);
			
		}else if(uploadType.equalsIgnoreCase("SNPMarker")){
			SNPMarkerInfoUpload mapupl=new SNPMarkerInfoUpload();
			result=mapupl.setMarkerDetails(req, fileName);	
			
		}
		System.out.println("result="+result);	
		fus.deleteFile(fileName);
		return am.findForward(result);
	}
	

}
