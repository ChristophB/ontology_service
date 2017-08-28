package de.onto_med.ontology_service.resources;

import de.onto_med.ontology_service.data_models.Phenotype;
import de.onto_med.ontology_service.data_models.Property;
import de.onto_med.ontology_service.manager.PhenotypeManager;
import de.onto_med.ontology_service.views.RestApiView;
import org.apache.commons.lang3.StringUtils;
import org.lha.phenoman.model.phenotype.top_level.AbstractPhenotype;
import org.lha.phenoman.model.phenotype.top_level.Category;
import org.lha.phenoman.model.phenotype.top_level.RestrictedPhenotype;

import javax.activation.UnsupportedDataTypeException;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
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
	public Response getDecisionTree(@QueryParam("phenotype") String phenotype, @QueryParam("format") String format) {
		if (StringUtils.isBlank(phenotype)) throw new WebApplicationException("Query parameter 'phenotype' missing.");

		try {
			return Response.ok(manager.getPhenotypeDecisionTree(phenotype, format), MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename='" + phenotype + "_decisiontree." + format + "'")
				.build();
		} catch (IllegalArgumentException | IOException e) {
			throw new WebApplicationException(e.getMessage());
		}
	}

	@GET
	@Path("/phenotype-form")
	@Produces(MediaType.TEXT_HTML)
	public Response getPhenotypeForm(@QueryParam("type") String type, @QueryParam("id") String id) {
		return Response.ok(new RestApiView("PhenotypeForm.ftl", rootPath)).build();
	}

	@POST
	@Path("/create-category")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createCategory(Phenotype formData) {
		try {
			Category category = manager.createCategory(formData);
			return Response.ok("Category '" + category.getName() + "' created.").build();
		} catch (NullPointerException e) {
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
		} catch (NullPointerException | UnsupportedDataTypeException e) {
			throw new WebApplicationException(e.getMessage());
		}
	}

	@POST
	@Path("/create-restricted-phenotype")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createRestrictedPhenotype(Phenotype formData) {
		try {
			RestrictedPhenotype phenotype = manager.createRestrictedPhenotype(formData);
			return Response.ok("Phenotype '" + phenotype.getName() + "' created.").build();
		} catch (NullPointerException | UnsupportedDataTypeException e) {
			throw new WebApplicationException(e.getMessage());
		}
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
	public Response classifyIndividual(List<Property> properties) {
		if (properties == null || properties.isEmpty())
			throw new WebApplicationException("No properties were provided.");

		return Response.ok(manager.classifyIndividual(properties)).build();
	}
}
