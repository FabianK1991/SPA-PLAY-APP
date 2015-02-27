package spa.rest.entities;

import java.util.Set;

import spa.rest.RestController;

import com.hp.hpl.jena.rdf.model.Model;


public class ProcessInstance
{

    public static String createProcessInstance(Model model) throws Exception
    {
        return RestController.uploadModel("process_instance/create", model, "POST");
    }


    public static String updateProcessInstance(Model model) throws Exception
    {
        return RestController.uploadModel("process_instance/update", model, "PUT");
    }


    public static Model getProcessInstance(String id) throws Exception
    {
        return RestController.getModelByID("process_instance/get/", id);
    }


    public static Set<String> getProcessInstanceIDsBOI(Set<String> boiIDs) throws Exception
    {
        return RestController.searchByKeyword("process_instance/get_by_boi", boiIDs);
    }


    public static Set<String> getProcessInstanceIDsPID(String pID) throws Exception
    {
        return RestController.getIDsByID("process_instance/get_by_pid/", pID);
    }


    public static boolean deleteProcessInstance(String id) throws Exception
    {
        return RestController.delete("process_instance/delete/", id);
    }
}
