<div class="form-group row">
	<div class="col-sm-2">
		<label class="control-label">Synonyms</label>
		<a class="btn btn-primary btn-xs pull-right" onclick="addRow('#synonym-div')" data-toggle="tooltip" title="Add a Synonym" data-placement="right">
			<i class="fa fa-plus" aria-hidden="true"></i>
		</a>
	</div>
	
	<div class="col-sm-8" id="synonym-div">
		<small class="form-text text-muted">Specify some synonyms in different languages.</small>
	
		<div class="input-group hidden">
			<div class="input-group-addon">
				<select name="synonymLanguages[]" class="hidden-language" data-toggle="tooltip" title="Language" data-placement="right">
					<option value="en">EN</option>
					<option value="de">DE</option>
					<option value="fr">FR</option>
					<option value="es">ES</option>
				</select>
			</div>
			<input type="text" class="form-control" name="synonyms[]" placeholder="Some Synonym">
		</div>
	</div>
</div>