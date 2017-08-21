<form id="phenotype-category-form" action="" url="${rootPath}/phenotype/create-category" method="post" accept-charset="UTF-8" class="hidden">
	<#include "Id.ftl">
	<#include "Labels.ftl">
	<#include "SuperCategory.ftl">
	<#include "Definitions.ftl">
    <#include "Relations.ftl">

	<div class="form-group">
		<input type="button" id="submit" class="btn btn-primary" value="Create Category">
	</div>
</form>