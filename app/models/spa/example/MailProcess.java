package spa.example;

import java.util.HashSet;
import java.util.Set;

import spa.api.ProcessInstance;
import spa.api.ProcessModel;
import spa.api.process.buildingblock.Activity;
import spa.api.process.buildingblock.BusinessObject;
import spa.api.process.buildingblock.Event;
import spa.api.process.buildingblock.Event.EventType;
import spa.api.process.buildingblock.Flow;
import spa.api.process.buildingblock.Gateway;
import spa.api.process.buildingblock.Gateway.GatewayType;
import spa.api.process.buildingblock.instance.ActivityInstance;
import spa.api.process.buildingblock.instance.BusinessObjectInstance;


public class MailProcess
{

    static String nsm  = "http://mail.process/";
    static String nsmi = "http://mail.process/instance/";


    public static void main(String[] args) throws Exception
    {
        ProcessModel pm = createProcessModel();
        pm.saveModelToFile("files/mail_process.ttl");
        ProcessInstance pi = createProcessInstances(pm);
        pi.saveInstanceToFile("files/mail_process_instance.ttl");
    }

    public static ProcessModel createProcessModel() throws Exception
    {
        // create the process
        ProcessModel pm = new ProcessModel();
        pm.setId(nsm + "process1");

        // set name and keywords
        pm.setName("Mail Process");
        pm.getKeywords().add("mail");
        pm.getKeywords().add("spam");

        // create business objects
        BusinessObject bo = new BusinessObject(pm);
        pm.getBusinessObjects().add(bo);

        bo.setId(nsm + "bo_email");
        bo.setName("Email");

        // create activities
        Event start = new Event(pm, EventType.Start);
        start.setId(nsm + "start1");

        Activity scan = new Activity(pm);
        scan.setId(nsm + "scan");
        scan.setName("Scan Mail");
        
        Set<BusinessObject> as = new HashSet<BusinessObject>();
        BusinessObject myBO = new BusinessObject(pm);
        myBO.setId(nsm + "BO1");
        myBO.setName("Sales Order");
        
        as.add(myBO);
        
        scan.setBusinessObjects(as);

        Gateway xor = new Gateway(pm, GatewayType.XOR);
        xor.setId(nsm + "spam_xor");

        Activity read = new Activity(pm);
        read.setId(nsm + "read");
        read.setName("Read Email");

        Activity answer = new Activity(pm);
        answer.setId(nsm + "answer");
        answer.setName("Answer Email");

        Activity delete = new Activity(pm);
        delete.setId(nsm + "delete");
        delete.setName("Delete Email");

        Event end = new Event(pm, EventType.End);
        end.setId(nsm + "end1");

        // add activities to process model
        pm.getNodes().add(start);
        pm.getNodes().add(scan);
        pm.getNodes().add(xor);
        pm.getNodes().add(read);
        pm.getNodes().add(answer);
        pm.getNodes().add(delete);
        pm.getNodes().add(end);

        // create flows
        Flow fl1 = new Flow(pm, start, scan);
        fl1.setId(nsm + "f1");

        Flow fl2 = new Flow(pm, scan, xor);
        fl2.setId(nsm + "f2");

        Flow fl3 = new Flow(pm, xor, read, "no spam");
        fl3.setId(nsm + "f3");

        Flow fl4 = new Flow(pm, read, answer);
        fl4.setId(nsm + "f4");

        Flow fl5 = new Flow(pm, answer, end);
        fl5.setId(nsm + "f5");

        Flow fl6 = new Flow(pm, xor, delete, "spam");
        fl6.setId(nsm + "f6");

        Flow fl7 = new Flow(pm, delete, end);
        fl7.setId(nsm + "f7");

        start.getNextFlows().add(fl1);
        scan.getNextFlows().add(fl2);
        xor.getNextFlows().add(fl3);
        read.getNextFlows().add(fl4);
        answer.getNextFlows().add(fl5);

        xor.getNextFlows().add(fl6);
        delete.getNextFlows().add(fl7);

        return pm;
    }


    private static ProcessInstance createProcessInstances(ProcessModel pm) throws Exception
    {

        ProcessInstance pi = new ProcessInstance(pm);
        pi.setId(nsmi + "mai_process_log1");

        String agent_jack = nsmi + "jack";
        String agent_jane = nsmi + "jane";

        BusinessObjectInstance mail117 = new BusinessObjectInstance(pi);
        mail117.setId(nsmi + "m1117");
        mail117.setBusinessObject(nsm + "bo_email");
        mail117.setLabel("mail117");

        BusinessObjectInstance mail122 = new BusinessObjectInstance(pi);
        mail122.setId(nsmi + "mail122");
        mail122.setBusinessObject(nsm + "bo_email");
        mail122.setLabel("mail122");

        ActivityInstance a1 = new ActivityInstance(pi);
        a1.setId(nsmi + "scan-mail-117");
        a1.setActivity(nsm + "scan");
        a1.setAgent(agent_jack);
        a1.setDateTime("03.02.2014 15:07:23");

        ActivityInstance a2 = new ActivityInstance(pi);
        a2.setId(nsmi + "read-mail-117");
        a2.setActivity(nsm + "read");
        a2.setAgent(agent_jack);
        a2.setDateTime("03.02.2014 15:07:28");

        ActivityInstance a3 = new ActivityInstance(pi);
        a3.setId(nsmi + "scan-mail-222");
        a3.setActivity(nsm + "scan");
        a3.setAgent(agent_jane);
        a3.setDateTime("03.02.2014 15:08:23");

        ActivityInstance a4 = new ActivityInstance(pi);
        a4.setId(nsmi + "delete-mail-22");
        a4.setActivity(nsm + "delete");
        a4.setAgent(agent_jane);
        a4.setDateTime("03.02.2014 15:08:51");

        ActivityInstance a5 = new ActivityInstance(pi);
        a5.setId(nsmi + "sent-mail-as-reply-to-mail-117");
        a5.setActivity(nsm + "answer");
        a5.setAgent(agent_jack);
        a5.setDateTime("03.02.2014 15:12:11");

        // set ordering
        a1.setNext(a2);
        a2.setNext(a5);

        a3.setNext(a4);

        // add business object
        a1.getBoi().add(mail117);
        a2.getBoi().add(mail117);
        a5.getBoi().add(mail117);

        a3.getBoi().add(mail122);
        a4.getBoi().add(mail122);

        // add activity instances to process instace
        pi.getActivities().add(a1);
        pi.getActivities().add(a2);
        pi.getActivities().add(a3);
        pi.getActivities().add(a4);
        pi.getActivities().add(a5);

        return pi;
    }

}
