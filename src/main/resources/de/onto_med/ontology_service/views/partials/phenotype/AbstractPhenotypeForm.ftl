<form id="abstract-phenotype-form" action="" url="${rootPath}/phenotype/create-abstract-phenotype" method="post" accept-charset="UTF-8" class="hidden">
	<#include "Id.ftl">
	<#include "Labels.ftl">
	<#include "Category.ftl">
	<#include "Definitions.ftl">
    <#include "Relations.ftl">

    <#assign datatypes = [
       'numeric', 'Number',
       'date', 'Date',
       'string', 'String',
       'calculation', 'Calculation',
       'boolean', 'Boolean'
    ]>
    <#include "Datatype.ftl">

    <#include "Formula.ftl">
    <#include "Ucum.ftl">
    <#include "IsDecimal.ftl">

	<div class="form-group">
		<input type="button" class="btn btn-primary" value="Create Abstract Phenotype">
	</div>
</form>