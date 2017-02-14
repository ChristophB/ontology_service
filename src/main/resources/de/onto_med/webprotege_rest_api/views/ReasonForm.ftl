<#-- @ftlvariable name="" type="de.onto_med.webprotege_rest_api.views.ReasonFormView" -->
<#assign title = "Reasoning">
<#assign current = "Reasoning">

<html>
	<#include "Head.ftl">
	
	<body>
		<#include "Navbar.ftl">
		
		<div class="jumbotron text-center" style="padding: 10 0 10">
			<h2>Reasoning</h2>
			<p><!-- Description --></p>
		</div>
		
		<div class="container">
			<#if project??>
				<#assign current_submenue = "Reason this Ontology">
				<#include "ProjectLinks.ftl">
			</#if>
			
			<div class="row">
				<#if errorMessage??>
					<div class="alert alert-danger">
						<strong>Error:</strong> ${errorMessage}
					</div>
				</#if>
			</div>
			
			<div class="row well">
				<p>
					Whenever you refere to an OWLEntity, add the shortform as a prefix.<br>
					e.g.: class <i>Example</i> in ontology "http://example.com/example_ontology" becomes "example_ontology:Example".
				</p>
			
				<form action="<#if project??>/project/${project.projectId}</#if>/reason" method="get" class="form" role="form">
					<div class="form-group row">
						<label for="ce" class="col-md-2">Class Expression:</label>
						<div class="col-md-6">
							<textarea name="ce" col="3" placeholder="Manchestersyntax" class="form-control"></textarea>
						</div>
					</div>
					
					<#if !project??>
						<div class="form-group row">
							<label for="ontologies" class="col-md-2">Ontologies:</label>
							<div class="col-md-6">
								<textarea name="ontologies" col="3" placeholder="comma separated list of project IDs" class="form-control"></textarea>
							</div>
						</div>
					</#if>
					
					<div class="form-group row">
						<div class="col-md-8">
							<input type="submit" class="btn btn-default pull-right">
						</div>
					</div>
				</form>
			</div>
		</div>
		
		<#include "Footer.ftl">
	</body>
</html>