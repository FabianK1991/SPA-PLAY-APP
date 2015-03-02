package models.spa.api.process.buildingblock;

import models.spa.api.ProcessModel;

import com.hp.hpl.jena.rdf.model.Model;


public class BusinessObject extends Node
{
    static String type = "BusinessObject";


    public BusinessObject(ProcessModel p)
    {
        super(p);
    }


    @Override
    public void addToModel(Model m)
    {
        addToModel2(m, type);
    }

}
