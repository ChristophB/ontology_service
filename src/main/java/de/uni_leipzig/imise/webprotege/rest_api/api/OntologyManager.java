package de.uni_leipzig.imise.webprotege.rest_api.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.binaryowl.owlapi.BinaryOWLOntologyDocumentParserFactory;
import org.semanticweb.binaryowl.owlapi.BinaryOWLOntologyDocumentStorer;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.io.OWLParserFactoryRegistry;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

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
	
	public ArrayList<Object> getClassPropertiesByName(String name) throws Exception {	
		Filter filter = new Filter() {
			@Override public boolean run(OWLEntity a, String b) {
				return a.isOWLClass() && a.getIRI().getFragment().equals(b);
			}
		};
	    
	    return getPropertiesForOWLEntityIterator(extractEntitiesWithFilter(name, filter).iterator());
	}
	
	public ArrayList<Object> getNamedIndividualPropertiesByName(String name) throws Exception {
		Filter filter = new Filter() {
			@Override public boolean run(OWLEntity a, String b) {
				return a.isOWLNamedIndividual() && a.getIRI().getFragment().equals(b);
			}
		};
		
		return getPropertiesForOWLEntityIterator(extractEntitiesWithFilter(name, filter).iterator());
	}
	
	public ArrayList<Object> getClassPropertiesByProperty(String property) throws Exception {
		Filter filter = new Filter() {
			@Override public boolean run(OWLEntity a, String b) { 
				return a.isOWLClass()
					&& (
						a.getDataPropertiesInSignature().contains(b)
						|| a.getObjectPropertiesInSignature().contains(b)
						// annotationproperties missing
					);
			}
		};

	    return getPropertiesForOWLEntityIterator(extractEntitiesWithFilter(property, filter).iterator());
	}
	
	public ArrayList<Object> getNamedIndividualPropertiesByProperty(String property) throws Exception {
		Filter filter = new Filter() {
			@Override public boolean run(OWLEntity a, String b) { 
				return a.isOWLNamedIndividual()
					&& (
						a.getDataPropertiesInSignature().contains(b)
						|| a.getObjectPropertiesInSignature().contains(b)
						// annotationproperties missing
					);
			}
		};

		return getPropertiesForOWLEntityIterator(extractEntitiesWithFilter(property, filter).iterator());
	}
	
	public ArrayList<Object> getEntityPropertiesByName(String name) throws OWLOntologyCreationException, Exception {
		Filter filter = new Filter() {
			@Override public boolean run(OWLEntity a, String b) {
				return a.getIRI().getFragment().equals(b);
			}
		};
		return getPropertiesForOWLEntityIterator(extractEntitiesWithFilter(name, filter).iterator());
	}
	
	
	
	
 	private ArrayList<Object> getPropertiesForOWLEntityIterator(Iterator<OWLEntity> iterator) throws Exception {
		ArrayList<Object> properties = new ArrayList<Object>();

		while (iterator.hasNext()) {
	    	properties.add(getPropertiesForOWLEntity(iterator.next()));
	    }
		return properties;
	}
	
	private Object getPropertiesForOWLEntity(OWLEntity entity) throws Exception {
		if (entity.isOWLClass()) {
			return getPropertiesForOWLClass((OWLClass) entity);
		} else if (entity.isOWLNamedIndividual()) {
			return getPropertiesForOWLNamedIndividual((OWLNamedIndividual) entity);
		}
		
		throw new Exception("OWLEntity is neither OWLCLass nor OWLNamedIndividual.");
	}
	
	private OWLClassProperties getPropertiesForOWLClass(OWLClass cls) throws OWLOntologyCreationException {
		OWLClassProperties properties = new OWLClassProperties();
    	OWLOntology ontology = getRootOntology();
    	
    	properties.iri = cls.getIRI().toString();
    	properties.addSuperClassExpressions(cls.getSuperClasses(getOntologies()));
    	properties.addSubClassExpressions(cls.getSubClasses(getOntologies()));
    	
    	for (OWLAnnotationProperty property : ontology.getAnnotationPropertiesInSignature()) {
			Set<OWLAnnotation> values = cls.getAnnotations(ontology, property);
			properties.addAnnotationProperty(property, values);
		}
    	
    	return properties;
	}
	
	private OWLNamedIndividualProperties getPropertiesForOWLNamedIndividual(OWLNamedIndividual individual) throws OWLOntologyCreationException {
		OWLNamedIndividualProperties properties = new OWLNamedIndividualProperties();
		OWLOntology ontology = getRootOntology();
		
		properties.iri = individual.getIRI().toString();
		
		for (OWLDataProperty property : individual.getDataPropertiesInSignature()) {
			Set<OWLLiteral> values = individual.getDataPropertyValues(property, ontology);
			properties.addDataProperty(property, values);
		}
		
		for (OWLObjectProperty property : individual.getObjectPropertiesInSignature()) {
			Set<OWLIndividual> values = individual.getObjectPropertyValues(property, ontology);
			properties.addObjectProperty(property, values);
		}
		
		for (OWLAnnotationProperty property : ontology.getAnnotationPropertiesInSignature()) {
			Set<OWLAnnotation> values = individual.getAnnotations(ontology, property);
			properties.addAnnotationProperty(property, values);
		}
		
		for (OWLClassExpression type : individual.getTypes(ontology)) {
			properties.addTypeExpression(type);
		}
		
		return properties;
	}
		
	private ArrayList<OWLEntity> extractEntitiesWithFilter(String name, Filter filter) throws OWLOntologyCreationException {		
		ArrayList<OWLEntity> resultset = new ArrayList<OWLEntity>();
		
		OWLOntology ontology = getRootOntology();
	    	
	    Iterator<OWLEntity> entityIterator = ontology.getSignature(true).iterator();
	    while (entityIterator.hasNext()) {
	    	OWLEntity entity = entityIterator.next();
	    		
	    	if (!filter.run(entity, name)) continue;
	    		
	    	if (!resultset.contains(entity))
	    		resultset.add(entity);
	    }
		
	    return resultset;
	}
	
	
	
	public ArrayList<String> getOntologyImports() throws OWLOntologyCreationException {
		ArrayList<String> imports = new ArrayList<String>();
		
		Iterator<OWLOntology> iterator = getRootOntology().getImports().iterator();
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
	
	
	
	
	abstract class Filter {
		public abstract boolean run(OWLEntity a, String b);
	}

}
