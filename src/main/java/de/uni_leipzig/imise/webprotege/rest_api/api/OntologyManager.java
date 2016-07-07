package de.uni_leipzig.imise.webprotege.rest_api.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.binaryowl.owlapi.BinaryOWLOntologyDocumentParserFactory;
import org.semanticweb.binaryowl.owlapi.BinaryOWLOntologyDocumentStorer;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.io.OWLParserFactoryRegistry;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;

import edu.stanford.smi.protege.model.Instance;

public class OntologyManager {
	private String path;
	private String rootPath;
	private String importsPath;
	
	static {
	    OWLManager.getOWLDataFactory();
	    OWLParserFactoryRegistry parserFactoryRegistry = OWLParserFactoryRegistry.getInstance();
	    List<OWLParserFactory> parserFactoryList = new ArrayList<OWLParserFactory>(parserFactoryRegistry.getParserFactories());
	    Collections.reverse(parserFactoryList);
	    parserFactoryRegistry.clearParserFactories();
	    for(OWLParserFactory parserFactory : parserFactoryList) {
	         parserFactoryRegistry.registerParserFactory(parserFactory);
	    }
	    parserFactoryRegistry.registerParserFactory(new BinaryOWLOntologyDocumentParserFactory());
	}
	
	public OntologyManager(Instance project, String dataPath) {
		path        = dataPath + "/data-store/project-data/" + project.getName();
		rootPath    = path + "/ontology-data/root-ontology.binary";
		importsPath = path + "/imports-cache";
	}
	
	public ArrayList<OWLClassProperties> getClassProperties(String className) throws OWLOntologyCreationException {
		ArrayList<OWLClassProperties> properties = new ArrayList<OWLClassProperties>();
	    	
	    Iterator<OWLClass> clsIterator = getClassesByClassName(className).iterator();
	    while (clsIterator.hasNext()) {
	    	OWLClass cls = clsIterator.next();
	    	
	    	OWLClassProperties clsProperties = new OWLClassProperties();
	    	
	    	clsProperties.iri = cls.getIRI().toString();
	    	clsProperties.addSuperClassExpressions(cls.getSuperClasses(getOntologies()));
	    	clsProperties.addSubClassExpressions(cls.getSubClasses(getOntologies()));
	    	
	    	properties.add(clsProperties);
	    }
		return properties;
	}
	
	private ArrayList<OWLClass> getClassesByClassName(String className) throws OWLOntologyCreationException {
		ArrayList<OWLClass> resultset = new ArrayList<OWLClass>();
		
		Iterator<OWLOntology> ontIterator = getOntologies().iterator();
	    while (ontIterator.hasNext()) {
	    	OWLOntology ontology = ontIterator.next();
	    	
	    	Iterator<OWLClass> clsIterator = ontology.getClassesInSignature().iterator();
	    	while (clsIterator.hasNext()) {
	    		OWLClass cls = clsIterator.next();
	    		
	    		if (cls.getIRI().getFragment().equals(className) && !resultset.contains(cls)) {
	    			resultset.add(cls);
	    		}
	    	}
	    }
		
	    return resultset;
	}
	
	public ArrayList<String> getOntologyImports() throws UnknownOWLOntologyException, OWLOntologyCreationException {
		ArrayList<String> imports = new ArrayList<String>();
		
		Iterator<OWLOntology> iterator = getImports().iterator();
		while (iterator.hasNext()) {
			imports.add(iterator.next().getOntologyID().toString());
		}
		
		return imports;
	}
	
	private OWLOntology getRootOntology() throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        manager.addOntologyStorer(new BinaryOWLOntologyDocumentStorer());
	    
        File[] cachedDocuments = (new File(importsPath)).listFiles();
		for (File ontologyDocument : cachedDocuments) {
            if (!ontologyDocument.isHidden() && !ontologyDocument.isDirectory()) {
                manager.loadOntologyFromOntologyDocument(ontologyDocument);
            }
        }
		
		OWLOntology rootOntology = manager.loadOntologyFromOntologyDocument(new File(rootPath));
		
		return rootOntology;
	}
	
	private Set<OWLOntology> getOntologies() throws OWLOntologyCreationException {
		return getRootOntology().getImportsClosure();
	}
	
	private Set<OWLOntology> getImports() throws UnknownOWLOntologyException, OWLOntologyCreationException {
		return getRootOntology().getImports();
	}
	
	public class OWLClassProperties {
		public Set<String> superclasses = new HashSet<String>();
		public Set<String> subclasses   = new HashSet<String>();
		public String iri;
		
		public void addSuperClassExpression(OWLClassExpression expression) {
			this.superclasses.add(expression.toString());
		}
		
		public void addSuperClassExpressions(Set<OWLClassExpression> expressions) {
			Iterator<OWLClassExpression> iterator = expressions.iterator();
	    	while (iterator.hasNext()) {
	    		addSuperClassExpression(iterator.next());
	    	}
		}
		
		public void addSubClassExpression(OWLClassExpression expression) {
			this.subclasses.add(expression.toString());
		}
		
		public void addSubClassExpressions(Set<OWLClassExpression> expressions) {
			Iterator<OWLClassExpression> iterator = expressions.iterator();
	    	while (iterator.hasNext()) {
	    		addSubClassExpression(iterator.next());
	    	}
		}
	}
}
