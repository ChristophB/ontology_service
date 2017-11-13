<div class="form-group row">
	<label for="title-en" class="control-label col-sm-2">Title*</label>
	<div class="col-sm-6">
		<small class="form-text text-muted">A <b>unique</b> title of the phenotype or category. At least one title is required.</small>
		<div class="input-group">
        	<div class="input-group-addon">EN</div>
        	<input type="text" class="form-control awesomplete" id="title-en" name="titleEn" placeholder="Some_Unique_Identifier" onblur="inspectIfExists($('form:not(.hidden) input#title-en').val())">
        	<input type="text" class="form-control" id="alias-en" name="aliasEn" placeholder="Alias">
        </div>
        <div class="input-group">
           	<div class="input-group-addon">DE</div>
           	<input type="text" class="form-control awesomplete" id="title-de" name="titleDe" placeholder="Some_Unique_Identifier" onblur="inspectIfExists($('form:not(.hidden) input#title-de').val())">
           	<input type="text" class="form-control" id="alias-de" name="aliasDe" placeholder="Alias">
        </div>
	</div>
</div>