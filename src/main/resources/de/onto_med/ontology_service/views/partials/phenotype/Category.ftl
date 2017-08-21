<#macro selected string><#if phenotype?? & phenotype.category?? & phenotype.category == string>selected</#if></#macro>

<div class="form-group row">
	<label for="category" class="control-label col-sm-2">Category</label>
	<div class="col-sm-4">
		<input type="text" class="form-control drop category" id="categories" name="categories"
		    value="<#if phenotype?? & phenotype.categories??>${phenotype.categories}</#if>"
		>
	</div>
	<small class="form-text text-muted col-sm-4">
	    Insert multiple categories for this phenotype separated with semicolon.
	</small>
</div>