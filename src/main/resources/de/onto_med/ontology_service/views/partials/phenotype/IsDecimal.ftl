<div id="is-decimal-form-group" class="form-group row hidden">
	<label for="isDecimal" class="control-label col-sm-2">Is Decimal</label>
	<div class="col-sm-1">
	    <input type="checkbox" class="form-control" name="isDecimal" value="true" aria-describedby="is-decimal-help"
		    <#if phenotype?? & phenotype.isDecimal?? & phenotype.isDecimal>checked</#if>
	    >
	</div>
	<small id="is-decimal-help" class="form-text text-muted col-sm-3">
	    Select this checkbox if the phenotype allows decimal values.
	</small>
</div>