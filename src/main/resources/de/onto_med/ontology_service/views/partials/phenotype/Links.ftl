<#macro active name><#if current_submenu == name>active</#if></#macro>

<div class="row">
	<div class="col-md-12 text-center">
		<div class="list-inline list-group list-group-horizontal">
			<#if id??>
				<a class="list-group-item active" id="edit-link" href="#" onclick="showPhenotypeForm('', true)">
					Edit Phenotypes
				</a>
			</#if>

			<#if id??>
				<a class="list-group-item" id="reason-link" href="#" onclick="showPhenotypeForm('#reason-form', true)">
					Reasoning
				</a>
			</#if>
		</div>
	</div>
</div>
