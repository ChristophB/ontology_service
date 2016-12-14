<#-- @ftlvariable name="" type="de.uni_leipzig.imise.webprotege.rest_api.views.EntityFormView" -->
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
			
			<div class="row">
				<form action="<#if project??>/project/${project.projectId}</#if>/entity" method="get" class="form" role="form">
					<div class="form-group row">
						<label for="type" class="col-md-2">Type:</label>
						<div class="col-md-3">
							<select name="type" class="form-control">
								<option value="entity">Entity</option>
								<option value="class">Class</option>
								<option value="individual">Individual</option>
							</select>
						</div>
					</div>
					
					<div class="form-group row">
						<label for="name" class="col-md-2">Entity Name:</label>
						<div class="col-md-3">
							<input type="text" name="name" placeholder="Name" class="form-control">
						</div>
					</div>
					
					<div class="form-group row">
						<label for="property" class="col-md-2">Property:</label>
						<div class="col-md-3">
							<input type="text" name="property" placeholder="Name" class="form-control">
						</div>
						<div class="col-md-3">
							<input type="text" name="value" placeholder="Value" class="form-control">
						</div>
					</div>
					
					<div class="form-group row">
						<label for="match" class="col-md-2">Match Method:</label>
						<div class="col-md-2">
							<select name="match" class="form-control">
								<option value="loose">loose</option>
								<option value="exact">exact</option>
							</select>
						</div>
					
						<label for="operator" class="col-md-2">Logical Operator:</label>
						<div class="col-md-2">
							<select name="operator" class="form-control">
								<option value="and">and</option>
								<option value="or">or</option>
							</select>
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
	</body>
</html>