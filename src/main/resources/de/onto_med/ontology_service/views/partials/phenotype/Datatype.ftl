<#macro selected string><#if phenotype?? & phenotype.datatype?? & phenotype.datatype == string>selected</#if></#macro>

<div class="form-group row">
	<label for="datatype" class="control-label col-sm-2">Datatype*</label>
	
	<div class="col-sm-4">
		<select id="datatype" name="datatype" class="form-control" onchange="toggleValueDefinition()" required>
			<option />
			<#list datatypes?chunk(2) as row>
				<option value="${row[0]}" <@selected row[0] />>${row[1]}</option>
			</#list>
		</select>
	</div>

</div>