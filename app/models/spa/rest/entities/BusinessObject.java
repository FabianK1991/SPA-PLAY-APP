package spa.rest.entities;

import java.util.Set;

import spa.rest.RestController;

import com.hp.hpl.jena.rdf.model.Model;


public class BusinessObject
{
    public static Model getBusinessObject(String id) throws Exception
    {
        return RestController.getModelByID("/business_object/get/", id);
    }


    public static Set<String> getBusinessObjectByKeywords(Set<String> keywords) throws Exception
    {
        return RestController.searchByKeyword("/business_object/get_by_keywords", keywords);
    }


    public static Set<String> getBusinessObjectsByProcessID(String processID) throws Exception
    {
        return RestController.getIDsByID("/business_object/get_by_pid/", processID);
    }


    public static boolean deleteBusinessObject(String id) throws Exception
    {
        return RestController.delete("business_object/delete/", id);
    }
}
