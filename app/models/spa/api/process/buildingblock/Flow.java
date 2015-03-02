package models.spa.api.process.buildingblock;

import java.util.Random;

import models.spa.api.ProcessModel;


public class Flow
{
    String        id;
    ProcessModel  process;
    Node          from;
    Node          to;

    String        condition;

    static String type = "Flow";


    public Flow(ProcessModel p)
    {
        this.process = p;
    }


    public Flow(ProcessModel p, Node from, Node to)
    {
        this.process = p;
        this.from = from;
        this.to = to;
    }


    public Flow(ProcessModel p, Node from, Node to, String condition)
    {
        this.process = p;
        this.from = from;
        this.to = to;
        this.condition = condition;
    }


    public String getId()
    {
        if(id == null) {
            id = this.getProcess().getNsBase() + "flow" + System.nanoTime() + new Random().nextInt(1000);
        }
        return id;
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public ProcessModel getProcess()
    {
        return process;
    }


    public void setProcess(ProcessModel process)
    {
        this.process = process;
    }


    public Node getFrom()
    {
        return from;
    }


    public void setFrom(Node from)
    {
        this.from = from;
    }


    public Node getTo()
    {
        return to;
    }


    public void setTo(Node to)
    {
        this.to = to;
    }


    public String getCondition()
    {
        return condition;
    }


    public void setCondition(String condition)
    {
        this.condition = condition;
    }
}
