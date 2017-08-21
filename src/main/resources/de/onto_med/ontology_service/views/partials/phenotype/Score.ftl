<div id="score-form-group">
	<label class="control-label">By Score</label>
	<input type="number" class="form-control" steps="any" name="score" value="<#if phenotype?? & phenotype.score??>${phenotype.score}</#if>">
	<small class="form-text text-muted">Some description about scores.</small>
</div>