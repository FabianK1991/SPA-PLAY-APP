package models.core.util.parsing;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import play.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import models.core.DataAssociation;
import models.core.exceptions.IncorrectNumberOfProcessModelsExeption;
import models.core.exceptions.ProcessModelNotFoundException;
import models.core.process.Activity;
import models.core.process.ProcessModel;
import models.core.serverModels.businessObject.BusinessObject;
import models.spa.api.process.buildingblock.Event;
import models.spa.api.process.buildingblock.Flow;
import models.spa.api.process.buildingblock.Gateway;
import models.spa.api.process.buildingblock.Event.EventType;
import models.spa.api.process.buildingblock.Gateway.GatewayType;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import controllers.Application;


public class ProcessParser {
	
	public ProcessParser(ProcessModel pm){
		this.prm = pm;
	}
	
	private Document doc;
	private ProcessModel prm; 
	
	public static String nsm = "http://masterteamproject/";
	public static String nsmi = "http://masterteamproject/instance/";
	public static String nsboi = "http://masterteamproject/business_object_instance/";
	
	public String fileString;
	
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
	
	private List<Element> flows = new ArrayList<Element>();
	
	private void postprocessFlows(ProcessModel pm){
		for(Element e: this.flows){
			this.createFlow(e, pm);
		}
	}
	
	private void insertActivity(String pm_id, String activity_id){
		Application.db.connect();
		
		String query = "INSERT INTO process_activities (process_model,activity_id) VALUES ('%s','%s')";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(pm_id);
		args.add(activity_id);
		
		Application.db.exec(query, args, false);

		return;
	}
	
	/*
	 * Creates a new flow and adds it to the source node
	 * 
	 */
	private void createFlow(Element el, ProcessModel pm){
		models.spa.api.process.buildingblock.Node source = pm.getSPANodeById(pm.getId() + el.getAttribute("sourceRef"));
		models.spa.api.process.buildingblock.Node target = pm.getSPANodeById(pm.getId() + el.getAttribute("targetRef"));
		
		Flow f = new Flow(pm.getSPAProcessModel(), source, target);
		
		//System.out.println("Flow:");
		//System.out.println(el.getAttribute("id"));
		//System.out.println(source.getId());
		//System.out.println(target.getId());
		
		if( el.getAttribute("id") != null ){
			f.setId(pm.getId() + el.getAttribute("id"));
			this.replaceName(el.getAttribute("id"), pm);
		}
		else{
			String id =  getUID();
			f.setId(pm.getId() + id);
			this.replaceName(id, pm);
		}
		
		// set name ? - not supported by spa
		if( el.getAttribute("name") != null && el.getAttribute("name").length() > 0 ){
			//f.setCondition(el.getAttribute("name"));
			//Set<String> conditionSet = new HashSet<String>();
			//conditionSet.add("condition:" + el.getAttribute("name").replaceAll("[^A-Za-z0-9-_.]", ""));
			
			//f.getTo().setKeywords(conditionSet);
			f.setCondition(el.getAttribute("name").replaceAll("[^0-9a-zA-Z!?.;:+'&(){}\\[\\]\\\\/=<>-_]", ""));
		}
		
		Logger.info("INSERTING NEW FLOW: " + f.getId()  + " from: " + source.getId() + " to: " + target.getId());
		
		source.getNextFlows().add(f);
	}
	
