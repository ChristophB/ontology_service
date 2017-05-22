<#setting url_escaping_charset="UTF-8">
<#macro active name><#if current_submenu == name>active</#if></#macro>

<div class="row">
	<div class="col-md-12 text-center">
		<div class="list-inline list-group list-group-horizontal">
			<a class="list-group-item <@active "Overview" />"
			   href="${rootPath}/project/${project.projectId}/overview">
				Overview
			</a>
			
			<a class="list-group-item <@active "Query this Ontology" />"
			   href="${rootPath}/entity-form?ontologies=${project.projectIri?url}">
				Query this Ontology
			</a>
			
			<a class="list-group-item <@active "Reason this Ontology" />"
			   href="${rootPath}/reason-form?ontologies=${project.projectIri?url}">
				Reason this Ontology
			</a>
			
			<a class="list-group-item <@active "Taxonomy" />"
			   href="${rootPath}/project/${project.projectId}/taxonomy">
			   	Taxonomy
			</a>
			
			<a class="list-group-item" href="${rootPath}/project/${project.projectId}">
				OWL File (RDF/XML)
			</a>
			
			<a class="list-group-item" href="${webProtegeUri}" target="_blank">
				Open in WebProt&#233;g&#233;
			</a>
		</div>
	</div>
</div>