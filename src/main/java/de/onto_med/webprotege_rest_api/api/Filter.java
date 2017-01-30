package de.onto_med.webprotege_rest_api.api;

import org.apache.commons.lang3.StringUtils;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Abstract filter class.
 * @author Christoph Beger
 */
public abstract class Filter {
	/**
	 * Threshhold for Jaro Winkler Distance comparisson.
	 */
	private static final Double THRESHOLD = 0.8;
	
	public static boolean run(OWLEntity entity, String name, Class<?> cls, Boolean exact) {
		if (!cls.isAssignableFrom(entity.getClass())) return false;
		
		if (exact) {
			return exactMatch(entity, name);
		} else {
			return looseMatch(entity, name);
		}
	}
	
	private static boolean exactMatch(OWLEntity entity, String name) {
		return XMLUtils.getNCNameSuffix(entity.getIRI()).equals(name);
	}
	
	private static boolean looseMatch(OWLEntity entity, String name) {
		return StringUtils.getJaroWinklerDistance(XMLUtils.getNCNameSuffix(entity.getIRI()), name) >= THRESHOLD;
	}
}
