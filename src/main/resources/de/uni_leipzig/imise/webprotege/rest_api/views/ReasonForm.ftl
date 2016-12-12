<#-- @ftlvariable name="" type="de.uni_leipzig.imise.webprotege.rest_api.views.ReasonFormView" -->
<html>
	<head>
		<title>Reasoning | WebProt&#233;g&#233; REST-Interface</title>
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	</head>
	
	<body>
		<nav class="navbar navbar-default navbar-fixed-top">
			<div class="container">
				<ul class="nav navbar-nav">
					<li><a href="/">Documentation</a></li>
					<li><a href="/projects">Projects</a></li>
					<li><a href="/entity-form">Query Entities</a></li>
					<li class="active"><a href="/reason-form">Reasoning</a></li>
				</ul>
			</div>
		</nav>
		<div class="row" style="height: 50px; width:100%"></div>
		
		<div class="jumbotron text-center">
			<h2>Reasoning</h2>
			<p><!-- Description --></p>
		</div>
		
		<div class="container">
			<div class="row">
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
	</body>
</html>