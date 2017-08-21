<div class="form-group row">
	<label class="control-label col-sm-2">Super-Phenotype</label>
	<div class="checkbox col-sm-3">
		<input type="text" class="form-control" id="super-phenotype" name="superPhenotype" placeholder="Some_Super_Phenotype"
           	<#if phenotype?? & phenotype.superPhenotype??>value="${phenotype.superPhenotype}"</#if>
        >
	</div>
	<small class="form-text text-muted col-sm-6" id="super-phenotype-help">
		Right-Click on a phenotype on the right site and select "Create Restricted Phenotype" to change this value.
	</small>
</div>