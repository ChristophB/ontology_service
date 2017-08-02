<div class="form-group row">
	<label for="type" class="col-md-2">Type:</label>
	<div class="col-md-3">
		<select name="type" class="form-control">
			<option value="entity">Entity</option>
			<option value="class" <#if type?? && type == "class">selected</#if>>Class</option>
			<option value="individual" <#if type?? && type == "individual">selected</#if>>Individual</option>
		</select>
	</div>
</div>