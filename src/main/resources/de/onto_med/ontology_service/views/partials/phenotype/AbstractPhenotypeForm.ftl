<form id="abstract-phenotype-form" action="" url="${rootPath}/phenotype/${id}/create-abstract-phenotype" method="post" accept-charset="UTF-8" class="hidden">
	<#include "Id.ftl">
	<#include "Labels.ftl">
	<#include "Category.ftl">
	<#include "Definitions.ftl">
    <#include "Relations.ftl">

    <#assign datatypes = [
       'numeric', 'Number',
       'date', 'Date',
       'string', 'String',
       'boolean', 'Boolean',
       'calculation', 'Calculation',
       'composite-boolean', 'Boolean Expression'
    ]>
    <#include "Datatype.ftl">

    <#include "Formula.ftl">
    <#include "Ucum.ftl">
    <#include "IsDecimal.ftl">

	<div class="form-group">
		<input type="button" id="submit" class="btn btn-primary" value="Create Abstract Phenotype">
	</div>
</form>