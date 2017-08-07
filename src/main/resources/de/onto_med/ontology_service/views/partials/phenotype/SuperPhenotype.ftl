<div class="form-group row">
	<label class="control-label col-sm-2">Super-Phenotype</label>
	<div class="checkbox col-sm-3">
		<label for="has-super-phenotype" class="control-label">
			<input type="checkbox" id="has-super-phenotype" name="has-super-phenotype" onchange="toggleSuperPhenotype()" value="true"
				<#if phenotype?? & phenotype.hasSuperPhenotype?? & phenotype.hasSuperPhenotype>checked</#if>
			>Has Super-Phenotype
		</label>
	</div>
	<small class="form-text text-muted col-sm-6" id="has-super-phenotype-help">
		Check this box, if this phenotype is a derivation of another phenotype.<br>
		e.g.: "Weight between 50kg and 60kg" is a derivation of phenotype "Weight".
	</small>
	
	<div id="super-phenotype-div" class="hidden">
		<div class="col-sm-4">
			<input type="text" class="form-control" id="super-phenotype" name="super-phenotype" placeholder="Some_Super_Phenotype"
				<#if phenotype?? & phenotype.superPhenotype??>value="${phenotype.superPhenotype}"</#if>
			>
		</div>
		<small class="form-text text-muted col-sm-3" id="super-phenotype-help">
			Right-Click on a phenotype on the right site and select "Set as Super-Phenotype".
		</small>
	</div>
</div>