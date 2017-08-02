<#macro active name><#if current_submenu == name>active</#if></#macro>

<div class="row">
	<div class="col-md-12 text-center">
		<div class="list-inline list-group list-group-horizontal">
			<a class="list-group-item <@active "overview" />" href="${rootPath}/phenotype">
				Overview
			</a>
			
			<a class="list-group-item <@active "simplephenotype-form" />" href="${rootPath}/phenotype/simplephenotype-form">
				Create Simple Phenotype
			</a>
			
			<a class="list-group-item <@active "compositphenotype-form" />" href="${rootPath}/phenotype/compositphenotype-form">
				Create Composit Phenotype
			</a>
			
			<a class="list-group-item <@active "all" />" href="${rootPath}/phenotype/all">
				Show All
			</a>
			
			<a class="list-group-item <@active "decision-tree-form" />" href="${rootPath}/phenotype/decision-tree-form">
				Generate Decision Tree
			</a>
		</div>
	</div>
</div>
