<form id="phenotype-category-form" action="" url="${rootPath}/phenotype/${id}/create" method="post" accept-charset="UTF-8" class="hidden">
	<input type="hidden" name="isPhenotype" value="false">

	<#include "Identifier.ftl">
	<#include "Title.ftl">
	<#include "Synonym.ftl">
	<#include "SuperCategory.ftl">
	<#include "Description.ftl">
    <#include "Relations.ftl">

	<div class="form-group">
		<input type="button" id="submit" class="btn btn-primary" value="Create Category">
	</div>
</form>