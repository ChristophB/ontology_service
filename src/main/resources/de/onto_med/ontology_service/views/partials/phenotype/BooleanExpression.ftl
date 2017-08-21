<div id="expression-form-group">
	<label for="expression" class="control-label">Boolean-Expression*</label>
	<input type="button" class="btn btn-default" value="AND">
	<input type="button" class="btn btn-default" value="OR">
	<input type="button" class="btn btn-default" value="(">
	<input type="button" class="btn btn-default" value=")">
	
	<textarea class="form-control drop phenotype" id="expression" name="expression"
		placeholder="Phenotype_1 AND (Phenotype_2 OR Phenotype_3)" aria-describedby="expression-help"
	><#if phenotype?? & phenotype.expression??>${phenotype.expression}</#if></textarea>
	<small id="expression-help" class="form-text text-muted">
		Drag-and-drop a phenotype from the right site into your expression.
	</small>
</div>