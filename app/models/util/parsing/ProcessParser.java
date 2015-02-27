package models.util.parsing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import models.core.Activity;
import models.core.ProcessModel;
import models.spa.api.process.buildingblock.Event;
import models.spa.api.process.buildingblock.Flow;
import models.spa.api.process.buildingblock.Gateway;
import models.spa.api.process.buildingblock.Event.EventType;
import models.spa.api.process.buildingblock.Gateway.GatewayType;

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
	
	/*
	 * Creates a new flow and adds it to the source node
	 */
	private void createFlow(Element el, ProcessModel pm){
		models.spa.api.process.buildingblock.Node source = pm.getSPANodeById(el.getAttribute("sourceRef"));
		models.spa.api.process.buildingblock.Node target = pm.getSPANodeById(el.getAttribute("targetRef"));
		
		Flow f = new Flow(pm.getSPAProcessModel(), source, target);
		f.setId(el.getAttribute("id"));
		
		// set name ? - not supported by spa
		//f.setName(el.getAttribute("name"));
		
		source.getNextFlows().add(f);
	}
	
	/*
	 * Loops over the nodes and takes action according to node type
	 */
	private void handleChildren(Node n, ProcessModel pm){
		String name = n.getNodeName();
		
		switch(name){
			// Node is a task
			case "task":
				Element el = (Element)n;
				
				String id = el.getAttribute("id");
				
				Activity a = new Activity(el.getAttribute("id"), pm.getSPAProcessModel());
				
				if( el.getAttribute("name") != null ){
					a.setName(el.getAttribute("name"));
				}
				
				// add activity to process model
				pm.addActivity(a);
				break;
			case "startEvent":
				Event start = new Event(pm.getSPAProcessModel(), EventType.Start);
		        start.setId(((Element)n).getAttribute("id"));
				
		        pm.getSPAProcessModel().getNodes().add(start);
				break;
			case "endEvent":
				Event end = new Event(pm.getSPAProcessModel(), EventType.End);
		        end.setId(((Element)n).getAttribute("id"));
				
		        pm.getSPAProcessModel().getNodes().add(end);
				break;
			case "exclusiveGateway":
				Gateway xor = new Gateway(pm.getSPAProcessModel(), GatewayType.XOR);
		        xor.setId(((Element)n).getAttribute("id"));
		        
		        if( ((Element)n).getAttribute("name") != null ){
		        	xor.setName(((Element)n).getAttribute("name"));
		        }
				
		        pm.getSPAProcessModel().getNodes().add(xor);
				break;
			case "sequenceFlow":
				this.createFlow((Element)n, pm);
				
				break;
			case "association":
				break;
			default:
		}
	}
	
	/*
	 * Parses a process node into an ProcessModel
	 */
	private ProcessModel parseProcess(Element ele){		
		ProcessModel pm = new ProcessModel(ele.getAttribute("id"));
		
		// parse nodes
		NodeList nList = ele.getChildNodes();
		
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node n = nList.item(temp);
			
			this.handleChildren(n, pm);
		}
		
		return pm;
	}
	
	/*
	 * Parses an xml into processmodels
	 */
	public void parseXML(File file){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			
			this.doc = db.parse(file);
			
			NodeList nList = this.doc.getElementsByTagName("process");
			this.pms = new ArrayList<ProcessModel>();
			
			// parse each process
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Element ele = (Element)nList.item(temp);
				
				ProcessModel pm = this.parseProcess(ele);
				this.pms.add(pm);
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
