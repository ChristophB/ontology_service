<div class="form-group row">
	<label for="property" class="col-md-2">Property:</label>
	<div class="col-md-3">
		<input type="text" name="property" placeholder="Name" class="form-control" <#if property??>value="${property}"</#if>>
	</div>
	<div class="col-md-3">
		<input type="text" name="value" placeholder="Value" class="form-control" <#if value??>value="${value}"</#if>>
	</div>
</div>