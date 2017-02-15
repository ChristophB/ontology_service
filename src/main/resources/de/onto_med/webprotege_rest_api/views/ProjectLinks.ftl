<#macro active name><#if current_submenu == name>active</#if></#macro>

<div class="row">
	<div class="col-md-12 text-center">
		<div class="list-inline list-group list-group-horizontal">
			<a class="list-group-item <@active "Overview" />"
			   href="/project/${project.projectId}/overview">
				Overview
			</a>
			
			<a class="list-group-item <@active "Query this Ontology" />"
			   href="/entity-form?ontologies=${project.projectId}">
				Query this Ontology
			</a>
			
			<a class="list-group-item <@active "Reason this Ontology" />"
			   href="/reason-form?ontologies=${project.projectId}">
				Reason this Ontology
			</a>
			
			<a class="list-group-item" href="/project/${project.projectId}" target="_blank">
				OWL File (RDF/XML)
			</a>
		</div>
	</div>
</div>