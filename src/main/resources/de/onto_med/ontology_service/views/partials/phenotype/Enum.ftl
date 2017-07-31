<div id="enum-form-group" class="hidden">
	<label class="control-label">Possible Values</label>
	<a class="btn btn-primary btn-xs" onclick="addRow('#enum-form-group')">
		<i class="fa fa-plus" aria-hidden="true"></i>
	</a><br>
	<small class="form-text text-muted">
		Define specific values and add appropriate labels.
	</small>
	<div class="row hidden">
		<div class="col-sm-5">
			<input type="text" class="form-control" name="enum-value[]" placeholder="Value">
		</div>
		<div class="col-sm-1" style="margin-top:5px">=></div>
		<div class="col-sm-6">
			<input type="text" class="form-control" name="enum-label[]" placeholder="Label">
		</div>
	</div>
</div>