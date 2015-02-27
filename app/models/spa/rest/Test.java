package spa.rest;

import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Set;

import spa.rest.entities.BusinessObject;
import spa.rest.entities.Process;
import spa.rest.entities.ProcessInstance;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;


public class Test
{

    // private static String test = "files/model.ttl";
    private static String process  = "ontology/spa-email-process.ttl";
    private static String processI = "ontology/spa-email-process-instance2.ttl";


    public static void main(String[] args) throws Exception
    {
        // process
        Model modelP = ModelFactory.createDefaultModel();
        modelP.read(new FileInputStream(process), null, "TURTLE");

        // process instance
        Model modelI = ModelFactory.createDefaultModel();
        modelI.read(new FileInputStream(processI), null, "TURTLE");

        // // delete - create - get:
        Process.deleteProcess("http://process-mail.org#MailProcess");
        // String pID = Process.createProcess(modelP);
        Process.createProcess(modelP);

        ProcessInstance.deleteProcessInstance("http://process-mail.org/instance#PI_Mail_Jane_1");
        printModel(ProcessInstance.getProcessInstance("http://process-mail.org/instance#PI_Mail_Jane_1"));
        // String piID = ProcessInstance.createProcessInstance(modelI);
        ProcessInstance.createProcessInstance(modelI);

        // Model out = ModelFactory.createDefaultModel();
        // out.add(Process.getProcess(pID));
        // out.add(ProcessInstance.getProcessInstance(piID));
        // out.add(loadCoreOntology());
        // //
        // // RDFDataMgr.write(new FileOutputStream("../ontology/out.ttl"), out, RDFFormat.TTL);
        // //
        //
        // System.out.println("Delete PI");
        // ProcessInstance.deleteProcessInstance(piID);
        // printModel(ProcessInstance.getProcessInstance(piID));
        //
        // System.out.println("DELETE P");
        // Process.deleteProcess(pID);
        // printModel(Process.getProcess(pID));

        // create process

        // create process instance
        // System.out.println(ProcessInstance.createProcessInstance(modelI));
        // System.out.println(ProcessInstance.updateProcessInstance(modelI));
        // printModel(ProcessInstance.getProcessInstance("http://process-mail.org/instance#PI_Mail_Jane_1"));
        // ProcessInstance.deleteProcessInstance("http://process-mail.org/instance#PI_Mail_Jane_1");
        // printModel(ProcessInstance.getProcessInstance("http://process-mail.org/instance#PI_Mail_Jane_1"));

        Set<String> keywords = new HashSet<>();
        keywords.add("abc");
        // System.out.println(Process.getProcessIDsKeyword(keywords));

        // Process.deleteProcess("http://process-mail.org#MailProcess");

        // Process.updateProcess(model);

        // System.out.println(BusinessObject.getBusinessObjectByKeywords(keywords));
        System.out.println("----");
        // BusinessObject.deleteBusinessObject("http://process-mail.org#BusinessObject");
        printModel(BusinessObject.getBusinessObject("http://process-mail.org#Mail"));
        // System.out.println("--");
        printModel(BusinessObject.getBusinessObject("http://process-mail.org#BusinessObject"));

        System.out.println(">>>");
        System.out.println("BO by Process: " + BusinessObject.getBusinessObjectsByProcessID("http://process-mail.org#MailProcess"));

        Set<String> boiIDs = new HashSet<String>();
        boiIDs.add("http://process-mail.org/instance#BOT");
        System.out.println("POI by BOIs" + ProcessInstance.getProcessInstanceIDsBOI(boiIDs));

        Set<String> boIDs = new HashSet<String>();
        boIDs.add("http://process-mail.org#BusinessObject");
        System.out.println("PO by BO" + Process.getProcessIDsBO(boIDs));

        // printModel(loadCoreOntology());
    }


    public static void printModel(Model model)
    {
        StmtIterator sIt = model.listStatements();
        while(sIt.hasNext()) {
            System.out.println(sIt.next());
        }

    }


    public static Model loadCoreOntology() throws Exception
    {
        Model modelP = ModelFactory.createDefaultModel();
        return modelP.read(new FileInputStream("ontology/SPA.owl"), null, null);
    }
}
