<div class="row">
	<label for="datatype" class="control-label col-sm-2">Datatype*</label>
	
	<div class="col-sm-3">
		<select id="datatype" name="datatype" class="form-control" onchange="toggleValueDefinition()" required>
			<option />
			<#list datatypes?chunk(2) as row>
				<option value="${row[0]}">${row[1]}</option>
			</#list>
		</select>
	</div>
	
	<small class="form-text text-muted col-sm-5">Some description about the datatypes</small>
</div>