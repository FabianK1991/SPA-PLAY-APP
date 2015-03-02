package models.spa.api.process.buildingblock.instance;

import java.util.Random;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import models.spa.api.ProcessInstance;
import models.spa.api.process.buildingblock.Node;


public class BusinessObjectInstance
{

    String          id             = null;
    String          businessObject = null;
    String          label          = null;

    ProcessInstance pi             = null;


    public BusinessObjectInstance(ProcessInstance processInstance)
    {
        this.pi = processInstance;
    }


    public String getId()
    {
        if(id == null) {
            if(pi != null && pi.getNsBase() != null) {
                id = this.getPi().getNsBase() + "boi" + System.nanoTime() + new Random().nextInt(1000);
            } else {
                id = "http://spa.instance/bo" + System.nanoTime() + new Random().nextInt(1000);
            }
        }
        return id;
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public String getBusinessObject()
    {
        return businessObject;
    }


    public void setBusinessObject(String businessObject)
    {
        this.businessObject = businessObject;
    }


    public ProcessInstance getPi()
    {
        return pi;
    }


    public void setPi(ProcessInstance pi)
    {
        this.pi = pi;
    }


    public String getLabel()
    {
        return label;
    }


    public void setLabel(String label)
    {
        this.label = label;
    }


    public void addToModel(Model m)
    {
        Resource boi = m.createResource(id);
        m.add(boi, RDF.type, m.createResource(Node.BUSINESS_OBJECT_INSTANCE));
        if(businessObject != null) {
            m.add(boi, m.createProperty(Node.INSTANTIATES_BUSINESS_OBJECT), m.createResource(businessObject));
        }

        if(label != null) {
            m.add(boi, RDFS.label, m.createLiteral(label));
        }

    }
}
