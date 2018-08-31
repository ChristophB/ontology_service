<#macro active name><#if current == name> class="active"</#if></#macro>

<nav id="main-navbar" class="navbar navbar-default navbar-fixed-top">
	<div class="container">
		<a class="navbar-brand" href="https://github.com/ChristophB/ontology_service">Ontology Service</a>
		<ul class="nav navbar-nav navbar-right">
			<li id="main-navbar-documentation" <@active "Documentation" />><a href="${rootPath}">Documentation</a></li>

			<li id="main-navbar-webprotege" role="presentation" class="dropdown<#if current == "Projects" || current == "Query Entities" || current == "Reasoning"> active</#if>">
				<a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
					WebProt&#233;g&#233; <span class="caret"></span>
				</a>
				<ul class="dropdown-menu">
                	<li<@active "Projects" />><a href="${rootPath}/projects">Projects</a></li>
                	<li<@active "Query Entities" />><a href="${rootPath}/entity-form">Query Entities</a></li>
                	<li<@active "Reasoning" />><a href="${rootPath}/reason-form">Reasoning</a></li>
                </ul>
			</li>

			<li id="main-navbar-phenotype" <@active "Phenotyping" />><a href="${rootPath}/phenotype">Phenotyping</a></li>
		</ul>
	</div>
</nav>
<div class="row" style="height: 50px; width:100%"></div>