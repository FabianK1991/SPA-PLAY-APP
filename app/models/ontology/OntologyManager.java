package models.ontology;

import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import play.Application;
import play.GlobalSettings;
import play.Logger;


public class OntologyManager extends GlobalSettings {
  
  public static OWLOntology ontology = null;
  public static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
  public static OWLDataFactory factory = manager.getOWLDataFactory();
  
  public void onStart(Application app) {
    try{
      ontology = manager.loadOntologyFromOntologyDocument(new File(Settings.PATH_TO_ONTOLOGY));
    }
    catch (Exception e) {
      Logger.info(e.toString());
      Logger.info("Ontology could not be loaded.");
    }
    Logger.info("Ontology was succesfully loaded.");
  }

  public void onStop(Application app) {
    saveOntology();
    Logger.info("Application shutdown...");
  }
  
  
  public void saveOntology(){
    try {
      manager.saveOntology(ontology);
    } catch (OWLOntologyStorageException e) {
      Logger.info("Ontology could not be saved");
    }
  }

  
}
