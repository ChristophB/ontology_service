package de.onto_med.ontology_service.data_models;

import java.util.List;

import javax.ws.rs.QueryParam;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Phenotype {
	@QueryParam("id") @JsonProperty("id")
	private String id;
	@QueryParam("label[]") @JsonProperty("label[]")
	private List<String> labels;
	@QueryParam("label-language[]") @JsonProperty("label-language[]")
	private List<String> labelLanguages;
	@QueryParam("has-super-phenotype") @JsonProperty("has-super-phenotype")
	private Boolean hasSuperPhenotype = false;
	@QueryParam("super-phenotype") @JsonProperty("super-phenotype")
	private String superPhenotype;
	@QueryParam("category") @JsonProperty("category")
	private String category;
	@QueryParam("new-category") @JsonProperty("new-category")
	private String newCategory;
	@QueryParam("definition[]") @JsonProperty("definition[]")
	private List<String> definitions;
	@QueryParam("definition-language[]") @JsonProperty("definition-language[]")
	private List<String> definitionLanguages;
	@QueryParam("datatype") @JsonProperty("datatype")
	private String datatype;
	@QueryParam("ucum") @JsonProperty("ucum")
	private String ucum;
	@QueryParam("range-min[]") @JsonProperty("range-min[]")
	private List<String> rangeMins;
	@QueryParam("range-min-operator[]") @JsonProperty("range-min-operator[]")
	private List<String> rangeMinOperators;
	@QueryParam("range-max[]") @JsonProperty("range-max[]")
	private List<String> rangeMaxs;
	@QueryParam("range-max-operator[]") @JsonProperty("range-max-operator[]")
	private List<String> rangeMaxOperators;
	@QueryParam("enum-value[]") @JsonProperty("enum-value[]")
	private List<String> enumValues;
	@QueryParam("enum-label[]") @JsonProperty("enum-label[]")
	private List<String> enumLabels;
	@QueryParam("formula") @JsonProperty("formula")
	private String formula;
	@QueryParam("expression") @JsonProperty("expression")
	private String expression;
	@QueryParam("boolean-true-label") @JsonProperty("boolean-true-label")
	private String booleanTrueLabel;
	@QueryParam("boolean-false-label") @JsonProperty("boolean-false-label")
	private String boolenFalseLabel;
	@QueryParam("relation[]") @JsonProperty("relation[]")
	private List<String> relations;

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

	public Boolean getHasSuperPhenotype() {
		return hasSuperPhenotype;
	}

	public void setHasSuperPhenotype(Boolean hasSuperPhenotype) {
		this.hasSuperPhenotype = hasSuperPhenotype;
	}

	public String getSuperPhenotype() {
		return superPhenotype;
	}

	public void setSuperPhenotype(String superPhenotype) {
		this.superPhenotype = superPhenotype;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getNewCategory() {
		return newCategory;
	}

	public void setNewCategory(String newCategory) {
		this.newCategory = newCategory;
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

	public List<String> getRangeMins() {
		return rangeMins;
	}

	public void setRangeMins(List<String> rangeMins) {
		this.rangeMins = rangeMins;
	}

	public List<String> getRangeMinOperators() {
		return rangeMinOperators;
	}

	public void setRangeMinOperators(List<String> rangeMinOperators) {
		this.rangeMinOperators = rangeMinOperators;
	}

	public List<String> getRangeMaxs() {
		return rangeMaxs;
	}

	public void setRangeMaxs(List<String> rangeMaxs) {
		this.rangeMaxs = rangeMaxs;
	}

	public List<String> getRangeMaxOperators() {
		return rangeMaxOperators;
	}

	public void setRangeMaxOperators(List<String> rangeMaxOperators) {
		this.rangeMaxOperators = rangeMaxOperators;
	}

	public List<String> getEnumValues() {
		return enumValues;
	}

	public void setEnumValues(List<String> enumValues) {
		this.enumValues = enumValues;
	}

	public List<String> getEnumLabels() {
		return enumLabels;
	}

	public void setEnumLabels(List<String> enumLabels) {
		this.enumLabels = enumLabels;
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

	public String getBoolenFalseLabel() {
		return boolenFalseLabel;
	}

	public void setBoolenFalseLabel(String boolenFalseLabel) {
		this.boolenFalseLabel = boolenFalseLabel;
	}

	public List<String> getRelations() {
		return relations;
	}

	public void setRelations(List<String> relations) {
		this.relations = relations;
	}

}
