<#macro active name><#if current_submenu == name>active</#if></#macro>

<div class="row">
	<div class="col-md-12 text-center">
		<div class="list-inline list-group list-group-horizontal">
			<a class="list-group-item <@active "overview" />" href="${rootPath}/phenotype">
				Overview
			</a>
			
			<a class="list-group-item <@active "singlephenotype-form" />" href="${rootPath}/phenotype/singlephenotype-form">
				Create Simple Phenotype
			</a>
			
			<a class="list-group-item <@active "compositephenotype-form" />" href="${rootPath}/phenotype/compositephenotype-form">
				Create Composite Phenotype
			</a>
			
			<a class="list-group-item <@active "all" />" href="${rootPath}/phenotype/all">
				Show All
			</a>
		</div>
	</div>
</div>
