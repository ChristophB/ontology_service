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
import de.onto_med.ontology_service.data_models.PhenotypeFormData;
import de.onto_med.ontology_service.views.PhenotypeFormView;
import de.onto_med.ontology_service.views.RestApiView;

import org.lha.phenoman.man.PhenotypeOntologyManager;
import org.lha.phenoman.model.AbstractBooleanPhenotype;
import org.lha.phenoman.model.AbstractCalculationPhenotype;
import org.lha.phenoman.model.AbstractSinglePhenotype;
import org.lha.phenoman.model.top_level.AbstractPhenotype;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

@Path("/phenotype")
public class PhenotypeResource extends Resource {
	final static Logger logger = LoggerFactory.getLogger(PhenotypeResource.class);
	
	private String phenotypePath;
	
	public PhenotypeResource(String rootPath, String phenotypePath) {
		super(rootPath);
		this.phenotypePath = phenotypePath;
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getPhenotypeView() {
		return Response.ok(new RestApiView("PhenotypeView.ftl", rootPath)).build();
	}
	
	@GET
	@Path("/{iri}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("serial")
	public Response getPhenotype(@PathParam("iri") String iri) {
		// TODO: implement retrival of phenotype data for given iri (?)
		
		return Response.ok(new ArrayList<String>() {{ add("Some descriptions for " + iri); }}).build();
	}
	
	@GET
	@Path("/all")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	@SuppressWarnings("serial")
	public Response getPhenotypeTaxonomy(@Context HttpHeaders headers, @QueryParam("type") String type) {
		switch (StringUtils.defaultString(type, "all")) {
			case "category":
				logger.info("only return categories");
				break;
			case "boolean":
				logger.info("only return boolean expressions, used for decisiontree generation view");
				break;
			default:
				logger.info("use all phenotypes");
		}
		
		// TODO: implement taxonomy extraction from cop.owl
		
		List<TaxonomyNode> taxonomyNodes = new ArrayList<TaxonomyNode>() {{
			add(new TaxonomyNode("Category_1", new Attributes("Category_1", "category"), new ArrayList<TaxonomyNode>() {{
				add(new TaxonomyNode("Integer_Phenotype", new Attributes("Integer_Phenotype", "integer"), new ArrayList<TaxonomyNode>() {{
					add(new TaxonomyNode("String_Phenotype", new Attributes("String_Phenotype_1", "string")));
				}}));
				add(new TaxonomyNode("String_Phenotype", new Attributes("String_phenotype_1", "string")));
			}}));
			add(new TaxonomyNode("Category_2", new Attributes("Category_2", "category"), new ArrayList<TaxonomyNode>() {{
				add(new TaxonomyNode("Numeric_Phenotype", new Attributes("Numeric_Phenotype", "formula")));
				add(new TaxonomyNode("Boolean_Phenotype", new Attributes("Boolean_Phenotype", "expression")));
			}}));
			add(new TaxonomyNode("Double_Phenotype", new Attributes("Double_Phenotype", "double")));
		}};
		
		if (acceptsMediaType(headers, MediaType.APPLICATION_JSON_TYPE)) {
			return Response.ok(taxonomyNodes).build();
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
	@Path("/simplephenotype-form")
	@Produces(MediaType.TEXT_HTML)
	public Response getSimplePhenotypeForm(@BeanParam Phenotype phenotype) {
		return Response.ok(new PhenotypeFormView("SimplePhenotypeForm.ftl", rootPath, phenotype)).build();
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
	public Response createPhenotype(@Context HttpHeaders headers, @BeanParam PhenotypeFormData formData) {
		// TODO: redirect to form after creation succeded or failed and show message
		RestApiView view = new RestApiView("PhenotypeView.ftl", rootPath);
		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(phenotypePath, true);
		
		if (StringUtils.isBlank(formData.getId()) || StringUtils.isBlank(formData.getDatatype())) {
			view.addMessage("danger", "ID and/or Datatype is missing.");
			return Response.ok(view).build();
		}
		
		AbstractPhenotype phenotype;
		switch (formData.getDatatype()) {
		case "integer":
			phenotype = new AbstractSinglePhenotype(formData.getId(), OWL2Datatype.XSD_INTEGER);
			break;
		case "double":
			phenotype = new AbstractSinglePhenotype(formData.getId(), OWL2Datatype.XSD_DOUBLE);
			break;
		case "string":
			phenotype = new AbstractSinglePhenotype(formData.getId(), OWL2Datatype.XSD_STRING);
			break;
		case "expression":
			if (StringUtils.isBlank(formData.getFormula())) {
				view.addMessage("danger", "Phenotype with Datatype 'Boolean Expression' requires an expression.");
				return Response.ok(view).build();
			}
			phenotype = new AbstractBooleanPhenotype(formData.getId());
			break;
		case "formula":
			if (StringUtils.isBlank(formData.getFormula())) {
				view.addMessage("danger", "Phenotype with Datatype 'Formula' requires a formula.");
				return Response.ok(view).build();
			}
			phenotype = new AbstractCalculationPhenotype(formData.getId(), formData.getFormula());
			break;
		default:
			view.addMessage("danger", "Could not determine Datatype.");
			return Response.ok(view).build();
		}
		
		for (int i = 0; i < formData.getLabels().size(); i++) {
			String label = formData.getLabels().get(i);
			if (StringUtils.isBlank(label)) continue;
			String language = formData.getLabelLanguages().get(i);
			if (StringUtils.isNoneBlank(language)) phenotype.addLabel(label, language);
			else phenotype.addLabel(label);
		}
		
		if (formData.getHasSuperPhenotype() && StringUtils.isNoneBlank(formData.getSuperPhenotype())) {
			// not used
		}
		
		String category = formData.getCategory();
		if (StringUtils.isNoneBlank(category)) {
			if (!category.equals("new_category"))
				phenotype.setPhenotypeClasses(category);
			else if (StringUtils.isNoneBlank(formData.getNewCategory()))
				manager.addPhenotypeClass(formData.getNewCategory());
		}

//		for (int i = 0; i < formData.getDefinitions().size(); i++) {
//			String definition = formData.getDefinitions().get(i);
//			if (StringUtils.isBlank(definition)) continue;
//			String language = formData.getDefinitionLanguages().get(i);
//			if (StringUtils.isNoneBlank(definition)) phenotype.addDefinition(definition, language);
//			else phenotype.addLabel(definition);
//		}
		
//		for (Stirng relation : formData.getRelations()) {
//			if (StringUtils.isNoneBlank(relation))
//				phenotype.addRelation(relation);
//		}
		
		
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
	
	class TaxonomyNode {
		public String text;
		public List<TaxonomyNode> children;
		public Attributes a_attr;
		
		public TaxonomyNode(String text, Attributes a_attr) {
			this.text   = text;
			this.a_attr = a_attr;
		}
		
		public TaxonomyNode(String text, Attributes a_attr, List<TaxonomyNode> children) {
			this(text, a_attr);
			this.children = children;
		}
	}
	
	class Attributes {
		public String type;
		public String id;
		
		public Attributes(String id, String type) {
			this.id  = id;
			this.type = type;
		}
	}
}
