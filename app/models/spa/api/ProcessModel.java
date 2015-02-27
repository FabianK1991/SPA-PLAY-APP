package models.spa.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import models.spa.api.process.buildingblock.Activity;
import models.spa.api.process.buildingblock.BusinessObject;
import models.spa.api.process.buildingblock.Event;
import models.spa.api.process.buildingblock.Event.EventType;
import models.spa.api.process.buildingblock.Flow;
import models.spa.api.process.buildingblock.Gateway;
import models.spa.api.process.buildingblock.Gateway.GatewayType;
import models.spa.api.process.buildingblock.Node;

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


public class ProcessModel
{

    private String      id              = null;
    private String      nsBase          = null;
    private String      name            = null;
    private String      description     = null;

    Set<Node>           nodes           = new HashSet<Node>();
    Set<BusinessObject> businessObjects = new HashSet<>();
    Set<String>         keywords        = new HashSet<String>();


    public ProcessModel()
    {

    }


    public ProcessModel(String id)
    {
        this.id = id;
    }


    public String getId()
    {
        return id;
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public String getNsBase()
    {
        return nsBase;
    }


    public void setNsBase(String nsBase)
    {
        this.nsBase = nsBase;
    }


    public Set<Node> getNodes()
    {
        return nodes;
    }


    public void setNodes(Set<Node> nodes)
    {
        this.nodes = nodes;
    }


    public Set<BusinessObject> getBusinessObjects()
    {
        return businessObjects;
    }


    public void setBusinessObjects(Set<BusinessObject> businessObjects)
    {
        this.businessObjects = businessObjects;
    }


    public Set<String> getKeywords()
    {
        return keywords;
    }


    public void setKeywords(Set<String> keywords)
    {
        this.keywords = keywords;
    }


    public String getDescription()
    {
        return description;
    }


    public void setDescription(String description)
    {
        this.description = description;
    }


    public Set<Activity> getAllActivities()
    {
        Set<Activity> activities = new HashSet<>();
        for(Node n : getNodes()) {
            if(n instanceof Activity) {
                activities.add((Activity) n);
            }
        }
        return activities;
    }


    public Set<BusinessObject> getAllBusisnessObjects()
    {
        Set<BusinessObject> bos = new HashSet<>();
        for(Node n : getNodes()) {
            if(n instanceof BusinessObject) {
                bos.add((BusinessObject) n);
            } else if(n.getBusinessObjects() != null) {
                bos.addAll(n.getBusinessObjects());
            }
        }

        if(businessObjects != null) {
            bos.addAll(businessObjects);
        }

        return bos;
    }


    public void saveModelToFile(String file) throws Exception
    {
        Model model = modelToRDF(this);
        model.write(new FileWriter(new File(file)), "TURTLE");
    }


    public String store() throws Exception
    {
        return spa.rest.entities.Process.createProcess(modelToRDF(this));
    }


    public String update() throws Exception
    {
        return spa.rest.entities.Process.updateProcess(modelToRDF(this));
    }


    public boolean delete() throws Exception
    {
        return spa.rest.entities.Process.deleteProcess(this.getId());
    }


    public static boolean delete(String id) throws Exception
    {
        return spa.rest.entities.Process.deleteProcess(id);
    }


    public static List<ProcessModel> getAllProcesses() throws Exception
    {
        Set<String> ids = spa.rest.entities.Process.getProcessIDsKeyword(new HashSet<String>());
        List<ProcessModel> processes = new ArrayList<ProcessModel>();

        for(String id : ids) {
            processes.add(rdfToModel(spa.rest.entities.Process.getProcess(id)));
        }

        return processes;
    }


    public static ProcessModel getProcess(String id) throws Exception
    {
        return rdfToModel(spa.rest.entities.Process.getProcess(id));
    }


    public static List<ProcessModel> searchByKeywords(Set<String> keywords) throws Exception
    {
        Set<String> ids = spa.rest.entities.Process.getProcessIDsKeyword(keywords);

        List<ProcessModel> processes = new ArrayList<ProcessModel>();

        for(String id : ids) {
            processes.add(rdfToModel(spa.rest.entities.Process.getProcess(id)));
        }

        return processes;
    }


    public static List<ProcessModel> searchByBusinessObjects(Set<String> boURIs) throws Exception
    {
        Set<String> ids = spa.rest.entities.Process.getProcessIDsBO(boURIs);

        List<ProcessModel> processes = new ArrayList<ProcessModel>();

        for(String id : ids) {
            processes.add(rdfToModel(spa.rest.entities.Process.getProcess(id)));
        }

        return processes;
    }


    public static ProcessModel loadProcessModel(String file) throws Exception
    {
        Model model = ModelFactory.createDefaultModel();
        model.read(new FileInputStream(file), null, "TURTLE");

        return rdfToModel(model);
    }


    public static ProcessModel createNewProcess(String file) throws Exception
    {
        Model model = ModelFactory.createDefaultModel();
        model.read(new FileInputStream(file), null, "TURTLE");

        spa.rest.entities.Process.createProcess(model);

        return rdfToModel(model);
    }


    private static ProcessModel rdfToModel(Model model)
    {
        // get id
        StmtIterator iter1 = model.listStatements(null, RDF.type, new ResourceImpl(Node.PROCESS));
        String id = null;
        while(iter1.hasNext()) {
            id = iter1.next().getSubject().toString();
        }

        ProcessModel p = new ProcessModel(id);
        Map<String, Node> nodes = new HashMap<>();

        // get associated nodes
        Set<String> composedOf = new HashSet<String>();
        StmtIterator iter2 = model.listStatements(new SimpleSelector(model.createResource(id), (Property) null, (RDFNode) null));
        while(iter2.hasNext()) {
            Statement s = iter2.next();
            Property predicate = s.getPredicate();
            String object = s.getObject().toString();

            String pred = predicate.toString();
            if(pred.equals(RDF.type.toString())) {
            } else if(pred.equals(RDFS.label.toString())) {
                p.setName(object);
            } else if(pred.equals(RDFS.comment.toString())) {
                p.setDescription(object);
            } else if(pred.equals(Node.COMPOSED_OF)) {
                composedOf.add(object);
            } else if(pred.equals(Node.USES_BUSINESS_OBJECT)) {
                BusinessObject bo = new BusinessObject(p);
                bo.setId(object);
                p.getBusinessObjects().add(bo);
                nodes.put(object, bo);
            } else if(pred.equals(Node.KEYWORDS)) {
                p.getKeywords().add(object);
            } else {
                System.err.println(s);

            }
        }

        // instantiate associated nodes
        for(String nID : composedOf) {
            StmtIterator iter3 = model.listStatements(new SimpleSelector(model.createResource(nID), RDF.type, (RDFNode) null));
            while(iter3.hasNext()) {
                Statement s = iter3.next();
                Node node = null;
                String n = s.getObject().toString();

                if(n.equals(Node.START)) {
                    node = new Event(p, EventType.Start);
                } else if(n.equals(Node.END)) {
                    node = new Event(p, EventType.End);
                } else if(n.equals(Node.FLOW)) {
                    // flow will be instantiated in the next step
                } else if(n.equals(Node.ACTIVITY)) {
                    node = new Activity(p);
                } else if(n.equals(Node.BUSINESS_OBJECT)) {
                    node = new BusinessObject(p);
                } else if(n.equals(Node.AND)) {
                    node = new Gateway(p, GatewayType.AND);
                } else if(n.equals(Node.OR)) {
                    node = new Gateway(p, GatewayType.OR);
                } else if(n.equals(Node.XOR)) {
                    node = new Gateway(p, GatewayType.XOR);
                } else {
                    System.err.println(s);
                }

                if(node != null) {
                    node.setId(nID);
                    nodes.put(nID, node);
                }
            }
        }

        // load information about associated nodes
        Set<String> elements = new HashSet<String>(nodes.keySet());
        while(!elements.isEmpty()) {
            String nID = elements.iterator().next();
            elements.remove(nID);

            StmtIterator iter4 = model.listStatements(new SimpleSelector(model.createResource(nID), (Property) null, (RDFNode) null));
            while(iter4.hasNext()) {
                Statement s = iter4.next();
                String object = s.getObject().toString();
                String prop = s.getPredicate().toString();

                if(prop.equals(Node.KEYWORDS)) {
                    nodes.get(nID).getKeywords().add(object);
                } else if(prop.equals(Node.USES_BUSINESS_OBJECT)) {
                    if(!nodes.containsKey(object)) {
                        elements.add(object);
                    }
                    BusinessObject bo = (BusinessObject) (nodes.containsKey(object) ? nodes.get(object) : new BusinessObject(p));
                    bo.setId(object);
                    nodes.get(nID).getBusinessObjects().add(bo);
                    nodes.put(object, bo);
                } else if(prop.equals(Node.NODE_TO_FLOW)) {
                } else if(prop.equals(RDFS.label.toString())) {
                    nodes.get(nID).setName(object);
                } else if(prop.equals(RDF.type.toString())) {
                } else {
                    System.err.println(s);
                }
            }
        }

        // create flow
        Map<String, Flow> flows = new HashMap<String, Flow>();
        StmtIterator iter4 = model.listStatements(null, (Property) model.createProperty(Node.NODE_TO_FLOW), (RDFNode) null);
        while(iter4.hasNext()) {
            Statement s = iter4.next();
            String nID = s.getSubject().toString();
            String flowID = s.getObject().toString();
            Flow flow = new Flow(p);
            flow.setId(flowID);
            flow.setFrom(nodes.get(nID));

            flows.put(flowID, flow);

            nodes.get(nID).getNextFlows().add(flow);

        }

        StmtIterator iter5 = model.listStatements(null, (Property) model.createProperty(Node.FLOW_TO_NODE), (RDFNode) null);
        while(iter5.hasNext()) {
            Statement s = iter5.next();
            String flowID = s.getSubject().toString();
            String nodeID = s.getObject().toString();

            Flow flow = flows.get(flowID);
            flow.setTo(nodes.get(nodeID));
        }

        // flow information
        for(String fID : flows.keySet()) {
            StmtIterator iter6 = model.listStatements(model.createResource(fID), (Property) null, (RDFNode) null);
            Flow flow = flows.get(fID);
            while(iter6.hasNext()) {
                Statement s = iter6.next();
                String prop = s.getPredicate().toString();
                if(prop.equals(RDF.type.toString())) {
                    // do nothing
                } else if(prop.equals(Node.NODE_TO_FLOW)) {
                    // do nothing
                } else if(prop.equals(Node.FLOW_TO_NODE)) {
                    // do nothing
                } else if(prop.equals(Node.CONDITION)) {
                    flow.setCondition(s.getObject().toString());
                } else {
                    System.err.println(s);
                }

            }
        }

        p.nodes = new HashSet<Node>(nodes.values());
        return p;
    }


    private static Model modelToRDF(ProcessModel p)
    {
        Model m = ModelFactory.createDefaultModel();
        m.setNsPrefix("spa", Node.mainURI);
        m.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        m.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        m.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");

        p.generateID();

        m.setNsPrefix("p", p.nsBase);

        Resource pID = m.createResource(p.id);
        m.add(pID, RDF.type, m.createResource(Node.PROCESS));

        // name
        if(p.name != null) {
            m.add(pID, RDFS.label, m.createLiteral(p.name));
        }

        if(p.description != null) {
            m.add(pID, RDFS.comment, m.createLiteral(p.description));
        }

        // keywords
        for(String k : p.keywords) {
            m.add(pID, m.createProperty(Node.KEYWORDS), m.createLiteral(k));
        }

        // business objects
        for(BusinessObject bo : p.businessObjects) {
            m.add(pID, m.createProperty(Node.USES_BUSINESS_OBJECT), m.createResource(bo.getId()));
            bo.addToModel(m);

        }

        // nodes
        for(Node n : p.nodes) {
            n.addToModel(m);
        }

        return m;
    }


    private void generateID()
    {
        if(id == null) {
            id = "http://spa.model/p" + System.nanoTime() + new Random().nextInt(1000);
        }

        int x = Math.max(id.lastIndexOf("/"), id.lastIndexOf("#"));
        nsBase = id.substring(0, x + 1);

    }


    public Set<ProcessInstance> getAllInstances() throws Exception
    {
        Set<String> piURI = spa.rest.entities.ProcessInstance.getProcessInstanceIDsPID(id);
        Set<ProcessInstance> pis = new HashSet<ProcessInstance>();
        for(String piId : piURI) {
            pis.add(ProcessInstance.getProcessInstance(this, piId));
        }

        return pis;

    }

}