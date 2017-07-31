<div id="expression-form-group" class="hidden">
	<label for="expression" class="control-label">Boolean-Expression*</label>
	<input type="button" value="AND">
	<input type="button" value="OR">
	<input type="button" value="(">
	<input type="button" value=")">
	
	<textarea class="form-control drop phenotype" id="expression" name="expression"
		placeholder="Phenotype_1 AND (Phenotype_2 OR Phenotype_3)" aria-describedby="expression-help"
	><#if phenotype?? & phenotype.expression??>${phenotype.expression}</#if></textarea>
	<small id="expression-help" class="form-text text-muted">
		Drag-and-drop a phenotype from the right site into your expression.
	</small>
</div>