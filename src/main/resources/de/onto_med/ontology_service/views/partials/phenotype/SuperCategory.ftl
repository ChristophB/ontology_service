<#macro selected string><#if phenotype?? & phenotype.category?? & phenotype.category == string>selected</#if></#macro>

<div class="form-group row">
	<label for="superCategory" class="control-label col-sm-2">Super Category</label>
	<div class="col-sm-3">
		<input type="text" class="form-control" id="super-category" name="superCategory"
		    value="<#if phenotype?? & phenotype.superCategory??>${phenotype.superCategory}</#if>"
		>
	</div>
</div>