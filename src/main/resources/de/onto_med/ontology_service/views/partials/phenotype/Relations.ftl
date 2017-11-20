<div class="form-group row">
	<div class="col-sm-2">
		<label class="control-label">Relations</label>
		<a class="btn btn-primary btn-xs pull-right" onclick="addRow('#relation-div')" data-toggle="tooltip" title="Add a relation" data-placement="right">
			<i class="fa fa-plus" aria-hidden="true"></i>
		</a>
	</div>
	<div class="col-sm-8" id="relation-div">
		<small class="form-text text-muted">Relations may be IRIs of concepts of ontologies or some identifying code or a database table/column name.</small>
		<#if phenotype?? & phenotype.relations??>
			<#list phenotype.relations as relation>
				<input type="text" class="form-control" name="relations[]" placeholder="https://example.com/foo#bar" value="${relation}">
			</#list>
		</#if>
		<input type="text" class="form-control hidden" name="relations[]" placeholder="https://example.com/foo#bar">
	</div>
</div>