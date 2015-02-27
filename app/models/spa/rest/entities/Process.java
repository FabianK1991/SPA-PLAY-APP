package spa.rest.entities;

import java.util.Set;

import spa.rest.RestController;

import com.hp.hpl.jena.rdf.model.Model;


public class Process
{

    public static String createProcess(Model model) throws Exception
    {
        return RestController.uploadModel("process/create", model, "POST");
    }


    public static String updateProcess(Model model) throws Exception
    {
        return RestController.uploadModel("process/update", model, "PUT");
    }


    public static boolean deleteProcess(String id) throws Exception
    {
        return RestController.delete("process/delete/", id);
    }


    public static Model getProcess(String id) throws Exception
    {
        return RestController.getModelByID("process/get/", id);
    }


    public static Set<String> getProcessIDsKeyword(Set<String> keywords) throws Exception
    {
        return RestController.searchByKeyword("process/get_by_keywords", keywords);
    }


    public static Set<String> getProcessIDsBO(Set<String> boIDs) throws Exception
    {
        return RestController.searchByKeyword("process/get_by_bo", boIDs);
    }
}
