<div class="form-group row">
	<div class="col-sm-2">
		<label class="control-label">Definitions</label>
		<a class="btn btn-primary btn-xs pull-right" onclick="addRow('#definition-div')" data-toggle="tooltip" title="Add a Definition" data-placement="right">
			<i class="fa fa-plus" aria-hidden="true"></i>
		</a>
	</div>
	
	<div class="col-sm-10" id="definition-div">
		<div class="input-group hidden">
			<div class="input-group-addon">
				<select name="definition-language[]" data-toggle="tooltip" title="Language" data-placement="right">
					<option value="en">EN</option>
					<option value="de">DE</option>
					<option value="fr">FR</option>
					<option value="es">ES</option>
				</select>
			</div>
			<textarea class="form-control" name="definition[]" placeholder="Some Definition"></textarea>
		</div>
	</div>
</div>