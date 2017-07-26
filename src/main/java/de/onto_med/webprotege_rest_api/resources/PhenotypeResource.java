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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.onto_med.webprotege_rest_api.views.PhenotypeFormView;

@Path("/phenotype")
public class PhenotypeResource extends Resource {
	
	public PhenotypeResource(String rootPath) {
		super(rootPath);
	}
	
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("serial")
	public Response getPhenotypeTaxonomy() {
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
		
		return Response.ok(taxonomyNodes).build();
	}
	
	@GET
	@Path("/form")
	@Produces(MediaType.TEXT_HTML)
	public Response getPhenotypeForm() {
		return Response.ok(new PhenotypeFormView(rootPath)).build();
	}
	
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createPhenotype(
		@NotNull @FormParam("label-en") String label_en,
		@FormParam("label-de") String label_de,
		@FormParam("alias-en[]") List<String> aliases_en,
		@FormParam("alias-de[]") List<String> aliases_de,
		@FormParam("has-super-phenotype") Boolean hasSuperPhenotype,
		@FormParam("super-phenotype") String superPhenotype,
		@FormParam("category") String category,
		@FormParam("new-category") String newCategory,
		@FormParam("definition-en") String definition_en,
		@FormParam("definition-de") String definition_de,
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
			= formatMultiLangFields("Label", label_en, label_de)
			+ formatMultiLangFields("Defintion", definition_en, definition_de)
			+ formatMultiLangFields("Aliases", aliases_en, aliases_de)
			+ String.format("Has Super Phenotype: %s => '%s'\n", hasSuperPhenotype, superPhenotype)
			+ "Category: " + category + "\n"
			+ "New-Category: " + newCategory + "\n"
			+ formatMultiLangFields("Definition", definition_en, definition_de)
			+ "Datatype: " + datatype + "\n"
			+ "Relations: " + relations; 
		
		return Response.ok(request).build();
	}
	
	private String formatMultiLangFields(String label, Object v1, Object v2) {
		return String.format("%s: '%s' (en), '%s' (de)\n", label, v1, v2);
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
