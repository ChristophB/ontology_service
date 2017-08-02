<label for="operator" class="col-md-2">Logical Operator:</label>
<div class="col-md-2">
	<select name="operator" class="form-control" title="Applied to 'IRI', 'name' and 'property'.">
		<option value="or" <#if operator?? && operator == "or">selected</#if>>or</option>
		<option value="and">and</option>
	</select>
</div>