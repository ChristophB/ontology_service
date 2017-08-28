<form id="composite-boolean-phenotype-form" action="" url="${rootPath}/phenotype/create-restricted-phenotype" method="post" accept-charset="UTF-8" class="hidden">
	<input type="hidden" name="datatype" value="composite-boolean">

	<#include "Id.ftl">
	<#include "Labels.ftl">
	<#include "SuperPhenotype.ftl">
    <#include "Definitions.ftl">
    <#include "Relations.ftl">

	<div class="form-group row">
	    <label class="control-label col-sm-2">Restriction*</label>

    	<div class="col-sm-10" id="datatype-specification">
    	    <#include "BooleanExpression.ftl">
    	    <#include "Score.ftl">
		</div>
	</div>

	<div class="form-group">
		<input type="button" id="submit" class="btn btn-primary" value="Create Composite Boolean Phenotype">
	</div>
</form>