<div id="score-form-group">
	<label class="control-label">By Score</label>
	<div class="row">
		<div class="col-md-4">
			<input type="number" class="form-control" steps="any" id="score" name="score" value="<#if phenotype?? & phenotype.score??>${phenotype.score}</#if>">
		</div>
	</div>
	<small class="form-text text-muted">Some description about scores.</small>
</div>