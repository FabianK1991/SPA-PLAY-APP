package models.spa.api.process.buildingblock.instance;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import models.spa.api.ProcessInstance;
import models.spa.api.process.buildingblock.Node;


public class ActivityInstance
{

    String                      id       = null;
    String                      agent    = null;
    String                      dateTime = null;

    String                      activity = null;

    ProcessInstance             pi       = null;

    ActivityInstance            next     = null;

    Set<BusinessObjectInstance> boi      = new HashSet<>();


    public ActivityInstance(ProcessInstance processInstance)
    {
        this.pi = processInstance;
    }


    public ActivityInstance(ProcessInstance processInstance, ActivityInstance next)
    {
        this.pi = processInstance;
        this.next = next;
    }


    public String getId()
    {
        if(id == null) {
            if(pi != null && pi.getNsBase() != null) {
                id = this.getPi().getNsBase() + "ai" + System.nanoTime() + new Random().nextInt(1000);
            } else {
                id = "http://spa.instance/ai" + System.nanoTime() + new Random().nextInt(1000);
            }
        }
        return id;
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public String getAgent()
    {
        return agent;
    }


    public void setAgent(String agent)
    {
        this.agent = agent;
    }


    public String getDateTime()
    {
        return dateTime;
    }


    public void setDateTime(String dateTime)
    {
        this.dateTime = dateTime;
    }


    public String getActivity()
    {
        return activity;
    }


    public void setActivity(String activity)
    {
        this.activity = activity;
    }


    public ProcessInstance getPi()
    {
        return pi;
    }


    public void setPi(ProcessInstance pi)
    {
        this.pi = pi;
    }


    public ActivityInstance getNext()
    {
        return next;
    }


    public void setNext(ActivityInstance next)
    {
        this.next = next;
    }


    public Set<BusinessObjectInstance> getBoi()
    {
        return boi;
    }


    public void setBoi(Set<BusinessObjectInstance> boi)
    {
        this.boi = boi;
    }


    public void addToModel(Model m)
    {
        Resource ai = m.createResource(getId());
        m.add(ai, RDF.type, m.createResource(Node.ACTIVITY_INSTANCE));

        if(activity != null) {
            m.add(ai, m.createProperty(Node.INSTANTIATES_ACTIVITY), m.createResource(activity));
        }

        if(pi != null) {
            m.add(ai, m.createProperty(Node.BELONGS_TO), m.createResource(pi.getId()));
        }

        if(agent != null) {
            m.add(ai, m.createProperty(Node.PERFORMED_BY), m.createResource(agent));
        }

        if(dateTime != null) {
            m.add(ai, m.createProperty(Node.PERFORMED_AT), m.createLiteral(dateTime));
        }

        if(next != null) {
            m.add(ai, m.createProperty(Node.FOLLOWED_BY), m.createResource(next.getId()));
        }

        for(BusinessObjectInstance bo : boi) {
            m.add(ai, m.createProperty(Node.USES_BUSINESS_OBJECT_INSTANCE), m.createResource(bo.getId()));
            bo.addToModel(m);
        }

    }

}
