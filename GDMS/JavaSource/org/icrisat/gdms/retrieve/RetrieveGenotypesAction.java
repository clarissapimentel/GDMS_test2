/**
 * 
 */
package org.icrisat.gdms.retrieve;

import java.sql.Connection;
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
import org.icrisat.gdms.upload.CharArrayBean;

/**
 * @author psrikalyani
 *
 */
public class RetrieveGenotypesAction extends Action{
	Connection con=null;
	private Session hsession;	
	private Transaction tx;
	
	public ActionForward execute(ActionMapping am, ActionForm af, HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		// TODO Auto-generated method stub
		HttpSession session = req.getSession(true);
		hsession = HibernateSessionFactory.currentSession();
		tx=hsession.beginTransaction();
		//int markerId=Integer.parseInt(mid);
		Query qgids=null;Query qmarker=null;
		String marker_name="";
		int mid=0;
		String finalStr="";
		String mid1=req.getParameter("data");
		mid=Integer.parseInt(mid1);
		qmarker=hsession.createQuery("from RetrievalMarkers where marker_id="+ mid+"");
		ArrayList mdet=(ArrayList)qmarker.list();
		for(Iterator iterator=mdet.iterator();iterator.hasNext();){
			RetrievalMarkers rm=(RetrievalMarkers) iterator.next();
			//finalStr=finalStr+strParam+"!!~~"+mub.getPrincipal_investigator()+"!!~~"+mub.getContact()+"!!~~"+mub.getInstitute()+"!!~~"+mub.getIncharge_person();
			marker_name=rm.getMarker_name();
		}
		
		
		qgids=hsession.createQuery("from CharArrayBean WHERE marker_id="+ mid+"");
		ArrayList gdet=(ArrayList)qgids.list();
		for(Iterator iterator=gdet.iterator();iterator.hasNext();){
			CharArrayBean cab=(CharArrayBean) iterator.next();
			finalStr=finalStr+cab.getGid()+"!~!"+cab.getGermplasm_name()+"~!~";
		}
		session.setAttribute("finalStr", finalStr);
		req.setAttribute("MName", marker_name);
		
		return am.findForward("retGenos");
	}
	

}
