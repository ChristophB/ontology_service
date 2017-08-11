package de.onto_med.ontology_service.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.onto_med.ontology_service.data_models.Individual;
import de.onto_med.ontology_service.data_models.Phenotype;
import de.onto_med.ontology_service.views.PhenotypeFormView;
import de.onto_med.ontology_service.views.RestApiView;

import org.lha.phenoman.man.PhenotypeOntologyManager;
import org.lha.phenoman.model.AbstractBooleanPhenotype;
import org.lha.phenoman.model.AbstractCalculationPhenotype;
import org.lha.phenoman.model.AbstractSinglePhenotype;
import org.lha.phenoman.model.RestrictedSinglePhenotype;
import org.lha.phenoman.model.top_level.AbstractPhenotype;
import org.lha.phenoman.model.top_level.RestrictedPhenotype;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;

@Path("/phenotype")
public class PhenotypeResource extends Resource {
	final static Logger logger = LoggerFactory.getLogger(PhenotypeResource.class);
	
	private String phenotypePath;
	
	private PhenotypeOntologyManager manager;
	
	public PhenotypeResource(String rootPath, String phenotypePath) {
		super(rootPath);
		this.phenotypePath = phenotypePath;
		manager = new PhenotypeOntologyManager(phenotypePath, false);
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getPhenotypeView() {
		return Response.ok(new RestApiView("PhenotypeView.ftl", rootPath)).build();
	}
	
	@GET
	@Path("/{iri}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPhenotype(@PathParam("iri") String iri) {
		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(phenotypePath, false);
		return Response.ok(manager.getPhenotype(XMLUtils.getNCNameSuffix(iri))).build();
	}
	
	@GET
	@Path("/all")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response getPhenotypeTaxonomy(@Context HttpHeaders headers, @QueryParam("type") String type) {
		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(phenotypePath, false);
		
		if (acceptsMediaType(headers, MediaType.APPLICATION_JSON_TYPE)) {
			return Response.ok(manager.getPhenotypeCategoryTree(true)).build();
		} else {
			return Response.ok(new RestApiView("AllPhenotypes.ftl", rootPath)).build();
		}
	}
	
	@GET
	@Path("/decision-tree")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getDecisionTree(@QueryParam("phenotype") String phenotype, @QueryParam("language") String language) {
		if (StringUtils.isBlank(phenotype)) throw new WebApplicationException("Query parameter 'phenotype' missing.");
		
		// TODO: generate GraphML representation of the phenotypes decision tree
		// StringUtils.defaultString(language, "en");
		
		return Response.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename='" + phenotype + "_decisiontree.GraphML'").build();
	}
	
	@GET
	@Path("/decision-tree-form")
	@Produces(MediaType.TEXT_HTML)
	public Response getDecisionTreeForm() {
		return Response.ok(new RestApiView("DecisionTreeForm.ftl", rootPath)).build();
	}
	
	@GET
	@Path("/singlephenotype-form")
	@Produces(MediaType.TEXT_HTML)
	public Response getSinglePhenotypeForm(@BeanParam Phenotype phenotype) {
		return Response.ok(new PhenotypeFormView("SinglePhenotypeForm.ftl", rootPath, phenotype)).build();
	}
	
	@GET
	@Path("/compositphenotype-form")
	@Produces(MediaType.TEXT_HTML)
	public Response getCompositPhenotypeForm(@BeanParam Phenotype phenotype) {
		return Response.ok(new PhenotypeFormView("CompositPhenotypeForm.ftl", rootPath, phenotype)).build();
	}
	
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response createPhenotype(@Context HttpHeaders headers, @BeanParam Phenotype formData) {
		// TODO: redirect to form after creation succeded or failed and show message
		RestApiView view = new RestApiView(formData.getType().equals("single") ? "SinglePhenotypeForm.ftl" : "CompositPhenotypeForm.ftl", rootPath);
		
		/*** form data validation ***/
		if (StringUtils.isBlank(formData.getId()) || StringUtils.isBlank(formData.getDatatype())) {
			view.addMessage("danger", "ID and/or Datatype is missing.");
			return Response.ok(view).build();
		}
		
		switch (formData.getDatatype()) {
		case "integer":
			createIntegerPhenotype(formData);
			break;
		case "double":
			createDoublePhenotype(formData);
			break;
		case "string":
			createStringPhenotype(formData);
			break;
		case "expression":
			if (StringUtils.isBlank(formData.getFormula())) {
				view.addMessage("danger", "Phenotype with Datatype 'Boolean Expression' requires an expression.");
				return Response.ok(view).build();
			}
			createBooleanPhenotype(formData);
			break;
		case "formula":
			if (StringUtils.isBlank(formData.getFormula())) {
				view.addMessage("danger", "Phenotype with Datatype 'Formula' requires a formula.");
				return Response.ok(view).build();
			}
			createCalculatedPhenotype(formData);
			break;
		default:
			view.addMessage("danger", "Could not determine Datatype.");
			return Response.ok(view).build();
		}
		
		manager.write();
		view.addMessage("success", "Phenotype '" + formData.getId() + "' created.");
		return Response.ok(view).build();
	}
	
	@POST
	@Path("/reason")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response classifyIndividuals(List<Individual> individuals) {
		for (Individual individual : individuals) {
			// TODO: implement reasoning in COP.owl
			individual.setClassification(new ArrayList<String>());
		}
		return Response.ok(individuals).build();
	}
	
	private AbstractSinglePhenotype createIntegerPhenotype(Phenotype formData) {
		AbstractSinglePhenotype phenotype = new AbstractSinglePhenotype(formData.getId(), OWL2Datatype.XSD_INTEGER);
		
		setPhenotypeLabels(phenotype, formData.getLabels(), formData.getLabelLanguages());
		setPhenotypeDefinitions(phenotype, formData.getDefinitions(), formData.getDefinitionLanguages());
		setPhenotypeCategories(phenotype, formData);
		setPhenotypeRelations(phenotype, formData.getRelations());
		if (StringUtils.isNoneBlank(formData.getUcum())) phenotype.setUnit(formData.getUcum());
		
		/*** superphenotype ***/
		if (formData.getHasSuperPhenotype() && StringUtils.isNoneBlank(formData.getSuperPhenotype())) {
			// TODO
		}
		
		manager.addAbstractSinglePhenotype(phenotype);
		
		
		
		/*** add restricted phenotypes by range ***/
		for (int i = 0; i < Math.max(formData.getRangeMins().size(), formData.getRangeMaxs().size()); i++) {
			String min = getElementAt(formData.getRangeMins(), i);
			String max = getElementAt(formData.getRangeMaxs(), i);
			String minOperator = getElementAt(formData.getRangeMinOperators(), i);
			String maxOperator = getElementAt(formData.getRangeMaxOperators(), i);
			String label = formData.getRangeLabels().get(i);
			
			if (StringUtils.isBlank(min) && StringUtils.isBlank(max)
				|| StringUtils.isBlank(minOperator) && StringUtils.isBlank(maxOperator)
			) continue;
			
			RestrictedSinglePhenotype restrictedPhenotype = StringUtils.isBlank(label)
				? new RestrictedSinglePhenotype(phenotype.getName())
				: new RestrictedSinglePhenotype(label, phenotype.getName());
			restrictedPhenotype.setDatatype(OWL2Datatype.XSD_INTEGER);
			
			setRestrictedPhenotypeRange(restrictedPhenotype, min, max, minOperator, maxOperator);
			
			manager.addRestrictedSinglePhenotype(restrictedPhenotype);
		}
		
		/*** TODO: add restricted phenotype by enum ***/
		
		/*** TODO: add restricted phenotype by boolean ***/
		
		return phenotype;
	}
	
	private AbstractSinglePhenotype createDoublePhenotype(Phenotype formData) {
		AbstractSinglePhenotype phenotype = new AbstractSinglePhenotype(formData.getId(), OWL2Datatype.XSD_DOUBLE);
		
		setPhenotypeLabels(phenotype, formData.getLabels(), formData.getLabelLanguages());
		setPhenotypeDefinitions(phenotype, formData.getDefinitions(), formData.getDefinitionLanguages());
		setPhenotypeRelations(phenotype, formData.getRelations());
		if (StringUtils.isNoneBlank(formData.getUcum())) phenotype.setUnit(formData.getUcum());
		
		// TODO
		manager.addAbstractSinglePhenotype(phenotype);
		return phenotype;
	}
	
	private AbstractSinglePhenotype createStringPhenotype(Phenotype formData) {
		AbstractSinglePhenotype phenotype = new AbstractSinglePhenotype(formData.getId(), OWL2Datatype.XSD_STRING);
		// TODO
		
		/*** add restricted phenotypes by enum ***/
		for (int i = 0; i < formData.getEnumValues().size(); i++) {
			RestrictedSinglePhenotype restrictedPhenotype = new RestrictedSinglePhenotype(formData.getEnumLabels().get(i), phenotype.getName());
			restrictedPhenotype.setValue(formData.getEnumValues().get(i));
			manager.addRestrictedSinglePhenotype(restrictedPhenotype);
		}
		
		manager.addAbstractSinglePhenotype(phenotype);
		return phenotype;
	}
	
	private AbstractCalculationPhenotype createCalculatedPhenotype(Phenotype formData) {
		AbstractCalculationPhenotype phenotype = new AbstractCalculationPhenotype(formData.getId(), formData.getFormula());
		// TODO
		manager.addAbstractCalculationPhenotype(phenotype);
		return phenotype;
	}
	
	private AbstractBooleanPhenotype createBooleanPhenotype(Phenotype formData) {
		AbstractBooleanPhenotype phenotype = new AbstractBooleanPhenotype(formData.getId());
		// TODO
		manager.addAbstractBooleanPhenotype(phenotype);
		return phenotype;
	}
	
	private void setPhenotypeLabels(AbstractPhenotype phenotype, List<String> labels, List<String> languages) {
		for (int i = 0; i < labels.size(); i++) {
			String label = labels.get(i);
			if (StringUtils.isBlank(label)) continue;
			String language = languages.get(i);
			if (StringUtils.isNoneBlank(language)) phenotype.addLabel(label, language);
			else phenotype.addLabel(label);
		}
	}
	
	private void setPhenotypeDefinitions(AbstractPhenotype phenotype, List<String> definitions, List<String> languages) {
		for (int i = 0; i < definitions.size(); i++) {
			String definition = definitions.get(i);
			if (StringUtils.isBlank(definition)) continue;
			String language = languages.get(i);
			if (StringUtils.isNoneBlank(definition)) phenotype.addDefinition(definition, language);
			else phenotype.addLabel(definition);
		}
	}
	
	private void setPhenotypeCategories(AbstractPhenotype phenotype, Phenotype formData) { // TODO: dont use formData
		String category    = formData.getCategory();
		String newCategory = formData.getNewCategory(); 
		if (StringUtils.isNoneBlank(category)) {
			if (!category.equals("new_category"))
				phenotype.setPhenotypeCategories(category);
			else if (StringUtils.isNoneBlank(newCategory)) {
				manager.addPhenotypeCategory(newCategory);
				phenotype.setPhenotypeCategories(newCategory);
			}
		}
	}
	
	private void setPhenotypeRelations(AbstractPhenotype phenotype, List<String> relations) {
		for (String relation : relations) {
			if (StringUtils.isNoneBlank(relation))
				phenotype.addRelatedConcept(relation);
		}
	}
	
	private void setRestrictedPhenotypeRange(RestrictedPhenotype phenotype, String min, String max, String minOperator, String maxOperator) {
		List<OWLFacet> facets = new ArrayList<OWLFacet>();
		
		if (phenotype.getDatatype().equals(OWL2Datatype.XSD_INTEGER)) {
			List<Integer> values = new ArrayList<Integer>();
			
			if (StringUtils.isNoneBlank(min) && StringUtils.isNoneBlank(minOperator)) {
				facets.add(OWLFacet.getFacetBySymbolicName(minOperator));
				values.add(Integer.valueOf(min));
			}
			if (StringUtils.isNoneBlank(max) && StringUtils.isNoneBlank(maxOperator)) {
				facets.add(OWLFacet.getFacetBySymbolicName(maxOperator));
				values.add(Integer.valueOf(max));
			}
			((RestrictedSinglePhenotype) phenotype).setRange(facets.toArray(new OWLFacet[facets.size()]), values.toArray(new Integer[values.size()]));
		} else if (phenotype.getDatatype().equals(OWL2Datatype.XSD_DOUBLE)) {
			List<Double> values = new ArrayList<Double>();
			
			if (StringUtils.isNoneBlank(min) && StringUtils.isNoneBlank(minOperator)) {
				facets.add(OWLFacet.getFacetBySymbolicName(minOperator));
				values.add(Double.valueOf(min));
			}
			if (StringUtils.isNoneBlank(max) && StringUtils.isNoneBlank(maxOperator)) {
				facets.add(OWLFacet.getFacetBySymbolicName(maxOperator));
				values.add(Double.valueOf(max));
			}
			((RestrictedSinglePhenotype) phenotype).setRange(facets.toArray(new OWLFacet[facets.size()]), values.toArray(new Double[values.size()]));
		} else throw new WebApplicationException("Invalid phenotype type supplied to setRestrictedRange() method.");
	}
	
	private <T> T getElementAt(List<T> list, int index) {
		return list.size() > index ? list.get(index) : null;
	}
}
