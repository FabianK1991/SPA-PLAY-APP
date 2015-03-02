package models.spa.api.process.buildingblock;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import models.spa.api.ProcessModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


public abstract class Node
{
    protected String              id;
    protected ProcessModel        process;
    protected String              name;
    protected Set<Flow>           nextFlows                     = new HashSet<Flow>();
    protected Set<String>         keywords                      = new HashSet<String>();
    protected Set<BusinessObject> businessObjects               = new HashSet<BusinessObject>();

    public static String          mainURI                       = "http://www.spa.org/core#";
    public String                 type                          = "Node";

    // constants
    public final static String    PROCESS                       = mainURI + "Process";

    public final static String    NODE                          = mainURI + "Node";
    public final static String    ACTIVITY                      = mainURI + "Activity";
    public final static String    BUSINESS_OBJECT               = mainURI + "BusinessObject";

    public final static String    GATEWAY                       = mainURI + "Gateway";
    public final static String    AND                           = mainURI + "AND";
    public final static String    OR                            = mainURI + "OR";
    public final static String    XOR                           = mainURI + "XOR";

    public final static String    EVENT                         = mainURI + "Event";
    public final static String    START                         = mainURI + "Start";
    public final static String    END                           = mainURI + "End";

    public final static String    FLOW                          = mainURI + "Flow";

    public final static String    AGENT                         = mainURI + "Agent";
    public final static String    PROCESS_INSTANCE              = mainURI + "ProcessInstance";
    public final static String    ACTIVITY_INSTANCE             = mainURI + "ActivityInstance";
    public final static String    BUSINESS_OBJECT_INSTANCE      = mainURI + "BusinessObjectInstance";

    public final static String    BELONGS_TO                    = mainURI + "belongsTo";
    public final static String    COMPOSED_OF                   = mainURI + "composedOf";
    public final static String    FLOW_TO_NODE                  = mainURI + "flowToNode";
    public final static String    FOLLOWED_BY                   = mainURI + "followedBy";
    public final static String    INSTANTIATES_ACTIVITY         = mainURI + "instantiatesActivity";
    public final static String    INSTANTIATES_BUSINESS_OBJECT = mainURI + "instantiatesBusinessObject";
    public final static String    INSTANTIATES_PROCESS         = mainURI + "instantiatesProcess";
    public final static String    NODE_TO_FLOW                  = mainURI + "nodeToFlow";
    public final static String    PERFORMED_BY                  = mainURI + "performedBy";
    public final static String    USES_BUSINESS_OBJECT          = mainURI + "usesBusinessObject";
    public final static String    USES_BUSINESS_OBJECT_INSTANCE = mainURI + "usesBusinessObjectInstance";

    public final static String    KEYWORDS                      = mainURI + "keywords";
    public final static String    CONDITION                     = mainURI + "condition";
    public final static String    PERFORMED_AT                  = mainURI + "performedAt";


    public Node(ProcessModel process)
    {
        this.process = process;
    }


    public String getId()
    {
        if(id == null) {
            id = this.getProcess().getNsBase() + "node" + System.nanoTime() + new Random().nextInt(1000);
        }
        return id;
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public Set<Flow> getNextFlows()
    {
        return nextFlows;
    }


    public void setNextFlows(Set<Flow> nextFlows)
    {
        this.nextFlows = nextFlows;
    }


    public ProcessModel getProcess()
    {
        return process;
    }


    public void setProcess(ProcessModel process)
    {
        this.process = process;
    }


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public Set<String> getKeywords()
    {
        return keywords;
    }


    public void setKeywords(Set<String> keywords)
    {
        this.keywords = keywords;
    }


    public Set<BusinessObject> getBusinessObjects()
    {
        return businessObjects;
    }


    public void setBusinessObjects(Set<BusinessObject> businessObjects)
    {
        this.businessObjects = businessObjects;
    }


    public String getTypeURI()
    {
        return mainURI + type;
    }


    public abstract void addToModel(Model m);


    protected void addToModel2(Model m, String nodeType)
    {
        Resource node = m.createResource(getId());

        m.add(node, RDF.type, m.createResource(mainURI + nodeType));

        m.add(m.createResource(process.getId()), m.createProperty(COMPOSED_OF), node);

        // label
        if(name != null) {
            m.add(node, RDFS.label, m.createLiteral(name));
        }

        // keywords
        if(keywords != null) {
            for(String k : keywords) {
                m.add(node, m.createProperty(KEYWORDS), m.createLiteral(k));
            }
        }

        // business objects
        for(BusinessObject bo : businessObjects) {
            m.add(node, m.createProperty(Node.USES_BUSINESS_OBJECT), m.createResource(bo.getId()));
            bo.addToModel(m);
        }

        // flow
        for(Flow f : nextFlows) {
            Resource fRes = m.createResource(f.getId());
            m.add(fRes, RDF.type, m.createResource(FLOW));
            m.add(m.createResource(process.getId()), m.createProperty(COMPOSED_OF), fRes);
            m.add(node, m.createProperty(NODE_TO_FLOW), fRes);
            m.add(fRes, m.createProperty(FLOW_TO_NODE), m.createResource(f.getTo().getId()));
            if(f.getCondition() != null) {
                m.add(fRes, m.createProperty(CONDITION), m.createLiteral(f.getCondition()));
            }
        }

    }
}