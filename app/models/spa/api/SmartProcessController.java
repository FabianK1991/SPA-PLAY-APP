package models.spa.api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.spa.api.process.buildingblock.Activity;
import models.spa.api.process.buildingblock.BusinessObject;
import models.spa.api.process.buildingblock.Node;
import models.spa.api.process.buildingblock.instance.ActivityInstance;


public class SmartProcessController
{

    public static void main(String[] args) throws Exception
    {
        // ProcessModel p = getProcess("http://process-mail.org#MailProcess");
        // p.saveModelToFile("../ontology/spa-email-process-api.ttl");

        System.out.println(System.lineSeparator() + "all processess" + System.lineSeparator() + "===================");
        for(ProcessModel pm : ProcessModel.getAllProcesses()) {
            System.out.println(pm.getId() + "\t\t" + pm.getName());
        }

        System.out.println(System.lineSeparator() + "get process" + System.lineSeparator() + "===================");
        ProcessModel process = getProcess("http://process-mail.org#MailProcess");
        System.out.println("\tID:");
        System.out.println("\t\t" + process.getId());
        System.out.println("\tName:");
        System.out.println("\t\t" + process.getName());
        System.out.println("\tActivities:");
        for(Activity a : process.getAllActivities()) {
            System.out.println("\t\t" + a.getName() + "\t(" + a.getId() + ")");
        }
        System.out.println("\tBusiness Objects:");
        for(BusinessObject b : process.getAllBusisnessObjects()) {
            System.out.println("\t\t" + b.getName() + "\t(" + b.getId() + ")");
        }

        System.out.println("\tNodes:");
        for(Node n : process.getNodes()) {
            System.out.println("\t\t" + n.getName() + "\t(" + n.getId() + ")");
        }
        System.out.println("\tInstances:");
        for(ProcessInstance pi : process.getAllInstances()) {
            System.out.println("\t\t" + pi.getId());
        }

        System.out.println(System.lineSeparator() + "search processes" + System.lineSeparator() + "===================");
        System.out.println("\tkeyword:");
        Set<String> keywords = new HashSet<>();
        keywords.add("mail");
        for(ProcessModel pm : searchProcessKeywords(keywords)) {
            System.out.println("\t\t" + pm.getName() + "\t(" + pm.getId() + ")");
        }
        System.out.println("\tbusiness object id:");
        Set<String> bos = new HashSet<>();
        bos.add("http://process-mail.org#Mail");
        for(ProcessModel pm : searchProcessBusinessObject(bos)) {
            System.out.println("\t\t" + pm.getName() + "\t(" + pm.getId() + ")");
        }

        System.out.println(System.lineSeparator() + "get process instance" + System.lineSeparator() + "===================");

        ProcessInstance.delete("http://process-mail.org/instance#PI_Mail_Jane_1");
        ProcessInstance.createProcessInstance("../ontology/spa-email-process-instance2.ttl");

        ProcessInstance pi = getProcessInstance("http://process-mail.org/instance#PI_Mail_Jane_1");

        System.out.println(pi.getId());
        for(ActivityInstance ai : pi.getActivitiesOrdered()) {
            System.out.println("\t" + ai.getId() + "\t[" + ai.getActivity() + "]");
        }

        // pi.saveInstanceToFile("../ontology/spa-email-process-instance2-api.ttl");

    }


    public static List<ProcessModel> getAllProcesses() throws Exception
    {
        return ProcessModel.getAllProcesses();
    }


    public static ProcessModel getProcess(String id) throws Exception
    {
        return ProcessModel.getProcess(id);
    }


    public static List<ProcessModel> searchProcessKeywords(Set<String> keywords) throws Exception
    {
        return ProcessModel.searchByKeywords(keywords);
    }


    public static List<ProcessModel> searchProcessBusinessObject(Set<String> bo) throws Exception
    {
        return ProcessModel.searchByBusinessObjects(bo);
    }


    public static ProcessModel createNewProcess(String file) throws Exception
    {
        return ProcessModel.createNewProcess(file);
    }


    public static ProcessInstance getProcessInstance(String id) throws Exception
    {
        return ProcessInstance.getProcessInstance(null, id);
    }


    public static ProcessInstance createProcessInstance(String file) throws Exception
    {
        return ProcessInstance.createProcessInstance(file);
    }

}
