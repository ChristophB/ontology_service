package de.onto_med.ontology_service.data_models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.lha.phenoman.model.phenotype.AbstractSinglePhenotype;
import org.lha.phenoman.model.phenotype.RestrictedBooleanPhenotype;
import org.lha.phenoman.model.phenotype.top_level.Category;
import org.lha.phenoman.model.phenotype.top_level.TextLang;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import javax.ws.rs.FormParam;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Phenotype {
	@FormParam("id") @JsonProperty("id")
	private String id;
	@FormParam("label[]") @JsonProperty("label[]")
	private List<String> labels;
	@FormParam("label-language[]") @JsonProperty("label-language[]")
	private List<String> labelLanguages;
	@FormParam("super-phenotype") @JsonProperty("super-phenotype")
	private String superPhenotype;
	@FormParam("categories") @JsonProperty("categories")
	private String categories;
	@FormParam("super-category") @JsonProperty("super-category")
	private String superCategory;
	@FormParam("definition[]") @JsonProperty("definition[]")
	private List<String> definitions;
	@FormParam("definition-language[]") @JsonProperty("definition-language[]")
	private List<String> definitionLanguages;
	@FormParam("datatype") @JsonProperty("datatype")
	private String datatype;
	@FormParam("ucum") @JsonProperty("ucum")
	private String ucum;
	@FormParam("range-min") @JsonProperty("range-min")
	private String rangeMin;
	@FormParam("range-min-operator") @JsonProperty("range-min-operator")
	private String rangeMinOperator;
	@FormParam("range-max") @JsonProperty("range-max")
	private String rangeMax;
	@FormParam("range-max-operator") @JsonProperty("range-max-operator")
	private String rangeMaxOperator;
	@FormParam("enum-value[]") @JsonProperty("enum-value[]")
	private List<String> enumValues;
	@FormParam("formula") @JsonProperty("formula")
	private String formula;
	@FormParam("expression") @JsonProperty("expression")
	private String expression;
	@FormParam("boolean-true-label") @JsonProperty("boolean-true-label")
	private String booleanTrueLabel;
	@FormParam("boolean-false-label") @JsonProperty("boolean-false-label")
	private String booleanFalseLabel;
	@FormParam("relation[]") @JsonProperty("relation[]")
	private List<String> relations;
	@FormParam("is-decimal") @JsonProperty("is-decimal")
	private boolean isDecimal;
	@FormParam("score") @JsonProperty("score")
	private Double score;


	public Phenotype() { super(); }

	public Phenotype(Category attributes) {
		this();
		if (attributes == null) return;

		setId(attributes.getName());
		setRelations(attributes.getRelatedConcepts());

		if (attributes.isAbstractSinglePhenotype()) {
			AbstractSinglePhenotype phenotype = attributes.asAbstractSinglePhenotype();
			setDatatype(owl2DatatypeToString(phenotype.getDatatype()));
			setUcum(attributes.asAbstractSinglePhenotype().getUnit());
		} else if (attributes.isAbstractBooleanPhenotype() || attributes.isRestrictedBooleanPhenotype()) {
			setDatatype("boolean");
			if (attributes.isRestrictedBooleanPhenotype()) {
				RestrictedBooleanPhenotype phenotype = attributes.asRestrictedBooleanPhenotype();
				setExpression(phenotype.getManchesterSyntaxExpression());
				setScore(phenotype.getScore());
			}
		} else if (attributes.isAbstractCalculationPhenotype() || attributes.isRestrictedCalculationPhenotype()) {
			setDatatype("formula");
			if (attributes.isAbstractCalculationPhenotype()) {
				setFormula(attributes.asAbstractCalculationPhenotype().getFormula());
				setUcum(attributes.asAbstractCalculationPhenotype().getUnit());
				// TODO: set ranges and operators
			}
		}

		for (TextLang textLang : attributes.getDefinitions()) {
			addDefinition(textLang.getText());
			addDefinitionLanguage(textLang.getLang());
		}
		for (TextLang textLang : attributes.getLabels()) {
			addLabel(textLang.getText());
			addLabelLanguage(textLang.getText());
		}
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getSuperCategory() {
		return superCategory;
	}

	public void setSuperCategory(String superCategory) {
		this.superCategory = superCategory;
	}

	public boolean getIsDecimal() {
		return isDecimal;
	}

	public void setIsDecimal(boolean isDecimal) {
		this.isDecimal = isDecimal;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> label) {
		this.labels = label;
	}

	public List<String> getLabelLanguages() {
		return labelLanguages;
	}

	public void setLabelLanguages(List<String> labelLanguage) {
		this.labelLanguages = labelLanguage;
	}

	public String getSuperPhenotype() {
		return superPhenotype;
	}

	public void setSuperPhenotype(String superPhenotype) {
		this.superPhenotype = superPhenotype;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public List<String> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<String> definition) {
		this.definitions = definition;
	}

	public List<String> getDefinitionLanguages() {
		return definitionLanguages;
	}

	public void setDefinitionLanguages(List<String> definitionLanguage) {
		this.definitionLanguages = definitionLanguage;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getUcum() {
		return ucum;
	}

	public void setUcum(String ucum) {
		this.ucum = ucum;
	}

	public String getRangeMin() {
		return rangeMin;
	}

	public void setRangeMin(String rangeMin) {
		this.rangeMin = rangeMin;
	}

	public String getRangeMinOperator() {
		return rangeMinOperator;
	}

	public void setRangeMinOperator(String rangeMinOperator) {
		this.rangeMinOperator = rangeMinOperator;
	}

	public String getRangeMax() {
		return rangeMax;
	}

	public void setRangeMax(String rangeMax) {
		this.rangeMax = rangeMax;
	}

	public String getRangeMaxOperator() {
		return rangeMaxOperator;
	}

	public void setRangeMaxOperator(String rangeMaxOperator) {
		this.rangeMaxOperator = rangeMaxOperator;
	}

	public List<String> getEnumValues() {
		return enumValues;
	}

	public void setEnumValues(List<String> enumValues) {
		this.enumValues = enumValues;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getBooleanTrueLabel() {
		return booleanTrueLabel;
	}

	public void setBooleanTrueLabel(String booleanTrueLabel) {
		this.booleanTrueLabel = booleanTrueLabel;
	}

	public String getBooleanFalseLabel() {
		return booleanFalseLabel;
	}

	public void setBooleanFalseLabel(String boolenFalseLabel) {
		this.booleanFalseLabel = boolenFalseLabel;
	}

	public List<String> getRelations() {
		return relations;
	}

	public void setRelations(List<String> relations) {
		this.relations = relations;
	}

	public void setRelations(Set<String> relations) {
		this.relations = new ArrayList<>();
		this.relations.addAll(relations);
	}

	public void addDefinition(String definition) {
		List<String> definitions = getDefinitions() != null ? getDefinitions() : new ArrayList<>();
		definitions.add(definition);
	}

	public void addDefinitionLanguage(String definitionLanguage) {
		List<String> definitionLanguages = getDefinitionLanguages() != null ? getDefinitionLanguages() : new ArrayList<>();
		definitionLanguages.add(definitionLanguage);
	}

	public void addLabel(String label) {
		List<String> labels = getLabels() != null ? getLabels() : new ArrayList<>();
		labels.add(label);
	}

	public void addLabelLanguage(String labelLanguage) {
		List<String> labelLanguages = getLabelLanguages() != null ? getLabelLanguages() : new ArrayList<>();
		labelLanguages.add(labelLanguage);
	}

	private String owl2DatatypeToString(OWL2Datatype datatype) {
		if (OWL2Datatype.XSD_INTEGER.equals(datatype)|| OWL2Datatype.XSD_DOUBLE.equals(datatype))
			return "numeric";
		else if (OWL2Datatype.XSD_DATE_TIME.equals(datatype))
			return "date";
		else if (OWL2Datatype.XSD_STRING.equals(datatype))
			return "string";

		return null;
	}

}
