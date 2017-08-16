<#macro minSelected string><#if phenotype?? & phenotype.rangeMin?? & phenotype.rangeMin == string>selected</#if></#macro>
<#macro maxSelected string><#if phenotype?? & phenotype.rangeMax?? & phenotype.rangeMax == string>selected</#if></#macro>

<div id="range-form-group">
	<label class="control-label">By Range</label>
	<div class="row">
		<div class="col-sm-1" style="padding-right:0">
			<select class="form-control operator-select" name="range-min-operator">
				<option value="&gt;=" <@minSelected "&gt;="/> >&ge;</option>
				<option value="&gt;" <@minSelected "&gt;"/> >&gt;</option>
			</select>
		</div>
		<div class="col-sm-2" style="padding-left:0">
			<input type="text" class="form-control" name="range-min" placeholder="Min"
			    value="<#if phenotype?? & phenotype.rangeMin??>${phenotype.rangeMin}</#if>"
			>
		</div>
			
		<div class="col-sm-1" style="padding-right:0">
			<select class="form-control operator-select" name="range-max-operator">
				<option value="&lt;=" <@maxSelected "&lt;="/> >&le;</option>
				<option value="&lt;" <@maxSelected "&lt;"/> >&lt;</option>
			</select>
		</div>
		<div class="col-sm-2" style="padding-left:0">
			<input type="text" class="form-control" name="range-max" placeholder="Max"
			    value="<#if phenotype?? & phenotype.rangeMax??>${phenotype.rangeMax}</#if>"
			>
		</div>
	</div>
	<small class="form-text text-muted">
    	Specify min and/or max values.<br>
    	e.g.: &lt;20, &le;34.5 => normal | &le;20 => too low | &gt;34.5 => too high
    </small>
</div>