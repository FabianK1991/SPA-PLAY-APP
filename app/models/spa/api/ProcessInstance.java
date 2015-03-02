package models.spa.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import models.spa.api.process.buildingblock.Node;
import models.spa.api.process.buildingblock.instance.ActivityInstance;
import models.spa.api.process.buildingblock.instance.BusinessObjectInstance;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


public class ProcessInstance
{
    private String              id              = null;
    private String              nsBase          = null;
    private String              name            = null;

    Set<BusinessObjectInstance> businessObjects = new HashSet<>();
    Set<ActivityInstance>       activities      = new HashSet<ActivityInstance>();
    private ProcessModel        processModel;


    public ProcessInstance(ProcessModel processModel)
    {
        this.processModel = processModel;
    }


    public String getId()
    {
        if(id == null) {
            id = "http://spa.instance/p" + System.nanoTime() + new Random().nextInt(1000);
        }

        int x = Math.max(id.lastIndexOf("/"), id.lastIndexOf("#"));
        nsBase = id.substring(0, x + 1);
        return id;
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public String getNsBase()
    {
        return nsBase;
    }


    public void setNsBase(String nsBase)
    {
        this.nsBase = nsBase;
    }


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public ProcessModel getProcessModel()
    {
        return processModel;
    }


    public void setProcessModel(ProcessModel processModel)
    {
        this.processModel = processModel;
    }


    public Set<BusinessObjectInstance> getBusinessObjects()
    {
        return businessObjects;
    }


    public void setBusinessObjects(Set<BusinessObjectInstance> businessObjects)
    {
        this.businessObjects = businessObjects;
    }


    public Set<ActivityInstance> getActivities()
    {
        return activities;
    }


    public List<ActivityInstance> getActivitiesOrdered()
    {
        List<ActivityInstance> orderedActivities = new LinkedList<>();

        if(activities.size() == 0) {
            return orderedActivities;
        } else if(activities.size() == 1) {
            orderedActivities.add(activities.iterator().next());
            return orderedActivities;
        }

        ActivityInstance current = null;
        for(ActivityInstance ai : activities) {
            if(ai.getNext() == null) {
                orderedActivities.add(ai);
                current = ai;
                break;
            }
        }

        do {
            ActivityInstance lastCurrent = current;
            for(ActivityInstance ai : activities) {
                if(ai.getNext() == current) {
                    orderedActivities.add(0, ai);
                    current = ai;
                }
            }

            if(lastCurrent == current) {
                System.err.println("ActivityInstances cannot be ordered.");
                return new ArrayList<ActivityInstance>(activities);
            }
        } while(orderedActivities.size() < activities.size());
        return orderedActivities;
    }


    public void setActivities(Set<ActivityInstance> activities)
    {
        this.activities = activities;
    }


    public void saveInstanceToFile(String file) throws Exception
    {
        Model model = modelToRDF(this);
        model.write(new FileWriter(new File(file)), "TURTLE");
    }


    public static ProcessInstance getProcessInstance(ProcessModel processModel, String id) throws Exception
    {
        return rdfToModel(models.spa.rest.entities.ProcessInstance.getProcessInstance(id), processModel);
    }


    public static ProcessInstance loadProcessInstance(String file) throws Exception
    {
        Model model = ModelFactory.createDefaultModel();
        model.read(new FileInputStream(file), null, "TURTLE");

        return rdfToModel(model, null);
    }


    public static ProcessInstance createProcessInstance(String file) throws Exception
    {
        Model model = ModelFactory.createDefaultModel();
        model.read(new FileInputStream(file), null, "TURTLE");

        models.spa.rest.entities.ProcessInstance.createProcessInstance(model);

        return rdfToModel(model, null);
    }


    public String store() throws Exception
    {
        return models.spa.rest.entities.ProcessInstance.createProcessInstance(modelToRDF(this));
    }


    public String update() throws Exception
    {
        return models.spa.rest.entities.ProcessInstance.updateProcessInstance(modelToRDF(this));
    }


    public boolean delete() throws Exception
    {
        return models.spa.rest.entities.ProcessInstance.deleteProcessInstance(id);
    }


    public static boolean delete(String id) throws Exception
    {
        return models.spa.rest.entities.ProcessInstance.deleteProcessInstance(id);
    }


    private static ProcessInstance rdfToModel(Model model, ProcessModel processModel) throws Exception
    {
        // get id
        StmtIterator iter1 = model.listStatements(null, RDF.type, new ResourceImpl(Node.PROCESS_INSTANCE));
        String id = null;
        while(iter1.hasNext()) {
            id = iter1.next().getSubject().toString();
        }

        ProcessInstance p = new ProcessInstance(processModel);
        p.setId(id);

        Map<String, BusinessObjectInstance> boiMap = new HashMap<>();
        Map<String, ActivityInstance> activitiesMap = new HashMap<>();

        StmtIterator iter2 = model.listStatements(new SimpleSelector(model.createResource(id), (Property) null, (RDFNode) null));
        while(iter2.hasNext()) {
            Statement s = iter2.next();
            Property predicate = s.getPredicate();
            String object = s.getObject().toString();

            String pred = predicate.toString();
            if(pred.equals(RDF.type.toString())) {
                // do nothing
            } else if(pred.equals(RDFS.label.toString())) {
                p.setName(object);
            } else if(pred.equals(Node.INSTANTIATES_PROCESS)) {
                if(p.getProcessModel() == null) {
                    p.setProcessModel(ProcessModel.getProcess(object));
                    p.getProcessModel().setId(object);
                }
            } else if(pred.equals(Node.USES_BUSINESS_OBJECT_INSTANCE)) {
                BusinessObjectInstance boi = new BusinessObjectInstance(p);
                boi.setId(object);
                boiMap.put(object, boi);
                p.getBusinessObjects().add(boi);
                StmtIterator iter2_1 = model.listStatements(new SimpleSelector(model.createResource(object), RDFS.label, (RDFNode) null));
                while(iter2_1.hasNext()) {
                    boi.setLabel(iter2_1.next().toString());
                }

            } else {
                System.err.println(s);
            }
        }

        StmtIterator iter3 = model.listStatements(new SimpleSelector(null, (Property) null, model.createResource(id)));
        while(iter3.hasNext()) {
            Statement s = iter3.next();
            String activity = s.getSubject().toString();
            String predicate = s.getPredicate().toString();
            if(predicate.equals(Node.BELONGS_TO)) {
                StmtIterator iter4 = model.listStatements(new SimpleSelector(model.createResource(activity), RDF.type, (RDFNode) null));
                while(iter4.hasNext()) {
                    Statement s2 = iter4.nextStatement();
                    String type = s2.getObject().toString();
                    if(type.equals(Node.ACTIVITY_INSTANCE)) {
                        ActivityInstance a;
                        if(activitiesMap.containsKey(activity)) {
                            a = activitiesMap.get(activity);
                        } else {
                            a = new ActivityInstance(p);
                            a.setId(activity);
                            activitiesMap.put(activity, a);
                        }

                        StmtIterator iter5 = model.listStatements(new SimpleSelector(model.createResource(activity), null, (RDFNode) null));
                        while(iter5.hasNext()) {
                            Statement s3 = iter5.next();
                            String pred = s3.getPredicate().toString();
                            String o = s3.getObject().toString();
                            if(pred.equals(RDF.type.toString())) {
                                // do nothing
                            } else if(pred.equals(Node.BELONGS_TO)) {
                                // do nothing
                            } else if(pred.equals(Node.INSTANTIATES_ACTIVITY)) {
                                a.setActivity(o);
                            } else if(pred.equals(Node.PERFORMED_BY)) {
                                a.setAgent(o);
                            } else if(pred.equals(Node.PERFORMED_AT)) {
                                a.setDateTime(o);
                            } else if(pred.equals(Node.FOLLOWED_BY)) {
                                ActivityInstance a2;
                                if(activitiesMap.containsKey(o)) {
                                    a2 = activitiesMap.get(o);
                                } else {
                                    a2 = new ActivityInstance(p);
                                    a2.setId(o);
                                    activitiesMap.put(o, a2);
                                }
                                a.setNext(a2);
                            } else if(pred.equals(Node.USES_BUSINESS_OBJECT_INSTANCE)) {
                                BusinessObjectInstance boi = new BusinessObjectInstance(p);
                                boi.setId(o);
                                boiMap.put(o, boi);
                                a.getBoi().add(boi);
                            } else {
                                System.err.println(s3);
                            }
                        }
                    } else {
                        System.err.println(s2);
                    }
                }
            } else {
                System.err.println(s);
            }
        }

        p.activities = new HashSet<ActivityInstance>(activitiesMap.values());

        for(String boiId : boiMap.keySet()) {
            BusinessObjectInstance boi = boiMap.get(boiId);
            StmtIterator iter6 = model.listStatements(new SimpleSelector(model.createResource(boiId), null, (RDFNode) null));
            while(iter6.hasNext()) {
                Statement s = iter6.next();
                String pred = s.getPredicate().toString();
                if(pred.equals(RDF.type.toString())) {
                    // do nothing
                } else if(pred.equals(Node.INSTANTIATES_BUSINESS_OBJECT)) {
                    boi.setBusinessObject(s.getObject().toString());
                } else if(pred.equals(RDFS.label.toString())) {
                    boi.setLabel(s.getObject().toString());
                } else {
                    System.err.println(">>>" + s);
                }

            }
        }

        return p;
    }


    private static Model modelToRDF(ProcessInstance p)
    {
        Model m = ModelFactory.createDefaultModel();
        m.setNsPrefix("spa", Node.mainURI);
        m.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        m.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        m.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");

        p.getId(); // generates id if not already set

        m.setNsPrefix("pi", p.nsBase);

        if(p.processModel != null && p.processModel.getNsBase() != null) {
            String pNs = p.processModel.getNsBase();
            m.setNsPrefix("p", pNs);
        }

        Resource pID = m.createResource(p.getId());
        m.add(pID, RDF.type, m.createResource(Node.PROCESS_INSTANCE));

        // process model
        if(p.processModel != null && p.processModel.getId() != null) {
            m.add(pID, m.createProperty(Node.INSTANTIATES_PROCESS), m.createResource(p.getProcessModel().getId()));
        }

        // business objects
        for(BusinessObjectInstance boi : p.businessObjects) {
            m.add(pID, m.createProperty(Node.USES_BUSINESS_OBJECT_INSTANCE), m.createResource(boi.getId()));
            boi.addToModel(m);
        }

        for(ActivityInstance ai : p.activities) {
            ai.addToModel(m);
        }

        return m;
    }
}
