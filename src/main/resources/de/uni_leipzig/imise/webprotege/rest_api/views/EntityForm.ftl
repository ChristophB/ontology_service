<#-- @ftlvariable name="" type="de.uni_leipzig.imise.webprotege.rest_api.views.EntityFormView" -->
<html>
	<head>
		<title>Query Entities | WebProt&#233;g&#233; REST-Interface</title>
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	</head>
	
	<body>
		<nav class="navbar navbar-default navbar-fixed-top">
			<div class="container">
				<ul class="nav navbar-nav">
					<li><a href="/">Documentation</a></li>
					<li><a href="/projects">Projects</a></li>
					<li class="active"><a href="/entity-form">Query Entities</a></li>
					<li><a href="/reason-form">Reasoning</a></li>
				</ul>
			</div>
		</nav>
		<div class="row" style="height: 50px; width:100%"></div>
		
		<div class="jumbotron text-center">
			<h2>Query Entities</h2>
			<p><!-- Description --></p>
		</div>
		
		<div class="container">
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