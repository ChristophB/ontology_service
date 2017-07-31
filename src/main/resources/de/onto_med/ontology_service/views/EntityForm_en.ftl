<#-- @ftlvariable name="" type="de.onto_med.webprotege_rest_api.views.EntityFormView" -->
<#assign title = "Query Entities">
<#assign current = "Query Entities">

<html>
	<#include "Head.ftl">
	
	<body>
		<#include "Navbar.ftl">
		
		<div class="jumbotron text-center" style="padding: 10 0 10">
			<h2>Query Entities</h2>
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
				<form action="${rootPath}/entity" method="get" class="form" role="form">
					<div class="form-group row">
						<label for="type" class="col-md-2">Type:</label>
						<div class="col-md-3">
							<select name="type" class="form-control">
								<option value="entity">Entity</option>
								<option value="class" <#if type?? && type == "class">selected</#if>>Class</option>
								<option value="individual" <#if type?? && type == "individual">selected</#if>>Individual</option>
							</select>
						</div>
					</div>
					
					<div class="form-group row">
						<label for="name" class="col-md-2">Entity Name:</label>
						<div class="col-md-3">
							<input type="text" name="name" placeholder="Name" class="form-control" <#if name??>value="${name}"</#if>>
						</div>
					</div>
					
					<div class="form-group row">
						<label for="name" class="col-md-2">Entity IRI:</label>
						<div class="col-md-6">
							<input type="text" name="iri" placeholder="http://example.org/ontology#entity" class="form-control" <#if iri??>value="${iri}"</#if>>
						</div>
					</div>
					
					<div class="form-group row">
						<label for="property" class="col-md-2">Property:</label>
						<div class="col-md-3">
							<input type="text" name="property" placeholder="Name" class="form-control" <#if property??>value="${property}"</#if>>
						</div>
						<div class="col-md-3">
							<input type="text" name="value" placeholder="Value" class="form-control" <#if value??>value="${value}"</#if>>
						</div>
					</div>
					
					<div class="form-group row">
						<label for="match" class="col-md-2">Match Method:</label>
						<div class="col-md-2">
							<select name="match" class="form-control" title="Applied to 'IRI', 'name' and 'property'.">
								<option value="loose">loose</option>
								<option value="exact" <#if match?? && match == "exact">selected</#if>>exact</option>
							</select>
						</div>
					
						<label for="operator" class="col-md-2">Logical Operator:</label>
						<div class="col-md-2">
							<select name="operator" class="form-control" title="Applied to 'IRI', 'name' and 'property'.">
								<option value="or" <#if operator?? && operator == "or">selected</#if>>or</option>
								<option value="and">and</option>
							</select>
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
		
		<#include "Footer.ftl">
	</body>
</html>