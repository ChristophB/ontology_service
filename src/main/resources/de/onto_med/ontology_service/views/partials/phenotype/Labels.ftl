<div class="form-group row">
	<div class="col-sm-2">
		<label class="control-label">Labels</label>
		<a class="btn btn-primary btn-xs" onclick="addRow('#labels-div')" data-toggle="tooltip" title="Add a Label" data-placement="right">
			<i class="fa fa-plus" aria-hidden="true"></i>
		</a>
	</div>
	
	<div class="col-sm-6" id="labels-div">
		<small class="form-text text-muted">Specify some human-readable labels in different languages.</small>
	
		<div class="input-group hidden">
		<div class="input-group-addon">
			<select name="label-language[]" data-toggle="tooltip" title="Language" data-placement="right">
				<option value="en">EN</option>
				<option value="de">DE</option>
				<option value="fr">FR</option>
				<option value="es">ES</option>
			</select>
		</div>
			<input type="text" class="form-control" name="label[]" placeholder="Some Label">
		</div>
	</div>
</div>