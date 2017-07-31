<#-- @ftlvariable name="" type="de.onto_med.webprotege_rest_api.views.ReasonFormView" -->
<#assign title = "Reasoning">
<#assign current = "Reasoning">

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
		
		<div class="jumbotron text-center" style="padding: 10 0 10">
			<h2>Reasoning</h2>
			<p><!-- Description --></p>
		</div>
		
		<div class="container">
			<div class="row">
				<#if errorMessage??>
					<div class="alert alert-danger">
						<strong>Error:</strong> ${errorMessage}
					</div>
				</#if>
			</div>
			
			<div class="row well">
				<form action="${rootPath}/reason" method="get" class="form" role="form">
					<div class="form-group row">
						<label for="ce" class="col-md-2">Class Expression:</label>
						<div class="col-md-6">
							<textarea name="ce" col="3" placeholder="Manchestersyntax" class="form-control"><#if ce??>${ce}</#if></textarea>
							<small class="form-text text-muted">
								Whenever you refere to an OWLEntity, add the shortform as a prefix.<br>
								e.g.: class <i>Example</i> in ontology "http://example.com/example_ontology" becomes "example_ontology:Example".
							</small>
						</div>
					</div>
					
					<div class="form-group row">
						<label for="ontologies" class="col-md-2">Ontologies:</label>
						<div class="col-md-6">
							<textarea name="ontologies" col="3" placeholder="comma separated list of project IDs/IRIs" class="form-control"><#if ontologies??>${ontologies}</#if></textarea>
						</div>
					</div>
					
					<div class="form-group row">
						<div class="col-md-8">
							<input type="submit" class="btn btn-default pull-right">
						</div>
					</div>
				</form>
			</div>
		</div>
		
		<#include "partials/Footer.ftl">
	</body>
</html>