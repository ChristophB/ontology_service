package de.onto_med.ontology_service.resources;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.onto_med.ontology_service.data_models.Phenotype;
import de.onto_med.ontology_service.data_models.PhenotypeFormData;
import de.onto_med.ontology_service.views.FormView;
import de.onto_med.ontology_service.views.PhenotypeFormView;
import de.onto_med.ontology_service.views.RestApiView;

@Path("/phenotype")
public class PhenotypeResource extends Resource {
	
	public PhenotypeResource(String rootPath) {
		super(rootPath);
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
		return Response.ok(new ArrayList<String>() {{ add("Some descriptions for " + iri); }}).build();
	}
	
	@GET
	@Path("/all")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	@SuppressWarnings("serial")
	public Response getPhenotypeTaxonomy(@Context HttpHeaders headers) {
		List<TaxonomyNode> taxonomyNodes = new ArrayList<TaxonomyNode>() {{
			add(new TaxonomyNode("Category_1", new Attributes("http://example.org/example#Category_1", "category"), new ArrayList<TaxonomyNode>() {{
				add(new TaxonomyNode("Integer_Phenotype", new Attributes("http://example.org/example#Integer_Phenotype", "integer"), new ArrayList<TaxonomyNode>() {{
					add(new TaxonomyNode("String_Phenotype", new Attributes("http://example.org/example#String_Phenotype_1", "string")));
				}}));
				add(new TaxonomyNode("String_Phenotype", new Attributes("http://example.org/example#String_phenotype_1", "string")));
			}}));
			add(new TaxonomyNode("Category_2", new Attributes("http://example.org/example#Category_2", "category"), new ArrayList<TaxonomyNode>() {{
				add(new TaxonomyNode("Numeric_Phenotype", new Attributes("http://example.org/example#Numeric_Phenotype", "formula")));
				add(new TaxonomyNode("Boolean_Phenotype", new Attributes("http://example.org/example#Boolean_Phenotype", "expression")));
			}}));
			add(new TaxonomyNode("Double_Phenotype", new Attributes("http://example.org/example#Double_Phenotype", "double")));
		}};
		
		if (acceptsMediaType(headers, MediaType.APPLICATION_JSON_TYPE)) {
			return Response.ok(taxonomyNodes).build();
		} else {
			return Response.ok(new RestApiView("AllPhenotypes.ftl", rootPath)).build();
		}
	}
	
	@GET
	@Path("/simplephenotype_form")
	@Produces(MediaType.TEXT_HTML)
	public Response getSimplePhenotypeForm() {
		PhenotypeFormView view = new PhenotypeFormView("SimplePhenotypeForm.ftl", rootPath);
		return Response.ok(view).build();
	}
	
	@GET
	@Path("/compositphenotype_form")
	@Produces(MediaType.TEXT_HTML)
	public Response getCompositPhenotypeForm(@BeanParam Phenotype phenotype) {
		return Response.ok(new PhenotypeFormView("CompositPhenotypeForm.ftl", rootPath, phenotype)).build();
	}
	
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createPhenotype(@BeanParam PhenotypeFormData phenotype) {
		String request
			= "ID: " + phenotype.getId() + "\n"
			+ String.format("Labels: %s (%s)\n", phenotype.getLabels(), phenotype.getLabelLanguages())
			+ String.format("Has Super Phenotype: %s => '%s'\n", phenotype.getHasSuperPhenotype(), phenotype.getSuperPhenotype())
			+ "Category: " + phenotype.getCategory() + "\n"
			+ "New-Category: " + phenotype.getNewCategory() + "\n"
			+ String.format("Defintions: %s (%s)\n", phenotype.getDefinitions(), phenotype.getDefinitionLanguages())
			+ "Datatype: " + phenotype.getDatatype() + "\n"
			+ "Relations: " + phenotype.getRelations(); 
		
		return Response.ok(request).build();
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
		public String iri;
		
		public Attributes(String iri, String type) {
			this.iri  = iri;
			this.type = type;
		}
	}
}