	private void replaceName(String oldID, ProcessModel pm){
		this.fileString = this.fileString.replaceAll(Pattern.quote("bpmnElement=\"" + oldID + "\""), "bpmnElement=\"" + pm.getRawId() + oldID + "\"")
				.replaceAll(Pattern.quote("id=\"" + oldID + "\""), "id=\"" + pm.getRawId() + oldID + "\"")
				.replaceAll(Pattern.quote("sourceRef=\"" + oldID + "\""), "sourceRef=\"" + pm.getRawId() + oldID + "\"")
				.replaceAll(Pattern.quote("targetRef=\"" + oldID + "\""), "targetRef=\"" + pm.getRawId() + oldID + "\"")
				.replaceAll(Pattern.quote("<bpmn2:incoming>" + oldID + "</bpmn2:incoming>"), "<bpmn2:incoming>" + pm.getRawId() + oldID + "</bpmn2:incoming>")
				.replaceAll(Pattern.quote("<bpmn2:outgoing>" + oldID + "</bpmn2:outgoing>"), "<bpmn2:outgoing>" + pm.getRawId() + oldID + "</bpmn2:outgoing>");
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
			case "receiveTask":
			case "sendTask":
			case "task":
				//System.out.println("Task!");
				Element el = (Element)n;
				
				String id = el.getAttribute("id");
				
				System.out.println(id);
				
				//Activity a = new Activity(id, pm.getSPAProcessModel());
				models.spa.api.process.buildingblock.Activity a = new models.spa.api.process.buildingblock.Activity(pm.getSPAProcessModel());
				
				a.setId(pm.getId() + id);
				this.replaceName(id, pm);
				
				if( el.getAttribute("name") != null ){
					a.setName(el.getAttribute("name"));
				}
				
				// add activity to process model
				pm.getSPAProcessModel().getNodes().add(a);
				
				this.insertActivity(pm.getRawId(), a.getId().replace(ProcessParser.nsm, ""));
				
				// Input Association
				/*NodeList dataAssociations = el.getElementsByTagNameNS("*", "sourceRef");
				
				for (int temp = 0; temp < dataAssociations.getLength(); temp++) {
					Element e = (Element)dataAssociations.item(temp);
					
					DataAssociation da = new DataAssociation(nsm + id, nsm + e.getTextContent());
					//System.out.println("Yolo: " + e.getTextContent());
					
					pm.dataAssoc.add(da);
				}*/
				break;
			case "startEvent":
				//System.out.println("Start!");
				Event start = new Event(pm.getSPAProcessModel(), EventType.Start);
		        start.setId(pm.getId() + ((Element)n).getAttribute("id"));
		        this.replaceName(((Element)n).getAttribute("id"), pm);
		        
		        System.out.println(((Element)n).getAttribute("id"));
				
		        pm.getSPAProcessModel().getNodes().add(start);
				break;
			case "endEvent":
				//System.out.println("End!");
				Event end = new Event(pm.getSPAProcessModel(), EventType.End);
		        end.setId(pm.getId() + ((Element)n).getAttribute("id"));
		        this.replaceName(((Element)n).getAttribute("id"), pm);
		        
		        System.out.println(((Element)n).getAttribute("id"));
				
		        pm.getSPAProcessModel().getNodes().add(end);
				break;
			case "exclusiveGateway":
				//System.out.println("Gaytway!");
				Gateway xor = new Gateway(pm.getSPAProcessModel(), GatewayType.XOR);
		        xor.setId(pm.getId() + ((Element)n).getAttribute("id"));
		        this.replaceName(((Element)n).getAttribute("id"), pm);
		        
		        System.out.println(((Element)n).getAttribute("id"));
		        
		        if( ((Element)n).getAttribute("name") != null ){
		        	xor.setName(((Element)n).getAttribute("name"));
		        }
				
		        pm.getSPAProcessModel().getNodes().add(xor);
				break;
			case "parallelGateway":
				//System.out.println("Gaytway!");
				Gateway and = new Gateway(pm.getSPAProcessModel(), GatewayType.AND);
				and.setId(pm.getId() + ((Element)n).getAttribute("id"));
				this.replaceName(((Element)n).getAttribute("id"), pm);
		        
		        System.out.println(((Element)n).getAttribute("id"));
		        
		        if( ((Element)n).getAttribute("name") != null ){
		        	and.setName(((Element)n).getAttribute("name"));
		        }
				
		        pm.getSPAProcessModel().getNodes().add(and);
				break;
			case "inclusiveGateway":
				//System.out.println("Gaytway!");
				Gateway or = new Gateway(pm.getSPAProcessModel(), GatewayType.OR);
				or.setId(pm.getId() + ((Element)n).getAttribute("id"));
				this.replaceName(((Element)n).getAttribute("id"), pm);
		        
		        System.out.println(((Element)n).getAttribute("id"));
		        
		        if( ((Element)n).getAttribute("name") != null ){
		        	or.setName(((Element)n).getAttribute("name"));
		        }
				
		        pm.getSPAProcessModel().getNodes().add(or);
				break;
			case "sequenceFlow":
				//System.out.println("Flow!");
				//this.createFlow((Element)n, pm);
				this.flows.add((Element)n);
				
				break;
			case "association":
				break;
			// create bo
				
			// NOT PARSED ANYMORE!!!
			/*
			case "dataObjectReference":
				//System.out.println("BO!!");
				Element ele = (Element)n;
				
				NodeList properties = ele.getElementsByTagNameNS("*", "property");
				BusinessObject bo = new BusinessObject(nsm + ele.getAttribute("id"));
				
				bo.setName(ele.getAttribute("name"));
				
				// TODO: clarify if this should be common way to go
				bo.setSAPId(ele.getAttribute("name").split(":")[0]);
				
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
						//case "sapid":
						//	bo.setSAPId(property.getAttribute("value"));
						//	//System.out.println(propName + ": " + property.getAttribute("value"));
						//	break;
					}
				}
				
				pm.bos.add(bo);
				break;*/
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
		
		// Process flows
		this.postprocessFlows(pm);
		
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
			this.fileString = FileUtils.readFileToString(file);
			
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
			e.printStackTrace();
		}
	}
}
