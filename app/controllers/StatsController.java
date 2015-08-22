package controllers;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;

import com.fasterxml.jackson.databind.node.ObjectNode;

//@With(ActionController.class)
public class StatsController extends Controller{

    public static Result instancesOfProcessModels() {
        ObjectNode result = Json.newObject();
        
        result.put("model1", "4");
        result.put("model2", "7");
        result.put("model3", "2");
        
        return ok(result);      
    }
    
    public static Result timePerProcessModel(){
        ObjectNode result = Json.newObject();
        
        result.put("best", "[34.5, 20, 48],");
        result.put("worst", "[54.3, 31.8, 76]");
        result.put("average", "[40.6, 26.3, 63]");

        return ok(result);      
    }
    
    public static Result newInstancesPerProcessModel(String process){
        ObjectNode result = Json.newObject();
        
        if(process.equals("ProcurementProcess")){
            result.put("Procurement Process", "[3, 4, 2, 6, 1, 4, 5]");  
        }else if(process.equals("SalesProcess")){
            result.put("Sales Process", "[2, 1, 6, 3, 4, 8, 2]");
        }else if(process.equals("CustomerInquiryProcessing")){
            result.put("Customer Inquiry Processing", "[14, 8, 18, 21, 28, 17, 8]");
        }
           
        return ok(result);      
    }
    
    public static Result processesInTime(){
        ObjectNode result = Json.newObject();        
        
        result.put("data", "91.3");
      
        return ok(result);      
    }
    
    public static Result testy() {
        response().setContentType("text/html");
        String s = "<html><head>";
        
        s = s +  "<script src=\"https://rawgit.com/masayuki0812/c3/master/c3.js\"></script>"; 
        s = s +  "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js\"></script>"; 
        s = s +  "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.6/d3.min.js\"></script>"; 
        s = s +  "<style src=\"https://rawgit.com/masayuki0812/c3/master/c3.css\"></style>"; 
        
        
        
        
        s= s + "</head><body>";
        
        s = s + "<div id=\"chart\"></div><div id=\"button\">BUTTON</div>";
        
        s = s + "<script>var defaultType = 'donut'; var alternativeType = 'bar';var chart = c3.generate({    data: {        url: 'http://localhost:9000/data/instancesOfPMs',        mimeType: 'json',        type: defaultType,    }});$('#button').data('currentType', defaultType).on('click', function(e) {    if ($(this).data('currentType') == defaultType){        $(this).data('currentType', alternativeType);    }else{        $(this).data('currentType', defaultType);    }          chart.transform($(this).data('currentType'));});</script>";
        
        s = s + "</body></html>";
        
        
        return ok(s);      
    }
    
    
    
    
}
