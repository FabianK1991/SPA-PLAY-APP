package models.util.parsing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import models.core.Activity;
import models.core.BusinessObject;
import models.core.DataAssociation;
import models.core.ProcessModel;
import models.core.exceptions.IncorrectNumberOfProcessModelsExeption;
import models.core.exceptions.ProcessModelNotFoundException;
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
	
	public ProcessParser(ProcessModel pm){
		this.prm = pm;
	}
	
	private Document doc;
	private ProcessModel prm; 
	
	public static String nsm = "http://masterteamproject/";
	public static String nsmi = "http://masterteamproject/instance/";
	public static String nsboi = "http://masterteamproject/business_object_instance/";
	
	private static String getUID() {
		String id = "";
		
		while (true) {
			id = UUID.randomUUID().toString();
			
			try {
				new ProcessModel(id);
			} catch (ProcessModelNotFoundException e) {
				break;
			}
		}
		return id;
	}
	
	/*
	 * Creates a new flow and adds it to the source node
	 * 
	 * TODO: If target or source is null then wait with creation!!!
	 * WORKAROUND: Place sequenceflow nodes at end of xml!!!
	 * 
	 */
	private void createFlow(Element el, ProcessModel pm){
		models.spa.api.process.buildingblock.Node source = pm.getSPANodeById(nsm + el.getAttribute("sourceRef"));
		models.spa.api.process.buildingblock.Node target = pm.getSPANodeById(nsm + el.getAttribute("targetRef"));
		
		Flow f = new Flow(pm.getSPAProcessModel(), source, target);
		
		//System.out.println("Flow:");
		//System.out.println(el.getAttribute("id"));
		//System.out.println(source.getId());
		//System.out.println(target.getId());
		
		if( el.getAttribute("id") != null ){
			f.setId(nsm + el.getAttribute("id"));
		}
		else{
			f.setId(nsm + getUID());
		}
		
		// set name ? - not supported by spa
		//f.setName(el.getAttribute("name"));
		
		source.getNextFlows().add(f);
	}
	
	/*
	 * Loops over the nodes and takes action according to node type
	 */
	private void handleChildren(Node n, ProcessModel pm){
		String name = n.getLocalName(); // ignore namespace
		
		if( name == null ){
			return;
		}
		
		switch(name){
			// Node is a task
			case "task":
				//System.out.println("Task!");
				Element el = (Element)n;
				
				String id = el.getAttribute("id");
				
				System.out.println(id);
				
				//Activity a = new Activity(id, pm.getSPAProcessModel());
				models.spa.api.process.buildingblock.Activity a = new models.spa.api.process.buildingblock.Activity(pm.getSPAProcessModel());
				
				a.setId(nsm + id);
				
				if( el.getAttribute("name") != null ){
					a.setName(el.getAttribute("name"));
				}
				
				// add activity to process model
				pm.getSPAProcessModel().getNodes().add(a);
				
				
				// Input Association
				NodeList dataAssociations = el.getElementsByTagNameNS("*", "sourceRef");
				
				for (int temp = 0; temp < dataAssociations.getLength(); temp++) {
					Element e = (Element)dataAssociations.item(temp);
					
					DataAssociation da = new DataAssociation(nsm + id, nsm + e.getTextContent());
					//System.out.println("Yolo: " + e.getTextContent());
					
					pm.dataAssoc.add(da);
				}
				break;
			case "startEvent":
				//System.out.println("Start!");
				Event start = new Event(pm.getSPAProcessModel(), EventType.Start);
		        start.setId(nsm + ((Element)n).getAttribute("id"));
		        
		        System.out.println(((Element)n).getAttribute("id"));
				
		        pm.getSPAProcessModel().getNodes().add(start);
				break;
			case "endEvent":
				//System.out.println("End!");
				Event end = new Event(pm.getSPAProcessModel(), EventType.End);
		        end.setId(nsm + ((Element)n).getAttribute("id"));
		        
		        System.out.println(((Element)n).getAttribute("id"));
				
		        pm.getSPAProcessModel().getNodes().add(end);
				break;
			case "exclusiveGateway":
				//System.out.println("Gaytway!");
				Gateway xor = new Gateway(pm.getSPAProcessModel(), GatewayType.XOR);
		        xor.setId(nsm + ((Element)n).getAttribute("id"));
		        
		        System.out.println(((Element)n).getAttribute("id"));
		        
		        if( ((Element)n).getAttribute("name") != null ){
		        	xor.setName(((Element)n).getAttribute("name"));
		        }
				
		        pm.getSPAProcessModel().getNodes().add(xor);
				break;
			case "sequenceFlow":
				//System.out.println("Flow!");
				this.createFlow((Element)n, pm);
				
				break;
			case "association":
				break;
			// create bo
			case "dataObjectReference":
				//System.out.println("BO!!");
				Element ele = (Element)n;
				
				NodeList properties = ele.getElementsByTagNameNS("*", "property");
				BusinessObject bo = new BusinessObject(nsm + ele.getAttribute("id"));
				
				bo.setName(ele.getAttribute("name"));
				
				for (int temp = 0; temp < properties.getLength(); temp++) {
					Element property = (Element)properties.item(temp);
					String propName = property.getAttribute("name");
					
					switch(propName){
						case "action":
							bo.setAction(property.getAttribute("value"));
							//System.out.println(propName + ": " + property.getAttribute("value"));
							break;
						case "min":
							bo.setMin(property.getAttribute("value"));
							//System.out.println(propName + ": " + property.getAttribute("value"));
							break;
						case "max":
							bo.setMax(property.getAttribute("value"));
							//System.out.println(propName + ": " + property.getAttribute("value"));
							break;
						case "attributes":
							bo.setNeededAttributes(property.getAttribute("value").split(","));
							//System.out.println(propName + ": " + property.getAttribute("value"));
							break;
						case "sapid":
							bo.setSAPId(property.getAttribute("value"));
							//System.out.println(propName + ": " + property.getAttribute("value"));
							break;
					}
				}
				
				pm.bos.add(bo);
				break;
			default:
		}
	}
	
	/*
	 * Parses a process node into an ProcessModel
	 */
	private ProcessModel parseProcess(ProcessModel pm, Element ele){
		// set id
		pm.getSPAProcessModel().setId(nsm + ele.getAttribute("id"));
		
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
	public void parseXML(File file) throws IncorrectNumberOfProcessModelsExeption{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			
			this.doc = db.parse(file);
			
			NodeList nList = this.doc.getElementsByTagNameNS("*", "process");
			
			//One file can only contain one process model!
			if (nList.getLength() == 1) {
				this.parseProcess(this.prm, (Element)nList.item(0));
			}
			else {
				throw new IncorrectNumberOfProcessModelsExeption();
			}
			/*
			// parse each process
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Element ele = (Element)nList.item(temp);
				
				ProcessModel pm = 
				this.pms.add(pm);
			}*/
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
