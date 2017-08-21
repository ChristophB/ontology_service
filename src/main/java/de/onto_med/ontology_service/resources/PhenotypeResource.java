package de.onto_med.ontology_service.resources;

import com.sun.javaws.exceptions.MissingFieldException;
import de.onto_med.ontology_service.data_models.Individual;
import de.onto_med.ontology_service.data_models.Phenotype;
import de.onto_med.ontology_service.manager.PhenotypeManager;
import de.onto_med.ontology_service.views.PhenotypeFormView;
import de.onto_med.ontology_service.views.RestApiView;
import org.apache.commons.lang3.StringUtils;
import org.lha.phenoman.model.phenotype.top_level.AbstractPhenotype;
import org.lha.phenoman.model.phenotype.top_level.Category;
import org.lha.phenoman.model.phenotype.top_level.RestrictedPhenotype;

import javax.activation.UnsupportedDataTypeException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

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
	public Category getPhenotype(@PathParam("iri") String iri) {
		System.out.println(manager.getPhenotype(iri));
		return manager.getPhenotype(iri);
	}

	@GET
	@Path("/categories")
	@Produces(MediaType.APPLICATION_JSON)
	public PhenotypeManager.TreeNode getCategories() {
		return manager.getTaxonomy(false);
	}

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public PhenotypeManager.TreeNode getPhenotypes() {
		return manager.getTaxonomy(true);
	}
	
	@GET
	@Path("/decision-tree")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getDecisionTree(@QueryParam("phenotype") String phenotype, @QueryParam("language") String language) {
		if (StringUtils.isBlank(phenotype)) throw new WebApplicationException("Query parameter 'phenotype' missing.");
		
		return Response.ok(manager.getPhenotypeDecisionTree(phenotype, StringUtils.defaultString(language, "en")))
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename='" + phenotype + "_decisiontree.GraphML'")
			.build();
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
		return Response.ok(new PhenotypeFormView(rootPath, null)).build(); // new Phenotype(manager.getPhenotype(id))
	}

	@POST
	@Path("/create-category")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createCategory(Phenotype formData) {
		try {
			PhenotypeManager.ExtendedCategory category = manager.createCategory(formData);
			return Response.ok("Category '" + category.getName() + "' created.").build();
		} catch (MissingFieldException e) {
			throw new WebApplicationException(e.getMessage());
		}
	}

	@POST
	@Path("/create-abstract-phenotype")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAbstractPhenotype(Phenotype formData) {
		try {
			AbstractPhenotype phenotype = manager.createAbstractPhenotype(formData);
			return Response.ok("Abstract phenotype '" + phenotype.getName() + "' created.").build();
		} catch (MissingFieldException e) {
			throw new WebApplicationException(e.getLaunchDescSource());
		} catch (UnsupportedDataTypeException e) {
			throw new WebApplicationException(e.getMessage());
		}
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
		} catch (MissingFieldException e) {
			view.addMessage("danger", e.getLaunchDescSource());
		} catch (UnsupportedDataTypeException e) {
			view.addMessage("danger", e.getMessage());
		}

		return Response.ok(view).build();
	}

	@GET
	@Path("/reason-form")
	@Produces({ MediaType.TEXT_HTML })
	public Response getReasonForm() {
		return Response.ok(new RestApiView("PhenotypeReasonForm.ftl", rootPath)).build();
	}

	@POST
	@Path("/reason")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response classifyIndividuals(List<Individual> individuals) {
		if (individuals == null || individuals.isEmpty())
			return Response.ok("No individuals were provided.").build();

		for (Individual individual : individuals) {
			try { individual.setClassification(manager.classifyIndividual(individual)); }
			catch (IllegalArgumentException e) {
				Response.ok(e.getMessage()).build();
			}
		}
		return Response.ok(individuals).build();
	}

}
