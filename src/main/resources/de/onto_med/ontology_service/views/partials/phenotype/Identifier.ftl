<div class="form-group row">
	<label for="identifier" class="control-label col-sm-2">Identifier</label>

	<div class="col-sm-8" id="title-div">
		<small class="form-text text-muted">
			A <b>unique</b> identifier of the phenotype or category.
			If this field is left empty, a value will be generated automatically.
		</small>

		<input type="text" class="form-control awesomplete" id="identifier" name="identifier" placeholder="Some_Unique_Identifier"
			   onblur="inspectIfExists($('form:not(.hidden) input#identifier').val())">
	</div>
</div>