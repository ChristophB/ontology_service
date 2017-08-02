<div id="range-form-group" class="hidden">
	<label class="control-label">Ranges</label> 
	<a class="btn btn-primary btn-xs" onclick="addRow('#range-form-group')">
		<i class="fa fa-plus" aria-hidden="true"></i>
	</a><br>
	<small class="form-text text-muted">
		Specify min and/or max values and give the resulting intervals a label.<br>
		e.g.: >20, &le;34.5 => normal | &le;20 => too low | &gt;34.5 => too high
	</small>

	<div class="row hidden">
		<div class="col-sm-2" style="padding-right:0">
			<select class="form-control" name="range-min-operator[]">
				<option />
				<option value="=">=</option>
				<option value="&ge;">&ge;</option>
				<option value="&gt;">&gt;</option>
			</select>
		</div>
		<div class="col-sm-2" style="padding-left:0">
			<input type="number" step="any" class="form-control" name="range-min[]" placeholder="Min">
		</div>
			
		<div class="col-sm-2" style="padding-right:0">
			<select class="form-control" name="range-max-operator[]">
				<option />
				<option value="&lt;">&lt;</option>
				<option value="&le;">&le;</option>
				<option value="=">=</option>
			</select>
		</div>
		<div class="col-sm-2" style="padding-left:0">
			<input type="number" step="any" class="form-control" name="range-max[]" placeholder="Max">
		</div>
	
		<div class="col-sm-4">
			<input type="text" class="form-control" name="enum-label" placeholder="Label">
		</div>
	</div>
</div>