package models.core.serverModels.document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import models.core.serverModels.businessObject.BusinessObject;
import models.core.serverModels.businessObject.BusinessObjectInstance;
import models.ontology.OntologyManager;
import play.mvc.Http.MultipartFormData.FilePart;

public class Document {
    
    public static final String DOC_PATH = "data/documents/";
    
    private String id;
    private String name;
    private String description;
    private String filepath;
    private ArrayList<String> images = new ArrayList<String>();
    
    
    public Document(FilePart file, String name){
        String id = System.currentTimeMillis() + (int)(Math.random()*100) + "" + (int)(Math.random()*100);
        File content = file.getFile();    
        File f = new File(DOC_PATH + id + "+--++--+" + name);

        if( !f.exists() ){
            try {
                FileUtils.moveFile(content, f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.id = id;
        this.name = name;
        this.description = this.parseDescription(f);
        this.images.addAll(this.parseImages());
        this.filepath = f.toPath().toString();
    }
    
    public Document(){
        
    }
    
    private Document loadFromOntology(){
        
        
        
        return null;
    }
    
	private ArrayList<String> parseImages() {
        // TODO Auto-generated method stub
        return null;
    }

    private String parseDescription(File f) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getName() {
		return this.name;
	}
	
	/*
	 * TODO
	 * Returns a description of the document
	 */
	public String getDescription() {
		return this.description;
	}
	
	/*
	 * TODO
	 * Returns a description of the document
	 */
	public ArrayList<String> getImages() {
		return null;
	}
	
	
	public File getFile() {
		return new File(this.filepath);
	}
	
	/*
	 * TODO
	 * Returns the business obejct instance
	 */
	public BusinessObjectInstance getBusinessObjectInstance() {
		return null;
	}
	
	/*
	 * TODO
	 * Returns the business obejct
	 */
	public BusinessObject getBusinessObject() {
		return null;
	}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
	
	
}
