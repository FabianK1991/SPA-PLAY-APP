package models.spa.api.process.buildingblock;

import com.hp.hpl.jena.rdf.model.Model;

import models.spa.api.ProcessModel;


public class Event extends Node
{
    public enum EventType {
        Start, End
    };

    String type = "Event";


    public Event(ProcessModel process, EventType type)
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
