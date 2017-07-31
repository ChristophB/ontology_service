<div class="form-group row">
	<div class="col-sm-2">
		<label for="relation" class="control-label">Relations</label>
		<a class="btn btn-primary btn-xs" onclick="addRow('#relation-div')" data-toggle="tooltip" title="Add a relation" data-placement="right">
			<i class="fa fa-plus" aria-hidden="true"></i>
		</a>
	</div>
	<div class="col-sm-8" id="relation-div">
		<input type="text" class="form-control hidden" id="relation[]" name="relation[]" placeholder="https://example.com/foo#bar">
	</div>
</div>