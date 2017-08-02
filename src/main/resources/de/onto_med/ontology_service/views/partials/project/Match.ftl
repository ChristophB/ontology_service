<label for="match" class="col-md-2">Match Method:</label>
<div class="col-md-2">
	<select name="match" class="form-control" title="Applied to 'IRI', 'name' and 'property'.">
		<option value="loose">loose</option>
		<option value="exact" <#if match?? && match == "exact">selected</#if>>exact</option>
	</select>
</div>