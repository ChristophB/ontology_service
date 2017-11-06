<div class="form-group row">
	<label for="id" class="control-label col-sm-2">ID*</label>
	<div class="col-sm-6">
		<small class="form-text text-muted">A <b>unique</b> identifier of the phenotype or category.</small>
		<input type="text" class="form-control awesomplete" id="id" name="id" placeholder="Some_Unique_Identifier" onblur="inspectIfExists($('form:not(.hidden) input#id').val())" required>
	</div>
</div>