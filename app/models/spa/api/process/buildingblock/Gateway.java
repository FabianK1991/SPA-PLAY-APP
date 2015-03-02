package models.spa.api.process.buildingblock;

import com.hp.hpl.jena.rdf.model.Model;

import models.spa.api.ProcessModel;


public class Gateway extends Node
{
    public enum GatewayType {
        AND, OR, XOR
    };


    public Gateway(ProcessModel process, GatewayType type)
    {
        super(process);
        this.type = type.toString();
    }


    @Override
    public void addToModel(Model m)
    {
        addToModel2(m, type);
    }

}
