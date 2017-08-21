<form id="string-phenotype-form" action="" url="${rootPath}/phenotype/create-restricted-phenotype" method="post" accept-charset="UTF-8" class="hidden">
	<input type="hidden" name="datatype" value="string">

	<#include "Id.ftl">
	<#include "Labels.ftl">
	<#include "SuperPhenotype.ftl">
    <#include "Definitions.ftl">
    <#include "Relations.ftl">

	<div class="form-group row">
	    <label class="control-label col-sm-2">Restriction*</label>

    	<div class="col-sm-10" id="datatype-specification">
			<#include "Enum.ftl">
		</div>
	</div>

	<div class="form-group">
		<input type="button" class="btn btn-primary" value="Create String Phenotype">
	</div>
</form>