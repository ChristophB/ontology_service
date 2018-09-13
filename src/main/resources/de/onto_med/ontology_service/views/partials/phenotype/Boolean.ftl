<div id="boolean-form-group" class="hidden">
	<label for="boolean" class="control-label">Possible Values</label><br>
	<small class="form-text text-muted">
		Define appropriate synonyms for True and False.
	</small>
	<div class="row">
		<label for="booleanTrueLabel" class="control-label col-sm-1">True:</label>
		<div class="col-sm-5">
			<input type="text" class="form-control" name="booleanTrueLabel" placeholder="Label"
				<#if phenotype?? & phenotype.booleanTrueLabel??> value="${phenotype.booleanTrueLabel}"</#if>
			>
		</div>
		<label for="booleanFalseLabel" class="control-label col-sm-1">False:</label>
		<div class="col-sm-5">
			<input type="text" class="form-control" name="booleanFalseLabel" placeholder="Label"
				<#if phenotype?? & phenotype.booleanFalseLabel??> value="${phenotype.booleanFalseLabel}"</#if>
			>
		</div>
	</div>
</div>