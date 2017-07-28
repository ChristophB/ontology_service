package de.onto_med.webprotege_rest_api.resources;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.onto_med.webprotege_rest_api.views.FormView;
import de.onto_med.webprotege_rest_api.views.PhenotypeView;
import de.onto_med.webprotege_rest_api.views.RestApiView;

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
	@Path("/all")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	@SuppressWarnings("serial")
	public Response getPhenotypeTaxonomy(@Context HttpHeaders headers) {
		List<TaxonomyNode> taxonomyNodes = new ArrayList<TaxonomyNode>() {{
			add(new TaxonomyNode("Category_1", new Attributes("category"), new ArrayList<TaxonomyNode>() {{
				add(new TaxonomyNode("Integer_Phenotype", new Attributes("integer")));
				add(new TaxonomyNode("String_Phenotype", new Attributes("string")));
			}}));
			add(new TaxonomyNode("Category_2", new Attributes("category"), new ArrayList<TaxonomyNode>() {{
				add(new TaxonomyNode("Numeric_Phenotype", new Attributes("formula")));
				add(new TaxonomyNode("Boolean_Phenotype", new Attributes("expression")));
			}}));
			add(new TaxonomyNode("Double_Phenotype", new Attributes("double")));
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
		return Response.ok(new FormView("SimplePhenotypeForm.ftl", rootPath)).build();
	}
	
	@GET
	@Path("/compositphenotype_form")
	@Produces(MediaType.TEXT_HTML)
	public Response getCompositPhenotypeForm() {
		return Response.ok(new FormView("CompositPhenotypeForm.ftl", rootPath)).build();
	}
	
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createPhenotype(
		@NotNull @FormParam("id") String id,
		@FormParam("label[]") List<String> label,
		@FormParam("label-language[]") List<String> labelLanguage,
		@FormParam("has-super-phenotype") Boolean hasSuperPhenotype,
		@FormParam("super-phenotype") String superPhenotype,
		@FormParam("category") String category,
		@FormParam("new-category") String newCategory,
		@FormParam("definition[]") List<String> definition,
		@FormParam("definition-language[]") List<String> definitionLanguage,
		@NotNull @FormParam("datatype") String datatype,
		@FormParam("ucum") String ucum,
		@FormParam("range-min[]") List<String> rangeMins,
		@FormParam("range-min-operator[]") List<String> rangeMinOperators,
		@FormParam("range-max[]") List<String> rangeMaxOperators,
		@FormParam("range-max-operator[]") List<String> rangeMAxOperators,
		@FormParam("enum-value[]") List<String> enumValues,
		@FormParam("enum-label[]") List<String> enumLabels,
		@FormParam("formula") String formula,
		@FormParam("expression") String expression,
		@FormParam("boolean-true-label") String booleanTrueLabel,
		@FormParam("boolean-false-label") String boolenFalseLabel,
		@FormParam("relation[]") List<String> relations
	) {
		String request
			= "ID: " + id + "\n"
			+ String.format("Labels: %s (%s)\n", label, labelLanguage)
			+ String.format("Has Super Phenotype: %s => '%s'\n", hasSuperPhenotype, superPhenotype)
			+ "Category: " + category + "\n"
			+ "New-Category: " + newCategory + "\n"
			+ String.format("Defintions: %s (%s)\n", definition, definitionLanguage)
			+ "Datatype: " + datatype + "\n"
			+ "Relations: " + relations; 
		
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
		
		public Attributes(String type) { this.type = type; }
	}
}
