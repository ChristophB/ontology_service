<#macro active name><#if current_submenu == name>active</#if></#macro>

<div class="row">
	<div class="col-md-12 text-center">
		<div class="list-inline list-group list-group-horizontal">
			<a class="list-group-item <@active "overview" />" href="${rootPath}/phenotype">
				Overview
			</a>
			
			<a class="list-group-item <@active "simplephenotype_form" />" href="${rootPath}/phenotype/simplephenotype_form">
				Create Simple Phenotype
			</a>
			
			<a class="list-group-item <@active "compositphenotype_form" />" href="${rootPath}/phenotype/compositphenotype_form">
				Create Composit Phenotype
			</a>
			
			<a class="list-group-item <@active "all" />" href="${rootPath}/phenotype/all">
				Show All
			</a>
		</div>
	</div>
</div>
