package tests;
import static org.junit.Assert.*;
import models.spa.api.ProcessInstance;
import models.spa.api.ProcessModel;
import models.spa.example.MailProcess;

import org.junit.Test;


public class SPATest {
	
	private ProcessModel createTestModel(){
		try {
			return MailProcess.createProcessModel();
		} catch (Exception e) {
			return null;
		}
	}
	
	private ProcessInstance createTestModelInstance(ProcessModel pm){
		try {
			return MailProcess.createProcessInstances(pm);
		} catch (Exception e) {
			return null;
		}
	}

	// ProcessModel
	@Test
	public void getProcessModelById() {
		// Process.getProcess
		ProcessModel pm = createTestModel();
		
		try {
			pm.store();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in store!");
		}
		
		fail("Not yet implemented!");
		
		try {
			pm.delete();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in delete!");
		}
	}
	
	@Test
	public void getProcessIDsKeyword() {
		// Process.getProcessIDsKeyword
		ProcessModel pm = createTestModel();
		
		try {
			pm.store();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in store!");
		}
		
		fail("Not yet implemented!");
		
		try {
			pm.delete();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in delete!");
		}
	}
	
	@Test
	public void getProcessIDsBO() {
		// Process.getProcessIDsBO
		ProcessModel pm = createTestModel();
		
		try {
			pm.store();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in store!");
		}
		
		fail("Not yet implemented!");
		
		try {
			pm.delete();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in delete!");
		}
	}
	
	// ProcessModelInstance
	@Test
	public void getProcessModelInstanceById() {
		// ProcessInstance.getProcessInstance
		ProcessInstance pm = createTestModelInstance(createTestModel());
		
		try {
			pm.store();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in store!");
		}
		
		fail("Not yet implemented!");
		
		try {
			pm.delete();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in delete!");
		}
	}
	
	@Test
	public void getProcessInstanceIDsPID() {
		// ProcessInstance.getProcessInstanceIDsPID
		ProcessInstance pm = createTestModelInstance(createTestModel());
		
		try {
			pm.store();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in store!");
		}
		
		fail("Not yet implemented!");
		
		try {
			pm.delete();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in delete!");
		}
	}
	
	@Test
	public void getProcessInstanceIDsBOI() {
		// ProcessInstance.getProcessInstanceIDsBOI
		ProcessInstance pm = createTestModelInstance(createTestModel());
		
		try {
			pm.store();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in store!");
		}
		
		fail("Not yet implemented!");
		
		try {
			pm.delete();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in delete!");
		}
	}
	
	// BusinessObject
	@Test
	public void getBusinessObject() {
		// BusinessObject.getBusinessObject
		ProcessModel pm = createTestModel();
		
		try {
			pm.store();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in store!");
		}
		
		fail("Not yet implemented!");
		
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
		
		try {
			pm.store();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in store!");
		}
		
		fail("Not yet implemented!");
		
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
		
		try {
			pm.store();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in store!");
		}
		
		fail("Not yet implemented!");
		
		try {
			pm.delete();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in delete!");
		}
	}
}
