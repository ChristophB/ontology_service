<form id="phenotype-category-form" action="" url="${rootPath}/phenotype/${id}/create" method="post" accept-charset="UTF-8" class="hidden">
	<input type="hidden" name="isPhenotype" value="false">

	<#include "Title.ftl">
	<#include "Labels.ftl">
	<#include "SuperCategory.ftl">
	<#include "Descriptions.ftl">
    <#include "Relations.ftl">

	<div class="form-group">
		<input type="button" id="submit" class="btn btn-primary" value="Create Category">
	</div>
</form>