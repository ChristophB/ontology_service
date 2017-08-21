<div id="formula-form-group" class="for-group row hidden">
	<label for="formula" class="control-label col-sm-2">Formula*</label>
	<div class="col-sm-8">
	    <textarea class="form-control drop phenotype" id="formula" name="formula" aria-describedby="formula-help"
	        placeholder="(Phenotype_1 + Phenotype_2) / Phenotype_3"
	    ><#if phenotype?? & phenotype.formula??>${phenotype.formula}</#if></textarea>
	    <small id="formula-help" class="form-text text-muted">Click on a phenotype on the right site to add it into your formula.</small>
	</div>
</div>