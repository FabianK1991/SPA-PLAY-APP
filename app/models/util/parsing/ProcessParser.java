package models.util.parsing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import models.core.Activity;
import models.core.ProcessModel;
import models.core.exceptions.IncorrectNumberOfProcessModelsExeption;
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
	
	public ProcessParser(File file, ProcessModel processModel){
		try {
			this.parseXML(file, processModel);
		} catch (IncorrectNumberOfProcessModelsExeption e) {
			/*If file contains 0 or more than 1 Process Model!*/
		}
	}
	
	private Document doc;
	private ArrayList<ProcessModel> pms; 
	
	/*
	 * Retrieves the parsed Process Models
	 */
	public List<ProcessModel> getParsedModels(){
		return pms;
	}
	
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
				
				//Activity a = new Activity(id, pm.getSPAProcessModel());
				models.spa.api.process.buildingblock.Activity a = new models.spa.api.process.buildingblock.Activity(pm.getSPAProcessModel());
				
				a.setId(id);
				
				if( el.getAttribute("name") != null ){
					a.setName(el.getAttribute("name"));
				}
				
				// add activity to process model
				pm.getSPAProcessModel().getNodes().add(a);
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
	private ProcessModel parseProcess(ProcessModel pm, Element ele){
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
	private void parseXML(File file, ProcessModel processModel) throws IncorrectNumberOfProcessModelsExeption{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			
			this.doc = db.parse(file);
			
			NodeList nList = this.doc.getElementsByTagName("process");
			this.pms = new ArrayList<ProcessModel>();
			
			//One file can only contain one process model!
			if (nList.getLength() == 1) {
				this.parseProcess(processModel, (Element)nList.item(0));
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
