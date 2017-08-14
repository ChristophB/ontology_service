<#macro selected string><#if phenotype?? & phenotype.category?? & phenotype.category == string>selected</#if></#macro>

<div class="form-group row">
	<label for="category" class="control-label col-sm-2">Category</label>
	<div class="col-sm-3">
		<select id="category" name="category" class="form-control">
			<option />
			<option value="Category_1" <@selected "Category_1" />>Category 1</option>
			<option value="Category_2" <@selected "Category_2" />>Category 2</option>
		</select>
	</div>
</div>