package de.onto_med.ontology_service.data_model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import care.smith.phep.phenoman.core.model.phenotype.top_level.Title;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public class PhenotypeFormData {
	@JsonProperty
	private String       identifier;
	@JsonProperty
	private String       mainTitle;
	@JsonProperty
	private List<String> titles               = new ArrayList<>();
	@JsonProperty
	private List<String> aliases              = new ArrayList<>();
	@JsonProperty
	private List<String> titleLanguages       = new ArrayList<>();
	@JsonProperty
	private List<String> synonyms             = new ArrayList<>();
	@JsonProperty
	private List<String> synonymLanguages     = new ArrayList<>();
	@JsonProperty
	private Boolean      isMainResult         = false;
	@JsonProperty
	private Boolean      isPhenotype;
	@JsonProperty
	private Boolean      isRestricted;
	@JsonProperty
	private String       superPhenotype;
	@JsonProperty
	private String       superCategory;
	@JsonProperty
	private List<String> descriptions         = new ArrayList<>();
	@JsonProperty
	private List<String> descriptionLanguages = new ArrayList<>();
	@JsonProperty
	private String       datatype;
	@JsonProperty
	private String       ucum;
	@JsonProperty
	private String       rangeMin;
	@JsonProperty
	private String       rangeMinOperator;
	@JsonProperty
	private String       rangeMax;
	@JsonProperty
	private String       rangeMaxOperator;
	@JsonProperty
	private List<String> enumValues           = new ArrayList<>();
	@JsonProperty
	private String       formula;
	@JsonProperty
	private String       formulaDatatype;
	@JsonProperty
	private String       expression;
	@JsonProperty
	private List<String> codeSystems          = new ArrayList<>();
	@JsonProperty
	private List<String> codes                = new ArrayList<>();
	@JsonProperty
	private List<String> relations            = new ArrayList<>();
	@JsonProperty
	private Double       score;
	@JsonProperty
	private String       aggregateFunction;

	private Map<String, Set<String>> descriptionMap;

	private Map<String, String> selectOptions;


	public PhenotypeFormData() { }

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@SuppressWarnings("unused")
	public Map<String, String> getSelectOptions() {
		return selectOptions;
	}

	public void setSelectOptions(Map<String, String> selectOptions) {
		this.selectOptions = selectOptions;
	}

	@SuppressWarnings("unused")
	public Map<String, Set<String>> getDescriptionMap() {
		return descriptionMap;
	}

	public void setDescriptionMap(Map<String, Set<String>> descriptionMap) {
		this.descriptionMap = descriptionMap;
	}

	public String getMainTitle() {
		return StringUtils.isBlank(mainTitle) ? identifier : mainTitle;
	}

	public void setMainTitle(String mainTitle) {
		this.mainTitle = mainTitle;
	}

	public Boolean getIsMainResult() {
		return isMainResult;
	}

	public void setIsMainResult(Boolean mainResult) {
		isMainResult = mainResult;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getAggregateFunction() {
		return aggregateFunction;
	}

	@SuppressWarnings("unused")
	public void setAggregateFunction(String aggregateFunction) {
		this.aggregateFunction = aggregateFunction;
	}

	public String getSuperCategory() {
		return superCategory;
	}

	public void setSuperCategory(String superCategory) {
		this.superCategory = superCategory;
	}

	public List<String> getTitles() {
		return titles;
	}

	public void setTitles(List<String> titles) {
		this.titles = titles;
	}

	public List<String> getAliases() {
		return aliases;
	}

	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}

	public List<String> getTitleLanguages() {
		return titleLanguages;
	}

	@SuppressWarnings("unused")
	public void setTitleLanguages(List<String> titleLanguages) {
		this.titleLanguages = titleLanguages;
	}

	public List<String> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(List<String> synonyms) {
		this.synonyms = synonyms;
	}

	public List<String> getSynonymLanguages() {
		return synonymLanguages;
	}

	public void setSynonymLanguages(List<String> synonymLanguages) {
		this.synonymLanguages = synonymLanguages;
	}

	public String getSuperPhenotype() {
		return superPhenotype;
	}

	public void setSuperPhenotype(String superPhenotype) {
		this.superPhenotype = superPhenotype;
	}

	public List<String> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}

	public List<String> getDescriptionLanguages() {
		return descriptionLanguages;
	}

	public void setDescriptionLanguages(List<String> descriptionLanguages) {
		this.descriptionLanguages = descriptionLanguages;
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

	public String getFormulaDatatype() {
		return formulaDatatype;
	}

	public void setFormulaDatatype(String formulaDatatype) {
		this.formulaDatatype = formulaDatatype;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public List<String> getCodeSystems() {
		return codeSystems;
	}

	public void setCodeSystems(List<String> codeSystems) {
		this.codeSystems = codeSystems;
	}

	public List<String> getCodes() {
		return codes;
	}

	public void setCodes(List<String> codes) {
		this.codes = codes;
	}

	public List<String> getRelations() {
		return relations;
	}

	public void setRelations(List<String> relations) {
		this.relations = relations;
	}

	public Boolean getIsPhenotype() {
		return isPhenotype;
	}

	public void setIsPhenotype(Boolean phenotype) {
		isPhenotype = phenotype;
	}

	public Boolean getIsRestricted() {
		return isRestricted;
	}

	public void setIsRestricted(Boolean restricted) {
		isRestricted = restricted;
	}

	@JsonIgnore
	public List<Title> getTitleObjects() {
		List<Title> result = new ArrayList<>();

		for (int i = 0; i < getTitles().size(); i++) {
			if (StringUtils.isBlank(getTitles().get(i))) continue;
			String title = getTitles().get(i);
			String alias = getAliases().size() > i ? getAliases().get(i) : null;
			String lang  = getTitleLanguages().size() > i ? getTitleLanguages().get(i) : null;

			if (alias != null && lang != null) result.add(new Title(title, alias, lang));
			else if (lang != null) result.add(new Title(title, lang));
			else result.add(new Title(title));
		}

		return result;
	}

	@JsonIgnore
	public String[] getSuperCategories() {
		if (StringUtils.isBlank(getSuperCategory())) return new String[0];
		List<String> result = new ArrayList<>();

		for (String superCategory : getSuperCategory().split(" ")) {
			result.add(superCategory.trim());
		}
		return result.toArray(new String[0]);
	}

	@SuppressWarnings("unused")
	private String owl2DatatypeToString(OWL2Datatype datatype) {
		if (OWL2Datatype.XSD_DECIMAL.equals(datatype))
			return "numeric";
		else if (OWL2Datatype.XSD_DATE_TIME.equals(datatype))
			return "date";
		else if (OWL2Datatype.XSD_STRING.equals(datatype))
			return "string";

		return null;
	}

}
