<div class="form-group row">
	<div class="col-sm-2">
		<label class="control-label">Descript.</label>
		<a class="btn btn-primary btn-xs pull-right" onclick="addRow('#description-div')" data-toggle="tooltip" title="Add a Description" data-placement="right">
			<i class="fa fa-plus" aria-hidden="true"></i>
		</a>
	</div>
	
	<div class="col-sm-10" id="description-div">
		<div class="input-group hidden">
			<div class="input-group-addon">
				<select name="descriptionLanguages[]" data-toggle="tooltip" title="Language" data-placement="right">
					<option value="en">EN</option>
					<option value="de">DE</option>
					<option value="fr">FR</option>
					<option value="es">ES</option>
				</select>
			</div>
			<textarea class="form-control" name="descriptions[]" placeholder="Some Description"></textarea>
		</div>
	</div>
</div>