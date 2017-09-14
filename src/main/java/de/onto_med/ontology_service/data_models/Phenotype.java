package de.onto_med.ontology_service.data_models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.lha.phenoman.model.phenotype.AbstractSinglePhenotype;
import org.lha.phenoman.model.phenotype.RestrictedBooleanPhenotype;
import org.lha.phenoman.model.phenotype.top_level.Category;
import org.lha.phenoman.model.phenotype.top_level.TextLang;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.ArrayList;
import java.util.List;

public class Phenotype {
	@JsonProperty
	private String id;
	@JsonProperty
	private List<String> labels = new ArrayList<>();
	@JsonProperty
	private List<String> labelLanguages = new ArrayList<>();
	@JsonProperty
	private String superPhenotype;
	@JsonProperty
	private String categories;
	@JsonProperty
	private String superCategory;
	@JsonProperty
	private List<String> definitions = new ArrayList<>();
	@JsonProperty
	private List<String> definitionLanguages = new ArrayList<>();
	@JsonProperty
	private String datatype;
	@JsonProperty
	private String ucum;
	@JsonProperty
	private String rangeMin;
	@JsonProperty
	private String rangeMinOperator;
	@JsonProperty
	private String rangeMax;
	@JsonProperty
	private String rangeMaxOperator;
	@JsonProperty
	private List<String> enumValues = new ArrayList<>();
	@JsonProperty
	private String formula;
	@JsonProperty
	private String expression;
	@JsonProperty
	private List<String> relations = new ArrayList<>();
	@JsonProperty
	private Boolean isDecimal;
	@JsonProperty
	private Double score;


	public Phenotype() { }

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

	public Boolean getIsDecimal() {
		return isDecimal;
	}

	public void setIsDecimal(Boolean isDecimal) {
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

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public List<String> getLabelLanguages() {
		return labelLanguages;
	}

	public void setLabelLanguages(List<String> labelLanguages) {
		this.labelLanguages = labelLanguages;
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

	public void setDefinitions(List<String> definitions) {
		this.definitions = definitions;
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

	public List<String> getRelations() {
		return relations;
	}

	public void setRelations(List<String> relations) {
		this.relations = relations;
	}

	private void addDefinition(String definition) {
		List<String> definitions = getDefinitions() != null ? getDefinitions() : new ArrayList<>();
		definitions.add(definition);
	}

	private void addDefinitionLanguage(String definitionLanguage) {
		List<String> definitionLanguages = getDefinitionLanguages() != null ? getDefinitionLanguages() : new ArrayList<>();
		definitionLanguages.add(definitionLanguage);
	}

	private void addLabel(String label) {
		List<String> labels = getLabels() != null ? getLabels() : new ArrayList<>();
		labels.add(label);
	}

	private void addLabelLanguage(String labelLanguage) {
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
