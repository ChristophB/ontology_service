<#macro active name><#if current_submenu == name>active</#if></#macro>

<div class="row">
	<div class="col-md-12 text-center">
		<div class="list-inline list-group list-group-horizontal">
			<a class="list-group-item <@active "overview" />" href="${rootPath}/phenotype">
				Overview
			</a>

			<a class="list-group-item <@active "phenotype-form" />" href="${rootPath}/phenotype/phenotype-form">
				Create Phenotype
			</a>
		</div>
	</div>
</div>
