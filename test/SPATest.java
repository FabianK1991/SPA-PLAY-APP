
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.spa.api.ProcessModel;
import models.spa.example.MailProcess;
import models.spa.api.process.buildingblock.*;
import models.spa.api.process.buildingblock.instance.*;
import models.spa.rest.entities.Process;
import models.spa.rest.entities.BusinessObject;
import models.spa.rest.entities.ProcessInstance;

import org.junit.Test;




public class SPATest {
	
	private ProcessModel createTestModel(){
		try {
			return MailProcess.createProcessModel();
		} catch (Exception e) {
			return null;
		}
	}
	
	private models.spa.api.ProcessInstance createTestModelInstance(ProcessModel pm){
		try {
			return MailProcess.createProcessInstances(pm);
		} catch (Exception e) {
			return null;
		}
	}

	// ProcessModel 
	//done
	@Test
	public void getProcessModelById() {
		// Process.getProcess
		ProcessModel pm = createTestModel();
		
		try {
			pm.delete();
			pm.store();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in store!");
		}
	
		try {
			new ProcessModel();
			ProcessModel pmNew = ProcessModel.getProcess(pm.getId());
			if(pmNew == null){
				throw new Exception();
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in retrieving the Process Model by an ID");
		}
		
		try {
			pm.delete();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in delete!");
		}
	}
	
	//done bzw kleines todo?
	@Test
	public void getProcessIDsKeyword() {
		// Process.getProcessIDsKeyword
		ProcessModel pm = createTestModel();
		HashSet<String> testKeywords = new HashSet<String>();
		
		for(int i = 0;  i < (int)(3 + Math.random()*5); i++){
			String word = "";
			for(int j = 0; j < (int)(5 + Math.random()*4); j++){
				char c = (char) (97 + (int)(Math.random() * 26));
				word = word + c;
			}
			testKeywords.add(word);
		}
		
		
		pm.setKeywords(testKeywords);
		
		try {
			pm.delete();
			pm.store();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in store!");
		}
		boolean flagContains = false;
		try {
			for(ProcessModel newPM:ProcessModel.searchByKeywords(testKeywords)){
				if (newPM.getId().equals(pm.getId())){
					flagContains = true;
				}
			}
		} catch (Exception e1) {
			fail("Process could not be received with the given keywords");
			e1.printStackTrace();
		}
		if(!flagContains){
			fail("Process was not found when searching by keywords, using every keyword of the process");
		}
		
		HashSet<String> someKeywords = new HashSet<String>();
		int count = 0;
		for(String keyword:testKeywords){
			someKeywords.add(keyword);
			count ++;
			
			if(count == testKeywords.size()-2) break;
		}
		
		flagContains = false;
		try {
			for(ProcessModel newPM:ProcessModel.searchByKeywords(someKeywords)){
				if (newPM.getId().equals(pm.getId())){
					flagContains = true;
				}
			}
		} catch (Exception e1) {
			fail("Process could not be received with the given keywords");
			e1.printStackTrace();
		}
		if(!flagContains){
			fail("Process was not found when searching by keywords, using every keyword of the process");
		}
		
		/*
		 * ggf noch case für abfrage on keywords?
		 */
		
		try {
			pm.delete();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in delete!");
		}
	}
	
	//done
	@Test
	public void getProcessIDsBO() {
		// Process.getProcessIDsBO
		ProcessModel pm = createTestModel();
		Set<models.spa.api.process.buildingblock.BusinessObject> bos = pm.getBusinessObjects();
		
		try {
			pm.delete();
			pm.store();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in store!");
		}
		HashSet<String> boUris = new HashSet<String>();
		for(models.spa.api.process.buildingblock.BusinessObject bo: bos){
			boUris.add(bo.getId());
		}
		List<ProcessModel> processModels = null;
		try {
			processModels = ProcessModel.searchByBusinessObjects(boUris);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			fail("Error while searching for process model with business object(s)");
		}
		boolean flagContains = false;
		for(ProcessModel newModel:processModels){
			if(newModel.getId().equals(pm.getId())){
				flagContains = true;
			}
		}
		if(!flagContains){
			fail("Error process model could not be found with business object(s)");
		}
		
		try {
			pm.delete();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in delete!");
		}
	}
	
	// ProcessModelInstance
	//done
	@Test
	public void getProcessModelInstanceById() {
		// ProcessInstance.getProcessInstance
		ProcessModel pm = createTestModel();
		models.spa.api.ProcessInstance pi = createTestModelInstance(pm);
		
		try {
			pi.delete();
			pi.store();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in store!");
		}
		
		try {
			models.spa.api.ProcessInstance piNew = models.spa.api.ProcessInstance.getProcessInstance(pm, pi.getId());
			if(piNew == null) throw new Exception();
		} catch (Exception e1) {
			fail("Error in retrieving the Process Model by an ID");
			e1.printStackTrace();
		}
		
		try {
			pi.delete();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in delete!");
		}
	}
	
	@Test
	public void getProcessInstanceIDsPID() {
		// ProcessInstance.getProcessInstanceIDsPID
		ProcessModel pm = createTestModel();
		models.spa.api.ProcessInstance pi = createTestModelInstance(pm);
		
		try {
			pi.delete();
			pi.store();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in store!");
		}
		
		Set<String> ids = null;
		try {
			ids = ProcessInstance.getProcessInstanceIDsPID(pm.getId());
		} catch (Exception e1) {
			fail("Error while retrieving the ProcessInstance Ids by the Process Ids");
			e1.printStackTrace();
		}
		
		try {
			boolean containsFlag = false;
			for(String id : ids) {
				if(id.equals(pi.getId())){
					containsFlag = true;
				}
			}
			
			if(!containsFlag){
				fail("Process Instance id was lost");
			}
		} catch (Exception e) {
			fail("The retrieved id set of the process instance ids was null");
			e.printStackTrace();
		}
		
		try {
			pi.delete();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in delete!");
		}
	}
	
	@Test
	public void getProcessInstanceIDsBOI() {
		// ProcessInstance.getProcessInstanceIDsBOI
		ProcessModel pm = createTestModel();
		models.spa.api.ProcessInstance pi = createTestModelInstance(pm);
		
		Set<String> boiIDs = new HashSet<String>();
		
		System.out.println(pi.getBusinessObjects().size());
		for(BusinessObjectInstance boi:pi.getBusinessObjects()){
			boiIDs.add(boi.getId());
		}
		
		
		try {
			pi.delete();
			pi.store();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in store!");
		}
		
		try {
			Set<String> processInstanceIDs = ProcessInstance.getProcessInstanceIDsBOI(boiIDs);
			
			boolean containsFlag = false;
			
			for(String piID : processInstanceIDs){
				if(piID.equals(pi.getId())){
					containsFlag = true;
				}
			}
			if(!containsFlag){
				fail("Process Instance could not be retrieved by business object instance id");
			}
		} catch (Exception e1) {
			fail("The return value was null");
			e1.printStackTrace();
		}
		
		try {
			pi.delete();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in delete!");
		}
	}
	
	// BusinessObject
	//done
	@Test
	public void getBusinessObject() {
		// BusinessObject.getBusinessObject
		ProcessModel pm = createTestModel();
		
		try {
			pm.delete();
			pm.store();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in store!");
		}
			ProcessModel newPM = null;
		try {
			newPM = ProcessModel.getProcess(pm.getId());
		} catch (Exception e1) {
			e1.printStackTrace();
			fail("Error in retrieval");
		}
		
		for(models.spa.api.process.buildingblock.BusinessObject boOld: pm.getAllBusisnessObjects()){
			boolean containsFlag = false;
			for(models.spa.api.process.buildingblock.BusinessObject boNew: newPM.getAllBusisnessObjects()){
				if (boNew.getId().equals(boOld.getId())){
					containsFlag = true;
				}
			}
			if(!containsFlag){
				fail("Error with stroring/retrieving Business Objects");
			}
		}
		
		
		try {
			pm.delete();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in delete!");
		}	
	}
	
	@Test
	public void getBusinessObjectByKeywords() {
		// BusinessObject.getBusinessObjectByKeywords
		ProcessModel pm = createTestModel();
		models.spa.api.process.buildingblock.BusinessObject bo = new models.spa.api.process.buildingblock.BusinessObject(pm);
		
		HashSet<String> testKeywords = new HashSet<String>();
		
		for(int i = 0;  i < (int)(3 + Math.random()*5); i++){
			String word = "";
			for(int j = 0; j < (int)(5 + Math.random()*4); j++){
				char c = (char) (97 + (int)(Math.random() * 26));
				word = word + c;
			}
			testKeywords.add(word);
		}
		
		bo.setKeywords(testKeywords);
		
		Set<models.spa.api.process.buildingblock.BusinessObject> needForSetBusinessObject = new HashSet<models.spa.api.process.buildingblock.BusinessObject>();
		needForSetBusinessObject.add(bo);
		
		pm.setBusinessObjects(needForSetBusinessObject);
		
		try {
			pm.delete();
			pm.store();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in store!");
		}
		
		try {
			Set<String> boids = BusinessObject.getBusinessObjectByKeywords(testKeywords);
			
			boolean containsFlag = false;
			for(String boid : boids){
				if(boid.equals(bo.getId())){
					containsFlag = true;
				}
			}
			if(!containsFlag){
				fail("Could not get BusinessObject by keyword");
			}
		} catch (Exception e1) {
			fail("Some null pointer by the RestApi");
			e1.printStackTrace();
		}
		
		try {
			pm.delete();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in delete!");
		}
	}
	
	@Test
	public void getBusinessObjectsByProcessID() {
		
		// BusinessObject.getBusinessObjectsByProcessID
		ProcessModel pm = createTestModel();
		Set<models.spa.api.process.buildingblock.BusinessObject> businessObjects = pm.getAllBusisnessObjects();
		
		try {
			pm.delete();
			pm.store();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in store!");
		}

		try {
			Set<String> boIDs = BusinessObject.getBusinessObjectsByProcessID(pm.getId());
			int count = businessObjects.size();
			for (String boID : boIDs) {
				for(models.spa.api.process.buildingblock.BusinessObject bo : businessObjects){
					if(bo.getId().equals(boID)){
						count--;
					}
				}
			}
			if(count != 0){
				fail("Businessobjects were not retrieved correctly for a given process ID");
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			pm.delete();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in delete!");
		}
	}
}
