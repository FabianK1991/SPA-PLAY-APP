package models.util.parsing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import models.core.Activity;
import models.core.ProcessModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class ProcessParser {
	public static void main(String[] args){
		ProcessParser pp = new ProcessParser();
		
		pp.parseXML(new File("test.xml"));
	}
	
	private Document doc;
	private ArrayList<ProcessModel> pms; 
	
	public void parseProcess(Element ele){		
		ProcessModel pm = new ProcessModel(ele.getAttribute("id"));
		
		// parse activities
		NodeList nList = ele.getElementsByTagName("Task");
		
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Element el = (Element)nList.item(temp);
			
			Activity a = new Activity(el.getAttribute("id"), pm.getSPAProcessModel());
			
			if( el.getAttribute("name") != null ){
				a.setName(el.getAttribute("name"));
			}
			
		}
	}
	
	public void parseXML(File file){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			
			this.doc = db.parse(file);
			
			NodeList nList = this.doc.getElementsByTagName("Process");
			this.pms = new ArrayList<ProcessModel>();
			
			// parse each process
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Element ele = (Element)nList.item(temp);
				
				this.parseProcess(ele);
			}
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
}
