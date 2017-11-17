<div class="form-group row">
	<div class="col-sm-2">
		<label for="title-en" class="control-label">Title*</label>
		<a class="btn btn-primary btn-xs pull-right" onclick="addRow('#title-div')" data-toggle="tooltip" title="Add a Title" data-placement="right">
			<i class="fa fa-plus" aria-hidden="true"></i>
		</a>
	</div>

	<div class="col-sm-8" id="title-div">
		<small class="form-text text-muted">A <b>unique</b> title of the phenotype or category. At least one title is required. Aliases are used as abbreviations in generated images.</small>

		<div class="input-group hidden">
           	<div class="input-group-addon">
          		<select name="titleLanguages[]" class="hidden-language" data-toggle="tooltip" title="Language" data-placement="right">
                   	<option value="en" selected="selected">EN</option>
                   	<option value="de">DE</option>
                   	<option value="fr">FR</option>
                   	<option value="es">ES</option>
                </select>
           	</div>
           	<input type="text" class="form-control" id="titles" name="titles[]" placeholder="Some_Unique_Identifier">
           	<div class="input-group-addon alias-addon">
           		<input type="text" class="form-control" id="aliases" name="aliases[]" placeholder="Alias">
           	</div>
        </div>

		<div class="input-group">
        	<div class="input-group-addon">
        		<select name="titleLanguages[]" id="title-languages" data-toggle="tooltip" title="Language" data-placement="right">
            		<option value="en">EN</option>
            		<option value="de">DE</option>
            		<option value="fr">FR</option>
            		<option value="es">ES</option>
            	</select>
            </div>
        	<input type="text" class="form-control awesomplete" id="titles" name="titles[]" placeholder="Some_Unique_Identifier" onblur="inspectIfExists($('form:not(.hidden) input#titles:first').val())">
        	<div class="input-group-addon alias-addon">
        		<input type="text" class="form-control" id="aliases" name="aliases[]" placeholder="Alias">
        	</div>
        </div>
	</div>
</div>