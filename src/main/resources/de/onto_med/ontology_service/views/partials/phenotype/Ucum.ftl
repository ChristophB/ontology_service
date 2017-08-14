<div id="ucum-form-group" class="form-group row hidden">
	<label for="ucum" class="control-label col-sm-2">UCUM</label>
	<div class="col-sm-4">
	    <input type="text" class="form-control" id="ucum" name="ucum" aria-describedby="ucum-help"
		    <#if phenotype?? & phenotype.ucum??>value="${phenotype.ucum}"</#if>
	    >
	    <small id="ucum-help" class="form-text text-muted">
	        If the numeric values do have a unit, specify it as <a href="http://unitsofmeasure.org/trac">UCUM</a>.
	    </small>
	</div>
</div>