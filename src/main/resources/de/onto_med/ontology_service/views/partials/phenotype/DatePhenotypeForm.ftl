<form id="date-phenotype-form" action="" url="${rootPath}/phenotype/${id}/create" method="post" accept-charset="UTF-8" class="hidden">
	<input type="hidden" name="isPhenotype" value="true">
	<input type="hidden" name="isRestricted" value="true">
	<input type="hidden" name="datatype" value="date">

	<#include "Identifier.ftl">
	<#include "Title.ftl">
	<#include "Synonym.ftl">
	<#include "SuperPhenotype.ftl">
    <#include "Description.ftl">
    <#include "Relations.ftl">

	<div class="form-group row">
	    <label class="control-label col-sm-2">Restriction*</label>

    	<div class="col-sm-10" id="datatype-specification">
			<#include "Range.ftl">
			<#include "Enum.ftl">
		</div>
	</div>

	<div class="form-group">
		<input type="button" id="submit" class="btn btn-primary" value="Create Restricted Date Phenotype">
	</div>
</form>