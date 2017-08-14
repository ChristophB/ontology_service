<div id="range-form-group">
	<label class="control-label">By Range</label>
	<small class="form-text text-muted">
		Specify min and/or max values.<br>
		e.g.: >20, &le;34.5 => normal | &le;20 => too low | &gt;34.5 => too high
	</small>

	<div class="row">
		<div class="col-sm-2" style="padding-right:0">
			<select class="form-control" name="range-min-operator">
				<option />
				<!-- <option value="=">=</option> -->
				<option value="&gt;=">&ge;</option>
				<option value="&gt;">&gt;</option>
			</select>
		</div>
		<div class="col-sm-2" style="padding-left:0">
			<input type="text" class="form-control" name="range-min" placeholder="Min">
		</div>
			
		<div class="col-sm-2" style="padding-right:0">
			<select class="form-control" name="range-max-operator">
				<option />
				<option value="&lt;">&lt;</option>
				<option value="&lt;=">&le;</option>
				<!-- <option value="=">=</option> -->
			</select>
		</div>
		<div class="col-sm-2" style="padding-left:0">
			<input type="text" class="form-control" name="range-max" placeholder="Max">
		</div>
	</div>
</div>