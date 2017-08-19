package de.onto_med.ontology_service.resources;

import com.sun.javaws.exceptions.MissingFieldException;
import de.onto_med.ontology_service.data_models.Individual;
import de.onto_med.ontology_service.data_models.Phenotype;
import de.onto_med.ontology_service.manager.PhenotypeManager;
import de.onto_med.ontology_service.views.PhenotypeFormView;
import de.onto_med.ontology_service.views.RestApiView;
import org.apache.commons.lang3.StringUtils;
import org.lha.phenoman.man.PhenotypeOntologyManager;
import org.lha.phenoman.model.category_tree.PhenotypeCategoryTreeNode;
import org.lha.phenoman.model.phenotype.*;
import org.lha.phenoman.model.phenotype.top_level.AbstractPhenotype;
import org.lha.phenoman.model.phenotype.top_level.Category;
import org.lha.phenoman.model.phenotype.top_level.PhenotypeRange;
import org.lha.phenoman.model.phenotype.top_level.RestrictedPhenotype;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;

import javax.activation.UnsupportedDataTypeException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Path("/phenotype")
public class PhenotypeResource extends Resource {
	private PhenotypeManager manager;
	
	public PhenotypeResource(String rootPath, String phenotypePath) {
		super(rootPath);
		manager = new PhenotypeManager(phenotypePath);
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
		return Response.ok(manager.getPhenotype(iri)).build();
	}

	@GET
	@Path("/categories")
	@Produces(MediaType.APPLICATION_JSON)
	public PhenotypeCategoryTreeNode getCategories() {
		return manager.getTaxonomy(false);
	}

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public PhenotypeCategoryTreeNode getPhenotypes() {
		return manager.getTaxonomy(true);
	}
	
	@GET
	@Path("/decision-tree")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getDecisionTree(@QueryParam("phenotype") String phenotype, @QueryParam("language") String language) {
		if (StringUtils.isBlank(phenotype)) throw new WebApplicationException("Query parameter 'phenotype' missing.");
		
		// TODO: generate GraphML representation of the phenotypes decision tree
		// String labelLanguage = StringUtils.defaultString(language, "en");
		
		return Response.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename='" + phenotype + "_decisiontree.GraphML'").build();
	}
	
	@GET
	@Path("/decision-tree-form")
	@Produces(MediaType.TEXT_HTML)
	public Response getDecisionTreeForm() {
		return Response.ok(new RestApiView("DecisionTreeForm.ftl", rootPath)).build();
	}

	@GET
	@Path("/phenotype-form")
	@Produces(MediaType.TEXT_HTML)
	public Response getPhenotypeForm(@QueryParam("type") String type, @QueryParam("id") String id) {
		return Response.ok(new PhenotypeFormView(rootPath, new Phenotype(manager.getPhenotype(id)))).build();
	}

	@POST
	@Path("/create-category")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response createCategory(@BeanParam Phenotype formData) {
		PhenotypeFormView view = new PhenotypeFormView(rootPath, formData);

		try {
			Category category = manager.createCategory(formData);
			view.addMessage("success", "Category '" + category.getName() + "' created.");
		} catch (MissingFieldException e) {
			view.addMessage("danger", e.getMessage());
		}

		return Response.ok(view).build();
	}

	@POST
	@Path("/create-abstract-phenotype")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response createAbstractPhenotype(@BeanParam Phenotype formData) {
		PhenotypeFormView view = new PhenotypeFormView(rootPath, formData);

		try {
			AbstractPhenotype phenotype = manager.createAbstractPhenotype(formData);
			view.addMessage("success", "Abstract phenotype '" + phenotype.getName() + "' created.");
		} catch (MissingFieldException | UnsupportedDataTypeException e) {
			view.addMessage("danger", e.getMessage());
		}

		return Response.ok(view).build();
	}

	@POST
	@Path("/create-restricted-phenotype")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response createRestrictedPhenotype(@Context HttpHeaders headers, @BeanParam Phenotype formData) {
		RestApiView view = new PhenotypeFormView(rootPath);

		try {
			RestrictedPhenotype phenotype = manager.createRestrictedPhenotype(formData);
			view.addMessage("success", "Phenotype '" + phenotype.getName() + "' created.");
		} catch (MissingFieldException | UnsupportedDataTypeException e) {
			view.addMessage("danger", e.getMessage());
		}

		return Response.ok(view).build();
	}

	@POST
	@Path("/reason")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response classifyIndividuals(List<Individual> individuals) {
		for (Individual individual : individuals) {
			// TODO: implement reasoning in COP.owl
			individual.setClassification(new ArrayList<>());
		}
		return Response.ok(individuals).build();
	}

}
