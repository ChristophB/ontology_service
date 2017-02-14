package de.onto_med.webprotege_rest_api.api;

import org.apache.commons.lang3.StringUtils;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

/**
 * Abstract filter class.
 * @author Christoph Beger
 */
public abstract class Filter {
	/**
	 * Threshhold for Jaro Winkler Distance comparisson.
	 */
	private static final Double THRESHOLD = 0.8;
	
	public static boolean run(OWLEntity entity, String name, Class<?> cls, Boolean exact, OWLOntology ontology) {
		if (!cls.isAssignableFrom(entity.getClass())) return false;
		
		if (exact) {
			return exactMatch(entity, name, ontology);
		} else {
			return looseMatch(entity, name, ontology);
		}
	}
	
	private static boolean exactMatch(OWLEntity entity, String name, OWLOntology ontology) {
		return XMLUtils.getNCNameSuffix(entity.getIRI()).equals(name)
			|| getLabel(entity, ontology).equals(name);
	}
	
	private static boolean looseMatch(OWLEntity entity, String name, OWLOntology ontology) {
		return StringUtils.getJaroWinklerDistance(XMLUtils.getNCNameSuffix(entity.getIRI()), name) >= THRESHOLD
			|| StringUtils.getJaroWinklerDistance(getLabel(entity, ontology), name) >= THRESHOLD;
	}
	
	private static String getLabel(OWLEntity entity, OWLOntology ontology) {
		for (OWLAnnotation a : EntitySearcher.getAnnotations(entity, ontology)) {
			if (a.getValue() instanceof OWLLiteral)
				return ((OWLLiteral) a.getValue()).getLiteral();
		}
		return "";
	}
}
