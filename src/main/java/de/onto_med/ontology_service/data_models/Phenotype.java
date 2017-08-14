package de.onto_med.ontology_service.data_models;

import java.util.List;

import javax.ws.rs.FormParam;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Phenotype {
	@FormParam("id") @JsonProperty("id")
	private String id;
	@FormParam("label[]") @JsonProperty("label[]")
	private List<String> labels;
	@FormParam("label-language[]") @JsonProperty("label-language[]")
	private List<String> labelLanguages;
	@FormParam("super-phenotype") @JsonProperty("super-phenotype")
	private String superPhenotype;
	@FormParam("category") @JsonProperty("category")
	private String category;
	@FormParam("definition[]") @JsonProperty("definition[]")
	private List<String> definitions;
	@FormParam("definition-language[]") @JsonProperty("definition-language[]")
	private List<String> definitionLanguages;
	@FormParam("datatype") @JsonProperty("datatype")
	private String datatype;
	@FormParam("ucum") @JsonProperty("ucum")
	private String ucum;
	@FormParam("range-min") @JsonProperty("range-min")
	private List<String> rangeMins;
	@FormParam("range-min-operator") @JsonProperty("range-min-operator")
	private List<String> rangeMinOperators;
	@FormParam("range-max") @JsonProperty("range-max")
	private List<String> rangeMaxs;
	@FormParam("range-max-operator") @JsonProperty("range-max-operator")
	private List<String> rangeMaxOperators;
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
	@FormParam("isDecimal") @JsonProperty("isDecimal")
	private boolean isDecimal;


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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

}
